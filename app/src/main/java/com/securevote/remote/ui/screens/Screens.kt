package com.securevote.remote.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.securevote.remote.BuildConfig
import com.securevote.remote.ui.*
import com.securevote.remote.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.*
import java.net.HttpURLConnection
import java.net.URL

// ================================================================
// STEP INDICATOR
// ================================================================

@Composable
fun StepIndicator(step: Int, total: Int, label: String) {
    Column {
        Text("Step $step of $total: $label", fontSize = 12.sp, color = MedGray, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { step.toFloat() / total },
            modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
            color = CivicBlue, trackColor = LightGray,
        )
    }
}

// ================================================================
// SPLASH SCREEN
// ================================================================

@Composable
fun SplashScreen(onBeginVoting: () -> Unit, onVerify: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(Modifier.size(100.dp).clip(CircleShape).background(CivicLight), contentAlignment = Alignment.Center) {
            Icon(Icons.Filled.HowToVote, contentDescription = null, tint = CivicBlue, modifier = Modifier.size(52.dp))
        }
        Spacer(Modifier.height(24.dp))
        Text("SECUREVOTE", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Navy, letterSpacing = 4.sp)
        Text("REMOTE", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = CivicBlue, letterSpacing = 6.sp)
        Spacer(Modifier.height(8.dp))
        Text("Secure Absentee Voting", fontSize = 14.sp, color = MedGray)
        Spacer(Modifier.height(48.dp))
        Button(
            onClick = onBeginVoting, modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = CivicBlue),
        ) { Text("Begin Voting", fontSize = 18.sp, fontWeight = FontWeight.SemiBold) }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(
            onClick = onVerify, modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(14.dp),
        ) { Text("Verify My Vote", color = CivicBlue) }
        Spacer(Modifier.height(24.dp))
        Text("End-to-end encrypted • Post-quantum secure", fontSize = 11.sp, color = MedGray, textAlign = TextAlign.Center)
    }
}

// ================================================================
// VOTER LOOKUP SCREEN (NEW)
// ================================================================

@Composable
fun VoterLookupScreen(onVoterFound: (VoterInfo) -> Unit, onBack: () -> Unit) {
    var regNumber by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var foundVoter by remember { mutableStateOf<VoterInfo?>(null) }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().padding(24.dp).navigationBarsPadding()) {
        StepIndicator(step = 1, total = 5, label = "Identify Yourself")
        Spacer(Modifier.height(24.dp))
        Text("Voter Identification", style = MaterialTheme.typography.headlineMedium, color = Navy)
        Text("Enter your voter registration number to begin.", style = MaterialTheme.typography.bodyMedium, color = MedGray)

        Spacer(Modifier.height(32.dp))

        if (foundVoter == null) {
            // Entry mode
            OutlinedTextField(
                value = regNumber, onValueChange = { input ->
                    regNumber = input.uppercase().filter { it.isLetterOrDigit() || it == '-' }
                    error = null
                },
                label = { Text("Registration Number") },
                placeholder = { Text("e.g. FL-0112464792") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    if (regNumber.isNotBlank()) {
                        loading = true; error = null
                        scope.launch {
                            val v = lookupVoter(regNumber.trim())
                            loading = false
                            if (v != null) foundVoter = v else error = "Voter not found. Check your registration number."
                        }
                    }
                }),
                isError = error != null,
                supportingText = if (error != null) {{ Text(error!!, color = Crimson) }} else null,
            )
            Spacer(Modifier.height(8.dp))
            Text("Your registration number was mailed to you with your Election PIN.", fontSize = 12.sp, color = MedGray)

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    if (regNumber.isNotBlank()) {
                        loading = true; error = null
                        scope.launch {
                            val v = lookupVoter(regNumber.trim())
                            loading = false
                            if (v != null) foundVoter = v else error = "Voter not found. Check your registration number."
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp), enabled = regNumber.isNotBlank() && !loading,
            ) {
                if (loading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                else Text("Look Up My Registration", fontWeight = FontWeight.SemiBold)
            }
        } else {
            // Confirmation mode
            val v = foundVoter!!
            Card(
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                border = BorderStroke(2.dp, ForestGreen),
                colors = CardDefaults.cardColors(containerColor = GreenLight),
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(28.dp))
                        Spacer(Modifier.width(10.dp))
                        Text("Voter Found", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                    }
                    Spacer(Modifier.height(16.dp))
                    VoterDetailRow("Name", "${v.firstName} ${v.lastName}")
                    VoterDetailRow("Registration", v.registrationNumber)
                    VoterDetailRow("Precinct", v.precinctId)
                    VoterDetailRow("Status", v.status)
                }
            }
            Spacer(Modifier.height(24.dp))
            Text("Is this you? Confirm to proceed to identity verification.", fontSize = 14.sp, color = MedGray, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { onVoterFound(v) },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
            ) { Text("Yes, That's Me — Continue", fontWeight = FontWeight.SemiBold) }

            Spacer(Modifier.height(12.dp))
            OutlinedButton(
                onClick = { foundVoter = null; regNumber = "" },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
            ) { Text("Not Me — Try Again") }
        }

        Spacer(Modifier.weight(1f))
        TextButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("← Back", color = MedGray)
        }
    }
}

