package com.securevote.remote.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.securevote.remote.BuildConfig
import com.securevote.remote.navigation.SVRRoute
import com.securevote.remote.ui.screens.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID

// ---- Data Classes ----

data class LiveRace(val raceId: String, val title: String, val votingRule: String, val maxSelections: Int, val displayOrder: Int)
data class LiveCandidate(val candidateId: Long, val displayName: String, val party: String, val candidateHash: String, val displayOrder: Int)
data class LiveBDF(val electionId: String, val precinctId: String, val races: List<LiveRace>, val candidates: Map<String, List<LiveCandidate>>)
data class VoterInfo(val voterId: Long, val firstName: String, val lastName: String, val registrationNumber: String, val precinctId: String, val status: String)

// ---- API Calls ----

private val BASE = BuildConfig.SVR_GATEWAY_URL

suspend fun fetchBDF(): LiveBDF? = withContext(Dispatchers.IO) {
    try {
        val conn = URL("$BASE/ballot/bdf").openConnection() as HttpURLConnection
        conn.connectTimeout = 10000; conn.readTimeout = 10000
        if (conn.responseCode != 200) return@withContext null
        val json = conn.inputStream.bufferedReader().readText(); conn.disconnect()
        val root = Json.parseToJsonElement(json).jsonObject
        val races = (root["races"]?.jsonArray ?: return@withContext null).map { r ->
            val o = r.jsonObject
            LiveRace(o["race_id"]!!.jsonPrimitive.content, o["title"]!!.jsonPrimitive.content,
                o["voting_rule"]?.jsonPrimitive?.content ?: "CHOOSE_ONE",
                o["max_selections"]?.jsonPrimitive?.int ?: 1, o["display_order"]?.jsonPrimitive?.int ?: 0)
        }.sortedBy { it.displayOrder }
        val candidates = mutableMapOf<String, List<LiveCandidate>>()
        for ((raceId, arr) in root["candidates"]!!.jsonObject) {
            candidates[raceId] = arr.jsonArray.map { c ->
                val o = c.jsonObject
                LiveCandidate(o["candidate_id"]!!.jsonPrimitive.long, o["display_name"]!!.jsonPrimitive.content,
                    o["party"]?.jsonPrimitive?.content ?: "", o["candidate_hash"]!!.jsonPrimitive.content,
                    o["display_order"]?.jsonPrimitive?.int ?: 0)
            }.sortedBy { it.displayOrder }
        }
        LiveBDF(root["election_id"]!!.jsonPrimitive.content, root["precinct_id"]!!.jsonPrimitive.content, races, candidates)
    } catch (e: Exception) { e.printStackTrace(); null }
}

suspend fun lookupVoter(regNumber: String): VoterInfo? = withContext(Dispatchers.IO) {
    try {
        val encoded = java.net.URLEncoder.encode(regNumber, "UTF-8")
        val conn = URL("$BASE/voter/lookup?registration_number=$encoded").openConnection() as HttpURLConnection
        conn.connectTimeout = 10000; conn.readTimeout = 10000
        if (conn.responseCode != 200) return@withContext null
        val json = conn.inputStream.bufferedReader().readText(); conn.disconnect()
        val o = Json.parseToJsonElement(json).jsonObject
        VoterInfo(
            o["voter_id"]!!.jsonPrimitive.long, o["first_name"]!!.jsonPrimitive.content,
            o["last_name"]!!.jsonPrimitive.content, o["registration_number"]!!.jsonPrimitive.content,
            o["precinct_id"]!!.jsonPrimitive.content, o["status"]!!.jsonPrimitive.content)
    } catch (e: Exception) { e.printStackTrace(); null }
}

data class PINResult(val verified: Boolean, val attemptsRemaining: Int, val locked: Boolean,
    val sessionToken: String?, val voterToken: String?, val submissionSequence: Int, val remainingSubmissions: Int)

suspend fun verifyPIN(voterId: Long, electionId: String, pin: String): PINResult = withContext(Dispatchers.IO) {
    try {
        val body = buildJsonObject {
            put("voter_id", voterId)
            put("election_id", electionId)
            put("pin", pin)
        }
        val conn = URL("$BASE/auth/verify-pin").openConnection() as HttpURLConnection
        conn.requestMethod = "POST"; conn.doOutput = true; conn.connectTimeout = 10000; conn.readTimeout = 10000
        conn.setRequestProperty("Content-Type", "application/json")
        conn.outputStream.write(body.toString().toByteArray()); conn.outputStream.flush()

        val code = conn.responseCode
        val resp = (if (code in 200..299) conn.inputStream else conn.errorStream).bufferedReader().readText()
        conn.disconnect()

        val o = Json.parseToJsonElement(resp).jsonObject
        val verified = o["verified"]?.jsonPrimitive?.boolean ?: false
        val remaining = o["attempts_remaining"]?.jsonPrimitive?.int ?: 3
        val locked = o["locked"]?.jsonPrimitive?.boolean ?: false
        val token = o["session_token"]?.jsonPrimitive?.content
        val vToken = o["voter_token"]?.jsonPrimitive?.content
        val seq = o["submission_sequence"]?.jsonPrimitive?.int ?: 1
        val remSub = o["remaining_submissions"]?.jsonPrimitive?.int ?: 5

        PINResult(verified, remaining, locked, token, vToken, seq, remSub)
    } catch (e: Exception) {
        e.printStackTrace()
        PINResult(false, 3, false, null, null, 1, 5)
    }
}

