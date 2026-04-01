package com.securevote.remote.navigation

/**
 * All navigation routes in the SVR app.
 * Each route maps to one screen in the voting flow.
 */
sealed class SVRRoute(val route: String) {
    // ---- Registration Flow ----
    data object Splash : SVRRoute("splash")
    data object LanguageSelect : SVRRoute("language_select")
    data object NFCScan : SVRRoute("nfc_scan")
    data object Biometric : SVRRoute("biometric")
    data object DuressPinSetup : SVRRoute("duress_pin_setup")
    data object RegistrationPhotos : SVRRoute("registration_photos")
    data object RegistrationComplete : SVRRoute("registration_complete")

    // ---- Biometric Fallback (IPVC QR) ----
    data object IPVCQRCode : SVRRoute("ipvc_qr_code")
    data object IPVCWaiting : SVRRoute("ipvc_waiting")

    // ---- Voting Session ----
    data object PinEntry : SVRRoute("pin_entry")
    data object BiometricReVerify : SVRRoute("biometric_reverify")
    data object TokenIssuance : SVRRoute("token_issuance")
    data object Ballot : SVRRoute("ballot/{raceIndex}") {
        fun createRoute(raceIndex: Int) = "ballot/$raceIndex"
    }
    data object BallotReview : SVRRoute("ballot_review")
    data object Encrypting : SVRRoute("encrypting")
    data object Success : SVRRoute("success")
    data object ThankYou : SVRRoute("thank_you")

    // ---- Verification ----
    data object Verify : SVRRoute("verify")
}