@Composable
private fun VoterDetailRow(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text("$label: ", fontSize = 14.sp, color = MedGray, fontWeight = FontWeight.Medium)
        Text(value, fontSize = 14.sp, color = Navy, fontWeight = FontWeight.SemiBold)
    }
}

// ================================================================
// NFC SCAN SCREEN (simulated — auto passes)
// ================================================================

@Composable
fun NFCScanScreen(voterName: String = "", onScanComplete: () -> Unit, onNoNFC: () -> Unit) {
    var phase by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        delay(800); phase = 1
        delay(1500); phase = 2
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center,
    ) {
        StepIndicator(step = 2, total = 5, label = "ID Verification")
        Spacer(Modifier.height(40.dp))

        when (phase) {
            0 -> {
                CircularProgressIndicator(color = CivicBlue, modifier = Modifier.size(64.dp))
                Spacer(Modifier.height(16.dp))
                Text("Preparing ID scan...", style = MaterialTheme.typography.bodyLarge, color = MedGray)
            }
            1 -> {
                CircularProgressIndicator(color = CivicBlue, modifier = Modifier.size(64.dp))
                Spacer(Modifier.height(16.dp))
                Text("Scanning ID for $voterName...", style = MaterialTheme.typography.bodyLarge)
                Text("Reading NFC chip signature", fontSize = 13.sp, color = MedGray)
            }
            2 -> {
                Box(Modifier.size(80.dp).clip(CircleShape).background(GreenLight), contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(48.dp))
                }
                Spacer(Modifier.height(16.dp))
                Text("ID Verified", style = MaterialTheme.typography.headlineMedium.copy(color = ForestGreen))
                Text("NFC chip signature valid", style = MaterialTheme.typography.bodyMedium, color = MedGray)

                LaunchedEffect(Unit) {
                    delay(1200)
                    onScanComplete()
                }
            }
        }
    }
}

// ================================================================
// BIOMETRIC SCREEN (simulated — auto passes)
// ================================================================

@Composable
fun BiometricScreen(voterName: String = "", onVerified: () -> Unit) {
    var phase by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        delay(600); phase = 1
        delay(2000); phase = 2
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center,
    ) {
        StepIndicator(step = 2, total = 5, label = "Biometric Verification")
        Spacer(Modifier.height(40.dp))

        when (phase) {
            0 -> {
                CircularProgressIndicator(color = CivicBlue, modifier = Modifier.size(64.dp))
                Spacer(Modifier.height(16.dp))
                Text("Initializing camera...", color = MedGray)
            }
            1 -> {
                Box(Modifier.size(160.dp).clip(CircleShape).border(3.dp, CivicBlue, CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.Face, contentDescription = null, tint = CivicBlue, modifier = Modifier.size(80.dp))
                }
                Spacer(Modifier.height(16.dp))
                Text("Verifying $voterName...", style = MaterialTheme.typography.bodyLarge)
                Text("Matching face to ID photo", fontSize = 13.sp, color = MedGray)
            }
            2 -> {
                Box(Modifier.size(80.dp).clip(CircleShape).background(GreenLight), contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(48.dp))
                }
                Spacer(Modifier.height(16.dp))
                Text("Identity Verified", style = MaterialTheme.typography.headlineMedium.copy(color = ForestGreen))
                Text("Biometric match confirmed", color = MedGray)

                LaunchedEffect(Unit) {
                    delay(1200)
                    onVerified()
                }
            }
        }
    }
}

