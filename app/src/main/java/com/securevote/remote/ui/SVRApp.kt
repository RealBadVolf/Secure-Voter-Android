package com.securevote.remote.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.securevote.remote.navigation.SVRRoute
import com.securevote.remote.ui.screens.*

/**
 * Root composable for the SecureVote Remote app.
 * Manages navigation between all screens in the voting flow.
 *
 * Flow:
 *   Splash → NFC Scan → Biometric → (IPVC QR fallback if bio fails)
 *   → PIN Entry → Token Issuance → Ballot (per race) → Review
 *   → Encryption → Success → Thank You
 */
@Composable
fun SVRApp() {
    val navController = rememberNavController()

    // Shared state that persists across navigation
    var selections by remember { mutableStateOf(mutableMapOf<String, String?>()) }
    var voteRecordId by remember { mutableStateOf("") }
    var confirmationHash by remember { mutableStateOf("") }
    var totalRaces by remember { mutableIntStateOf(4) }

    NavHost(
        navController = navController,
        startDestination = SVRRoute.Splash.route,
        enterTransition = { slideInHorizontally(tween(300)) { it } + fadeIn(tween(300)) },
        exitTransition = { slideOutHorizontally(tween(300)) { -it } + fadeOut(tween(300)) },
        popEnterTransition = { slideInHorizontally(tween(300)) { -it } + fadeIn(tween(300)) },
        popExitTransition = { slideOutHorizontally(tween(300)) { it } + fadeOut(tween(300)) },
    ) {
        // ---- Splash ----
        composable(SVRRoute.Splash.route) {
            SplashScreen(
                onBeginVoting = {
                    navController.navigate(SVRRoute.NFCScan.route) {
                        popUpTo(SVRRoute.Splash.route) { inclusive = true }
                    }
                },
                onVerify = {
                    navController.navigate(SVRRoute.Verify.route)
                }
            )
        }

        // ---- NFC ID Scan ----
        composable(SVRRoute.NFCScan.route) {
            NFCScanScreen(
                onScanComplete = {
                    navController.navigate(SVRRoute.Biometric.route)
                },
                onNoNFC = {
                    // Fallback to optical scan — still proceeds to biometric
                    navController.navigate(SVRRoute.Biometric.route)
                }
            )
        }

        // ---- Biometric Verification ----
        composable(SVRRoute.Biometric.route) {
            BiometricScreen(
                onVerified = {
                    // First-time registration: go to duress PIN setup
                    // Returning voter: go to PIN entry
                    // For now, assume registration flow
                    navController.navigate(SVRRoute.DuressPinSetup.route)
                },
                onUncertain = {
                    // Biometric failed — show IPVC QR fallback
                    navController.navigate(SVRRoute.IPVCQRCode.route)
                },
                onRejected = {
                    navController.navigate(SVRRoute.IPVCQRCode.route)
                }
            )
        }

        // ---- IPVC QR Fallback ----
        composable(SVRRoute.IPVCQRCode.route) {
            IPVCQRCodeScreen(
                voterDocumentHash = "placeholder-doc-hash",
                electionId = "general-2026-11-03",
                deviceKeyMaterial = ByteArray(32),
                onVerificationComplete = {
                    navController.navigate(SVRRoute.PinEntry.route) {
                        popUpTo(SVRRoute.IPVCQRCode.route) { inclusive = true }
                    }
                },
                onCancel = {
                    navController.navigate(SVRRoute.Splash.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ---- Duress PIN Setup (Registration) ----
        composable(SVRRoute.DuressPinSetup.route) {
            DuressPinSetupScreen(
                onPinSet = { pin ->
                    navController.navigate(SVRRoute.RegistrationPhotos.route)
                },
                onSkipInfo = { /* Show info dialog */ }
            )
        }

        // ---- Registration Photos (Selfie + ID + Holding) ----
        composable(SVRRoute.RegistrationPhotos.route) {
            RegistrationPhotosScreen(
                onPhotosComplete = {
                    navController.navigate(SVRRoute.PinEntry.route) {
                        popUpTo(SVRRoute.RegistrationPhotos.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        // ---- PIN Entry ----
        composable(SVRRoute.PinEntry.route) {
            PINEntryScreen(
                onPinVerified = { isDuress ->
                    navController.navigate(SVRRoute.TokenIssuance.route)
                }
            )
        }

        // ---- Token Issuance ----
        composable(SVRRoute.TokenIssuance.route) {
            TokenIssuanceScreen(
                onTokenIssued = {
                    navController.navigate(SVRRoute.Ballot.createRoute(0)) {
                        popUpTo(SVRRoute.TokenIssuance.route) { inclusive = true }
                    }
                }
            )
        }

        // ---- Ballot Races ----
        composable(
            route = SVRRoute.Ballot.route,
            arguments = listOf(navArgument("raceIndex") { type = NavType.IntType })
        ) { backStackEntry ->
            val raceIndex = backStackEntry.arguments?.getInt("raceIndex") ?: 0

            BallotRaceScreen(
                raceIndex = raceIndex,
                totalRaces = totalRaces,
                currentSelection = selections[raceIndex.toString()],
                onSelect = { candidateId ->
                    selections = selections.toMutableMap().also {
                        it[raceIndex.toString()] = candidateId
                    }
                },
                onNext = {
                    if (raceIndex < totalRaces - 1) {
                        navController.navigate(SVRRoute.Ballot.createRoute(raceIndex + 1))
                    } else {
                        navController.navigate(SVRRoute.BallotReview.route)
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // ---- Ballot Review ----
        composable(SVRRoute.BallotReview.route) {
            BallotReviewScreen(
                selections = selections,
                onConfirm = {
                    navController.navigate(SVRRoute.Encrypting.route) {
                        popUpTo(SVRRoute.BallotReview.route) { inclusive = true }
                    }
                },
                onChangeRace = { raceIndex ->
                    navController.navigate(SVRRoute.Ballot.createRoute(raceIndex))
                }
            )
        }

        // ---- Encryption Animation ----
        composable(SVRRoute.Encrypting.route) {
            EncryptionScreen(
                onComplete = { recordId, hash ->
                    voteRecordId = recordId
                    confirmationHash = hash
                    navController.navigate(SVRRoute.Success.route) {
                        popUpTo(SVRRoute.Encrypting.route) { inclusive = true }
                    }
                }
            )
        }

        // ---- Success ----
        composable(SVRRoute.Success.route) {
            SuccessScreen(
                voteRecordId = voteRecordId,
                confirmationHash = confirmationHash,
                onDone = {
                    navController.navigate(SVRRoute.ThankYou.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ---- Thank You ----
        composable(SVRRoute.ThankYou.route) {
            ThankYouScreen(
                onReturn = {
                    navController.navigate(SVRRoute.Splash.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ---- Post-Election Verification ----
        composable(SVRRoute.Verify.route) {
            VerificationScreen(
                onVerify = { recordId, pin ->
                    // In production: call repository.verifyVote()
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
