package com.securevote.remote.data.api

import com.securevote.remote.data.models.*
import retrofit2.Response
import retrofit2.http.*

/**
 * sv-remote-gateway REST API.
 * All endpoints use TLS 1.3 with certificate pinning.
 * Base URL: https://remote.securevote.gov/api/v1
 */
interface SVRGatewayApi {

    // ================================================================
    // REGISTRATION (routed to sv-remote-auth)
    // ================================================================

    /**
     * Register a new device for remote voting.
     * Three-factor auth must be completed on-device before calling.
     */
    @POST("register/device")
    suspend fun registerDevice(
        @Body request: DeviceRegistrationRequest
    ): Response<DeviceRegistrationResponse>

    /**
     * Re-register on a new device (lost/replaced phone).
     * Server performs additional photo comparison against original registration.
     * Rate limited: max 2 re-registrations per election.
     * Triggers 4-hour cooling period.
     */
    @POST("register/device/reregister")
    suspend fun reRegisterDevice(
        @Body request: DeviceRegistrationRequest,
        @Header("X-Reregistration") flag: String = "true"
    ): Response<DeviceRegistrationResponse>

    /**
     * IPVC agent-assisted registration.
     * Called by the IPVC workstation, not the voter's phone.
     * The QR code scanned by the agent contains the device binding data.
     */
    @POST("register/ipvc")
    suspend fun ipvcRegister(
        @Body payload: IPVCRegistrationRequest
    ): Response<DeviceRegistrationResponse>

    /**
     * Check registration status.
     */
    @GET("register/status")
    suspend fun getRegistrationStatus(
        @Header("Authorization") token: String,
        @Query("election_id") electionId: String
    ): Response<DeviceRegistrationResponse>

    // ================================================================
    // AUTHENTICATION (routed to sv-remote-auth)
    // ================================================================

    /**
     * Authenticate for a voting session.
     * Validates PIN, biometric hash, device attestation, and registration token.
     * Returns session token for blind token issuance.
     */
    @POST("auth/session")
    suspend fun authenticateSession(
        @Body request: VotingAuthRequest
    ): Response<SessionAuthResponse>

    /**
     * Request blind token issuance.
     * Must be called after successful session authentication.
     */
    @POST("auth/token")
    suspend fun issueBlindToken(
        @Header("Authorization") sessionToken: String,
        @Body request: BlindTokenRequest
    ): Response<BlindTokenResponse>

    // ================================================================
    // BALLOT (routed to sv-remote-ballot)
    // ================================================================

    /**
     * Fetch the Ballot Definition File for the voter's precinct.
     * Returns the signed BDF with all races, candidates, and hashes.
     */
    @GET("ballot/bdf")
    suspend fun getBallotDefinition(
        @Header("Authorization") sessionToken: String,
        @Query("election_id") electionId: String,
        @Query("precinct_id") precinctId: String
    ): Response<BallotDefinition>

    /**
     * Submit an encrypted vote.
     * The server cannot read the ballot contents — they're PQ-encrypted
     * with keys held only by the air-gapped tabulation HSM.
     */
    @POST("ballot/submit")
    suspend fun submitVote(
        @Body voteCast: RemoteVoteCast
    ): Response<VoteSubmissionResponse>

    // ================================================================
    // VERIFICATION (routed to sv-verify, Zone 5)
    // ================================================================

    /**
     * Verify a vote exists in the Merkle tree.
     * Public endpoint, rate-limited.
     */
    @POST("verify")
    suspend fun verifyVote(
        @Body request: VerifyRequest
    ): Response<VerificationResponse>
}

// ---- Supporting request/response types ----

@kotlinx.serialization.Serializable
data class SessionAuthResponse(
    val sessionToken: String,    // Short-lived JWT for this voting session
    val electionId: String,
    val precinctId: String,
    val submissionSequence: Int, // Next sequence number (1 if first time, 2+ if revote)
    val remainingSubmissions: Int,
    val votingWindowClosesAt: String, // ISO 8601
)

@kotlinx.serialization.Serializable
data class IPVCRegistrationRequest(
    val qrPayload: IPVCQRPayload,        // From voter's phone QR
    val agentId: String,                  // IPVC agent worker ID
    val agentCertificate: String,         // Agent's signed credential
    val venueId: String,                  // IPVC location identifier
    val overrideReason: String? = null,   // If biometric was overridden
    val biometricOverride: Boolean = false,
)

@kotlinx.serialization.Serializable
data class VerifyRequest(
    val voteRecordId: String,
    val electionId: String,
    val pin: String,             // Election PIN for authentication
)