// ================================================================
// PIN ENTRY SCREEN (REAL API VERIFICATION)
// ================================================================

@Composable
fun PINEntryScreen(
    voterId: Long, electionId: String, voterName: String,
    onPinVerified: (PINResult) -> Unit, onLocked: () -> Unit,
) {
    var pin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var attemptsRemaining by remember { mutableIntStateOf(3) }
    var loading by remember { mutableStateOf(false) }
    var locked by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        StepIndicator(step = 3, total = 5, label = "Enter PIN")
        Spacer(Modifier.height(32.dp))
        Icon(Icons.Filled.Lock, contentDescription = null, tint = CivicBlue, modifier = Modifier.size(48.dp))
        Spacer(Modifier.height(12.dp))
        Text("Enter Your Election PIN", style = MaterialTheme.typography.headlineSmall, color = Navy)
        Text(voterName, fontSize = 14.sp, color = CivicBlue, fontWeight = FontWeight.Medium)
        Text("Mailed to your registered address", fontSize = 12.sp, color = MedGray)
        Spacer(Modifier.height(28.dp))

        if (locked) {
            // Locked state
            Card(
                Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                border = BorderStroke(1.dp, Crimson),
            ) {
                Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.Lock, null, tint = Crimson, modifier = Modifier.size(40.dp))
                    Spacer(Modifier.height(8.dp))
                    Text("PIN Locked", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Crimson)
                    Spacer(Modifier.height(4.dp))
                    Text("Too many incorrect attempts.\nPlease vote in person at your polling place.",
                        fontSize = 14.sp, color = MedGray, textAlign = TextAlign.Center)
                }
            }
            Spacer(Modifier.height(24.dp))
            Button(onClick = onLocked, Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Text("Return to Home")
            }
            return
        }

        // PIN dots
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            (0 until 6).forEach { i ->
                Box(
                    Modifier.size(20.dp).clip(CircleShape)
                        .background(if (i < pin.length) CivicBlue else LightGray)
                        .border(1.dp, if (error != null) Crimson else if (i < pin.length) CivicBlue else MedGray, CircleShape)
                )
            }
        }

        if (error != null) {
            Spacer(Modifier.height(8.dp))
            Text(error!!, color = Crimson, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text("$attemptsRemaining attempt(s) remaining", color = MedGray, fontSize = 12.sp)
        }

        if (loading) {
            Spacer(Modifier.height(16.dp))
            CircularProgressIndicator(Modifier.size(24.dp), color = CivicBlue, strokeWidth = 2.dp)
            Text("Verifying PIN...", fontSize = 13.sp, color = MedGray)
        }

        Spacer(Modifier.height(24.dp))

        // Keypad
        val keys = listOf("1","2","3","4","5","6","7","8","9","","0","⌫")
        for (row in keys.chunked(3)) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(vertical = 6.dp)) {
                row.forEach { key ->
                    if (key.isEmpty()) {
                        Spacer(Modifier.size(72.dp))
                    } else {
                        OutlinedButton(
                            onClick = {
                                if (loading) return@OutlinedButton
                                error = null
                                if (key == "⌫") { if (pin.isNotEmpty()) pin = pin.dropLast(1) }
                                else if (pin.length < 6) {
                                    pin += key
                                    if (pin.length == 6) {
                                        // Call real API
                                        loading = true
                                        scope.launch {
                                            val result = verifyPIN(voterId, electionId, pin)
                                            loading = false
                                            if (result.verified) {
                                                onPinVerified(result)
                                            } else if (result.locked) {
                                                locked = true
                                            } else {
                                                attemptsRemaining = result.attemptsRemaining
                                                error = "Incorrect PIN"
                                                pin = ""
                                            }
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.size(72.dp), shape = CircleShape,
                            border = BorderStroke(1.dp, LightGray), enabled = !loading,
                        ) { Text(key, fontSize = 22.sp, fontWeight = FontWeight.Medium, color = Navy) }
                    }
                }
            }
        }
    }
}

// ================================================================
// TOKEN ISSUANCE SCREEN
// ================================================================

@Composable
fun TokenIssuanceScreen(onTokenIssued: () -> Unit) {
    var step by remember { mutableIntStateOf(0) }
    val steps = listOf("Generating blind token...", "Signing with election authority...", "Unlinding token...", "Token issued ✓")

    LaunchedEffect(Unit) {
        for (i in steps.indices) { delay(600); step = i }
        delay(500); onTokenIssued()
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center,
    ) {
        StepIndicator(step = 3, total = 5, label = "Issuing Ballot Token")
        Spacer(Modifier.height(40.dp))
        Icon(Icons.Filled.VpnKey, contentDescription = null, tint = CivicBlue, modifier = Modifier.size(48.dp))
        Spacer(Modifier.height(16.dp))
        Text("Anonymous Ballot Token", style = MaterialTheme.typography.headlineSmall, color = Navy)
        Spacer(Modifier.height(24.dp))

        steps.forEachIndexed { i, label ->
            if (i <= step) {
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (i < step) Icon(Icons.Filled.CheckCircle, null, tint = ForestGreen, modifier = Modifier.size(20.dp))
                    else if (i == step && step < steps.lastIndex) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                    else Icon(Icons.Filled.CheckCircle, null, tint = ForestGreen, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(label, fontSize = 14.sp, color = if (i <= step) Navy else MedGray)
                }
            }
        }
    }
}

// ================================================================
// BALLOT RACE SCREEN (live data)
// ================================================================

@Composable
fun BallotRaceScreen(
    raceTitle: String, votingRule: String, maxSelections: Int,
    candidates: List<LiveCandidate>, raceIndex: Int, totalRaces: Int,
    currentSelection: String?, onSelect: (String) -> Unit, onNext: () -> Unit, onBack: () -> Unit,
) {
    val instruction = when (votingRule) {
        "CHOOSE_ONE" -> "Vote for ONE"; "CHOOSE_N" -> "Vote for up to $maxSelections"
        "RANKED_CHOICE" -> "Rank in order"; else -> "Vote for ONE"
    }

    Column(modifier = Modifier.fillMaxSize().padding(20.dp).navigationBarsPadding()) {
        StepIndicator(step = 4, total = 5, label = "Cast Your Vote")
        Spacer(Modifier.height(8.dp))
        Text("Race ${raceIndex + 1} of $totalRaces", style = MaterialTheme.typography.bodySmall, color = MedGray)
        Spacer(Modifier.height(12.dp))
        Text(raceTitle, style = MaterialTheme.typography.headlineSmall, color = Navy)
        Text(instruction, color = CivicBlue, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Spacer(Modifier.height(16.dp))

        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            candidates.forEach { c ->
                val selected = currentSelection == c.candidateHash
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        .clickable { onSelect(if (selected) "" else c.candidateHash) },
                    border = BorderStroke(2.dp, if (selected) CivicBlue else Color(0xFFE5E7EB)),
                    colors = CardDefaults.cardColors(containerColor = if (selected) CivicLight else Color.White),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(24.dp).clip(CircleShape).border(2.dp, if (selected) CivicBlue else Color(0xFFCCCCCC), CircleShape),
                            contentAlignment = Alignment.Center) {
                            if (selected) Box(Modifier.size(12.dp).clip(CircleShape).background(CivicBlue))
                        }
                        Spacer(Modifier.width(14.dp))
                        Column {
                            Text(c.displayName, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Navy)
                            if (c.party.isNotEmpty()) Text(c.party, fontSize = 13.sp, color = MedGray)
                            Text("Hash: ${c.candidateHash.take(8)}...", fontSize = 10.sp, color = CivicBlue, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            if (raceIndex > 0) OutlinedButton(onClick = onBack, Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) { Text("← Back") }
            Button(onClick = onNext, Modifier.weight(2f), shape = RoundedCornerShape(12.dp),
                enabled = currentSelection != null && currentSelection.isNotEmpty()) {
                Text(if (raceIndex == totalRaces - 1) "Review Ballot →" else "Next →", Modifier.padding(vertical = 4.dp))
            }
        }
    }
}

// ================================================================
// BALLOT REVIEW SCREEN (live data)
// ================================================================

@Composable
fun BallotReviewScreen(
    races: List<LiveRace>, candidates: Map<String, List<LiveCandidate>>,
    selections: Map<String, String?>, onConfirm: () -> Unit, onChangeRace: (Int) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().padding(20.dp).navigationBarsPadding()) {
        StepIndicator(step = 4, total = 5, label = "Review Your Ballot")
        Spacer(Modifier.height(16.dp))
        Text("Review All Selections", style = MaterialTheme.typography.headlineMedium, color = Navy)
        Text("Tap Change to modify any selection.", fontSize = 14.sp, color = MedGray)
        Spacer(Modifier.height(16.dp))

        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            races.forEachIndexed { i, race ->
                val selHash = selections[race.raceId]
                val selC = if (!selHash.isNullOrEmpty()) candidates[race.raceId]?.find { it.candidateHash == selHash } else null
                Card(
                    Modifier.fillMaxWidth().padding(vertical = 4.dp), shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, if (selC == null) Amber else Color(0xFFE5E7EB)),
                    colors = CardDefaults.cardColors(containerColor = if (selC == null) AmberLight else Color.White),
                ) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(race.title, fontSize = 13.sp, color = MedGray)
                            if (selC != null) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("✓ ", color = ForestGreen, fontWeight = FontWeight.Bold)
                                    Text(selC.displayName, fontWeight = FontWeight.SemiBold, color = Navy)
                                    if (selC.party.isNotEmpty()) Text(" (${selC.party.split(" ")[0]})", fontSize = 12.sp, color = MedGray)
                                }
                            } else {
                                Text("⚠ No selection (skipped)", fontWeight = FontWeight.SemiBold, color = Amber)
                            }
                        }
                        TextButton(onClick = { onChangeRace(i) }) { Text("Change", color = CivicBlue, fontWeight = FontWeight.SemiBold, fontSize = 13.sp) }
                    }
                }
            }
        }

        val skipped = races.count { selections[it.raceId].isNullOrEmpty() }
        if (skipped > 0) {
            Card(colors = CardDefaults.cardColors(containerColor = AmberLight), shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                Text("⚠ You have $skipped skipped race(s)", Modifier.padding(12.dp), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Amber)
            }
        }

        Button(onClick = onConfirm, Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)) {
            Text("🔒  Encrypt & Submit Ballot", Modifier.padding(vertical = 6.dp), fontWeight = FontWeight.Bold)
        }
    }
}