suspend fun submitVote(
    selections: Map<String, String?>, electionId: String, precinctId: String, voterToken: String
): Triple<String, String, String>? = withContext(Dispatchers.IO) {
    try {
        val voteRecordId = "SV-${java.time.LocalDate.now().year}-${UUID.randomUUID().toString().take(8).uppercase()}"
        val selectionsJson = buildJsonObject {
            for ((raceId, hash) in selections) { if (hash != null) put(raceId, JsonPrimitive(hash)) }
        }
        val confirmationHash = java.security.MessageDigest.getInstance("SHA-256")
            .digest((voteRecordId + selectionsJson.toString()).toByteArray())
            .joinToString("") { "%02x".format(it) }.take(16).uppercase()
            .chunked(4).joinToString("-")
        val nonce = UUID.randomUUID().toString().replace("-", "")

        val body = buildJsonObject {
            put("vote_record_id", voteRecordId)
            put("voter_token", voterToken)
            put("encrypted_ballot", selectionsJson.toString())
            put("confirmation_hash", confirmationHash)
            put("nonce", nonce)
            put("biometric_hash", nonce.take(64))
            put("device_attestation", "android-demo")
            put("submission_sequence", 1)
            put("election_id", electionId)
            put("precinct_id", precinctId)
        }

        val conn = URL("$BASE/ballot/submit").openConnection() as HttpURLConnection
        conn.requestMethod = "POST"; conn.doOutput = true; conn.connectTimeout = 15000; conn.readTimeout = 15000
        conn.setRequestProperty("Content-Type", "application/json")
        conn.outputStream.write(body.toString().toByteArray()); conn.outputStream.flush()

        val code = conn.responseCode
        val resp = (if (code in 200..299) conn.inputStream else conn.errorStream).bufferedReader().readText()
        conn.disconnect()

        if (code in 200..299) {
            val r = Json.parseToJsonElement(resp).jsonObject
            val serverSig = r["server_signature"]?.jsonPrimitive?.content ?: ""
            Triple(voteRecordId, confirmationHash, serverSig)
        } else { println("Submit failed: $code $resp"); null }
    } catch (e: Exception) { e.printStackTrace(); null }
}

// ---- Main App ----

