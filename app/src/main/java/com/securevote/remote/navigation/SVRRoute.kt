package com.securevote.remote.navigation

sealed class SVRRoute(val route: String) {
    object Splash : SVRRoute("splash")
    object VoterLookup : SVRRoute("voter_lookup")
    object NFCScan : SVRRoute("nfc_scan")
    object Biometric : SVRRoute("biometric")
    object PinEntry : SVRRoute("pin_entry")
    object TokenIssuance : SVRRoute("token_issuance")
    object Ballot : SVRRoute("ballot/{raceIndex}") {
        fun createRoute(index: Int) = "ballot/$index"
    }
    object BallotReview : SVRRoute("ballot_review")
    object Encrypting : SVRRoute("encrypting")
    object Success : SVRRoute("success")
    object ThankYou : SVRRoute("thank_you")
    object Verify : SVRRoute("verify")
}