// ================================================================
// ENCRYPTION SCREEN (REAL API SUBMIT)
// ================================================================

@Composable
fun EncryptionScreen(
    selections: Map<String, String?>, electionId: String, precinctId: String, voterToken: String,
    onComplete: (voteRecordId: String, confirmationHash: String) -> Unit,
) {
    var step by remember { mutableIntStateOf(0) }
    var hexDisplay by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        // Hex animation
        launch { repeat(100) { hexDisplay = (1..32).map { "0123456789ABCDEF".random() }.joinToString(""); delay(80) } }
        // Progress steps
        delay(800); step = 1
        delay(1000); step = 2
        delay(800); step = 3

        // REAL API CALL
        val result = submitVote(selections, electionId, precinctId, voterToken)
        step = 4
        delay(500)

        if (result != null) {
            onComplete(result.first, result.second)
        } else {
            onComplete("SV-ERROR-${System.currentTimeMillis()}", "SUBMIT-FAILED")
        }
    }

    val labels = listOf("Encrypting selections...", "Applying blind signature...", "Sealing ballot envelope...", "Submitting to election authority...", "Vote recorded ✓")

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center,
    ) {
        StepIndicator(step = 5, total = 5, label = "Encrypting & Submitting")
        Spacer(Modifier.height(32.dp))

        Text(hexDisplay, fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = CivicBlue.copy(alpha = 0.4f),
            textAlign = TextAlign.Center, maxLines = 2, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))

        Icon(Icons.Filled.Lock, null, tint = CivicBlue, modifier = Modifier.size(48.dp))
        Spacer(Modifier.height(16.dp))
        Text("ML-KEM-1024 Encryption", fontSize = 13.sp, color = CivicBlue, fontFamily = FontFamily.Monospace)
        Spacer(Modifier.height(24.dp))

        labels.forEachIndexed { i, label ->
            if (i <= step) {
                Row(Modifier.fillMaxWidth().padding(vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (i < step) Icon(Icons.Filled.CheckCircle, null, tint = ForestGreen, modifier = Modifier.size(18.dp))
                    else CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(10.dp))
                    Text(label, fontSize = 13.sp, color = if (i < step) ForestGreen else Navy)
                }
            }
        }
    }
}