@Composable
fun SVRApp() {
    val navController = rememberNavController()

    // State
    var bdf by remember { mutableStateOf<LiveBDF?>(null) }
    var voter by remember { mutableStateOf<VoterInfo?>(null) }
    var selections by remember { mutableStateOf(mapOf<String, String?>()) }
    var voteRecordId by remember { mutableStateOf("") }
    var confirmationHash by remember { mutableStateOf("") }
    var voterToken by remember { mutableStateOf("") }
    var submissionSequence by remember { mutableIntStateOf(1) }
    var remainingSubmissions by remember { mutableIntStateOf(5) }

    // Fetch BDF on launch
    LaunchedEffect(Unit) {
        val result = fetchBDF()
        if (result != null) {
            bdf = result
            selections = result.races.associate { it.raceId to null }
        }
    }

    val totalRaces = bdf?.races?.size ?: 0

    NavHost(
        navController = navController, startDestination = SVRRoute.Splash.route,
        enterTransition = { slideInHorizontally(tween(300)) { it } + fadeIn(tween(300)) },
        exitTransition = { slideOutHorizontally(tween(300)) { -it } + fadeOut(tween(300)) },
        popEnterTransition = { slideInHorizontally(tween(300)) { -it } + fadeIn(tween(300)) },
        popExitTransition = { slideOutHorizontally(tween(300)) { it } + fadeOut(tween(300)) },
    ) {
        // ---- Splash ----
        composable(SVRRoute.Splash.route) {
            SplashScreen(
                onBeginVoting = { navController.navigate(SVRRoute.VoterLookup.route) { popUpTo(SVRRoute.Splash.route) { inclusive = true } } },
                onVerify = { navController.navigate(SVRRoute.Verify.route) }
            )
        }

        // ---- Voter Lookup (NEW) ----
        composable(SVRRoute.VoterLookup.route) {
            VoterLookupScreen(
                onVoterFound = { v ->
                    voter = v
                    voterToken = "token-${v.voterId}-${UUID.randomUUID().toString().take(8)}"
                    navController.navigate(SVRRoute.NFCScan.route)
                },
                onBack = { navController.popBackStack() }
            )
        }

        // ---- NFC Scan (simulated) ----
        composable(SVRRoute.NFCScan.route) {
            NFCScanScreen(
                voterName = voter?.let { "${it.firstName} ${it.lastName}" } ?: "",
                onScanComplete = { navController.navigate(SVRRoute.Biometric.route) },
                onNoNFC = { navController.navigate(SVRRoute.Biometric.route) }
            )
        }

        // ---- Biometric (simulated) ----
        composable(SVRRoute.Biometric.route) {
            BiometricScreen(
                voterName = voter?.let { "${it.firstName} ${it.lastName}" } ?: "",
                onVerified = { navController.navigate(SVRRoute.PinEntry.route) }
            )
        }

        // ---- PIN Entry (REAL API VERIFICATION) ----
        composable(SVRRoute.PinEntry.route) {
            val currentVoter = voter
            val electionId = bdf?.electionId ?: "general-2026-11-03"
            PINEntryScreen(
                voterId = currentVoter?.voterId ?: 0,
                electionId = electionId,
                voterName = currentVoter?.let { "${it.firstName} ${it.lastName}" } ?: "",
                onPinVerified = { result ->
                    if (result.voterToken != null) voterToken = result.voterToken
                    submissionSequence = result.submissionSequence
                    remainingSubmissions = result.remainingSubmissions
                    navController.navigate(SVRRoute.TokenIssuance.route)
                },
                onLocked = {
                    // PIN locked — go back to splash
                    navController.navigate(SVRRoute.Splash.route) { popUpTo(0) { inclusive = true } }
                }
            )
        }

        // ---- Token Issuance ----
        composable(SVRRoute.TokenIssuance.route) {
            TokenIssuanceScreen(onTokenIssued = {
                navController.navigate(SVRRoute.Ballot.createRoute(0)) {
                    popUpTo(SVRRoute.TokenIssuance.route) { inclusive = true }
                }
            })
        }

        // ---- Ballot (LIVE) ----
        composable(
            route = SVRRoute.Ballot.route,
            arguments = listOf(navArgument("raceIndex") { type = NavType.IntType })
        ) { entry ->
            val raceIndex = entry.arguments?.getInt("raceIndex") ?: 0
            val currentBDF = bdf
            if (currentBDF != null && raceIndex < currentBDF.races.size) {
                val race = currentBDF.races[raceIndex]
                BallotRaceScreen(
                    raceTitle = race.title, votingRule = race.votingRule, maxSelections = race.maxSelections,
                    candidates = currentBDF.candidates[race.raceId] ?: emptyList(),
                    raceIndex = raceIndex, totalRaces = totalRaces,
                    currentSelection = selections[race.raceId],
                    onSelect = { hash -> selections = selections.toMutableMap().also { it[race.raceId] = hash } },
                    onNext = {
                        if (raceIndex < totalRaces - 1) navController.navigate(SVRRoute.Ballot.createRoute(raceIndex + 1))
                        else navController.navigate(SVRRoute.BallotReview.route)
                    },
                    onBack = { navController.popBackStack() }
                )
            }
        }

        // ---- Review (LIVE) ----
        composable(SVRRoute.BallotReview.route) {
            val currentBDF = bdf
            if (currentBDF != null) {
                BallotReviewScreen(
                    races = currentBDF.races, candidates = currentBDF.candidates, selections = selections,
                    onConfirm = { navController.navigate(SVRRoute.Encrypting.route) { popUpTo(SVRRoute.BallotReview.route) { inclusive = true } } },
                    onChangeRace = { navController.navigate(SVRRoute.Ballot.createRoute(it)) }
                )
            }
        }

        // ---- Encryption + REAL SUBMIT ----
        composable(SVRRoute.Encrypting.route) {
            EncryptionScreen(
                selections = selections,
                electionId = bdf?.electionId ?: "",
                precinctId = bdf?.precinctId ?: "1042",
                voterToken = voterToken,
                onComplete = { id, hash ->
                    voteRecordId = id; confirmationHash = hash
                    navController.navigate(SVRRoute.Success.route) { popUpTo(SVRRoute.Encrypting.route) { inclusive = true } }
                }
            )
        }

        // ---- Success ----
        composable(SVRRoute.Success.route) {
            SuccessScreen(voteRecordId = voteRecordId, confirmationHash = confirmationHash,
                submissionSequence = submissionSequence, remainingSubmissions = 5 - submissionSequence,
                onDone = { navController.navigate(SVRRoute.ThankYou.route) { popUpTo(0) { inclusive = true } } })
        }

        // ---- Thank You ----
        composable(SVRRoute.ThankYou.route) {
            ThankYouScreen(onReturn = { navController.navigate(SVRRoute.Splash.route) { popUpTo(0) { inclusive = true } } })
        }

        // ---- Verification ----
        composable(SVRRoute.Verify.route) {
            VerificationScreen(onBack = { navController.popBackStack() })
        }
    }
}