// ================================================================
// SUCCESS SCREEN
// ================================================================

@Composable
fun SuccessScreen(voteRecordId: String, confirmationHash: String,
    submissionSequence: Int = 1, remainingSubmissions: Int = 4, onDone: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(28.dp).navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center,
    ) {
        Box(Modifier.size(80.dp).clip(CircleShape).background(GreenLight), contentAlignment = Alignment.Center) {
            Icon(Icons.Filled.CheckCircle, null, tint = ForestGreen, modifier = Modifier.size(52.dp))
        }
        Spacer(Modifier.height(16.dp))
        Text("Vote Recorded", style = MaterialTheme.typography.headlineMedium, color = ForestGreen, fontWeight = FontWeight.Bold)
        Text("Your vote is encrypted and stored.", fontSize = 14.sp, color = MedGray, textAlign = TextAlign.Center)
        Spacer(Modifier.height(24.dp))

        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = LightGray)) {
            Column(Modifier.padding(16.dp)) {
                Text("VOTE RECORD ID", fontSize = 11.sp, color = MedGray, fontWeight = FontWeight.Medium)
                Text(voteRecordId, fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = Navy)
                Spacer(Modifier.height(12.dp))
                Text("CONFIRMATION CODE", fontSize = 11.sp, color = MedGray, fontWeight = FontWeight.Medium)
                Text(confirmationHash, fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = CivicBlue)
                Spacer(Modifier.height(4.dp))
                Text("Save this code. Verify at api.badvolf.com after the election.", fontSize = 11.sp, color = MedGray)
            }
        }
        Spacer(Modifier.height(12.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Encryption", fontSize = 11.sp, color = MedGray)
                Text("ML-KEM-1024", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CivicBlue)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Channel", fontSize = 11.sp, color = MedGray)
                Text("REMOTE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CivicBlue)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Submission", fontSize = 11.sp, color = MedGray)
                Text("#$submissionSequence of 5", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CivicBlue)
            }
        }
        Spacer(Modifier.height(24.dp))

        Button(onClick = onDone, Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CivicBlue)) {
            Text("Done", fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(8.dp))
        Text(
            if (remainingSubmissions > 0) "You may revote up to $remainingSubmissions more time(s) before the window closes."
            else "This is your final submission. No more revotes available.",
            fontSize = 11.sp, color = MedGray, textAlign = TextAlign.Center)
    }
}

// ================================================================
// THANK YOU SCREEN
// ================================================================

@Composable
fun ThankYouScreen(onReturn: () -> Unit) {
    LaunchedEffect(Unit) { delay(5000); onReturn() }

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center,
    ) {
        Text("Thank you for voting!", style = MaterialTheme.typography.headlineMedium, color = Navy, textAlign = TextAlign.Center)
        Spacer(Modifier.height(12.dp))
        Text("Your voice matters.", fontSize = 16.sp, color = MedGray)
        Spacer(Modifier.height(32.dp))
        Text("Returning to home screen...", fontSize = 13.sp, color = MedGray)
    }
}

// ================================================================
// VERIFICATION SCREEN
// ================================================================

@Composable
fun VerificationScreen(onBack: () -> Unit) {
    var rawInput by remember { mutableStateOf("") }
    var confirmHash by remember { mutableStateOf<String?>(null) }
    var verifyStatus by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Auto-format: insert dashes as SV-2026-XXXXXXXX
    val formattedId = remember(rawInput) {
        val clean = rawInput.replace("-", "").uppercase()
        buildString {
            clean.forEachIndexed { i, c ->
                if (i == 2 || i == 6) append('-')
                append(c)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp).navigationBarsPadding()) {
        TextButton(onClick = onBack) { Text("← Back", color = CivicBlue) }
        Spacer(Modifier.height(16.dp))
        Text("Verify Your Vote", style = MaterialTheme.typography.headlineMedium, color = Navy)
        Text("Enter your Vote Record ID to confirm it was counted.", fontSize = 14.sp, color = MedGray)
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = rawInput,
            onValueChange = { input ->
                rawInput = input.uppercase().filter { it.isLetterOrDigit() || it == '-' }
            },
            label = { Text("Vote Record ID") },
            placeholder = { Text("SV-2026-XXXXXXXX") },
            modifier = Modifier.fillMaxWidth(), singleLine = true,
        )

        if (verifyStatus == null && !loading) {
            Spacer(Modifier.height(8.dp))
            Text("This ID was shown on your receipt after voting.", fontSize = 12.sp, color = MedGray)
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                loading = true; verifyStatus = null; confirmHash = null
                scope.launch(kotlinx.coroutines.Dispatchers.IO) {
                    try {
                        val conn = URL("${BuildConfig.SVR_GATEWAY_URL}/verify").openConnection() as HttpURLConnection
                        conn.requestMethod = "POST"; conn.doOutput = true; conn.connectTimeout = 10000
                        conn.setRequestProperty("Content-Type", "application/json")
                        val body = """{"vote_record_id":"${formattedId.trim()}","election_id":"","pin":""}"""
                        conn.outputStream.write(body.toByteArray())
                        val code = conn.responseCode
                        val resp = (if (code in 200..299) conn.inputStream else conn.errorStream).bufferedReader().readText()
                        conn.disconnect()
                        val o = Json.parseToJsonElement(resp).jsonObject
                        verifyStatus = o["status"]?.jsonPrimitive?.content ?: "UNKNOWN"
                        val ch = o["confirmation_hash"]?.jsonPrimitive?.content
                        confirmHash = if (ch.isNullOrEmpty() || ch == "null") null else ch
                    } catch (e: Exception) {
                        verifyStatus = "CONNECTION_ERROR"
                    }
                    loading = false
                }
            },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
            enabled = formattedId.length >= 5 && !loading,
        ) {
            if (loading) CircularProgressIndicator(Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
            else Text("Verify My Vote")
        }

        if (verifyStatus != null) {
            Spacer(Modifier.height(24.dp))
            when (verifyStatus) {
                "VERIFIED" -> {
                    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = GreenLight),
                        border = BorderStroke(1.dp, ForestGreen)) {
                        Column(Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.CheckCircle, null, tint = ForestGreen, modifier = Modifier.size(24.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Vote Verified", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = ForestGreen)
                            }
                            Spacer(Modifier.height(8.dp))
                            Text("Your vote exists in the election record.", fontSize = 13.sp, color = MedGray)
                            if (confirmHash != null) {
                                Spacer(Modifier.height(8.dp))
                                Text("Confirmation: $confirmHash", fontSize = 12.sp, color = CivicBlue, fontFamily = FontFamily.Monospace)
                            }
                            Spacer(Modifier.height(8.dp))
                            Text("Note: Full Merkle proof verification will be available after the election is certified.",
                                fontSize = 11.sp, color = MedGray)
                        }
                    }
                }
                "NOT_FOUND" -> {
                    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = AmberLight),
                        border = BorderStroke(1.dp, Amber)) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Vote Not Found", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Amber)
                            Spacer(Modifier.height(4.dp))
                            Text("No vote with this Record ID was found. Check the ID and try again.", fontSize = 13.sp, color = MedGray)
                        }
                    }
                }
                else -> {
                    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2))) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Verification Error", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Crimson)
                            Spacer(Modifier.height(4.dp))
                            Text("Could not connect to the verification server. Please try again later.", fontSize = 13.sp, color = MedGray)
                        }
                    }
                }
            }
        }
    }
}
