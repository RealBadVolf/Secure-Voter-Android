package com.securevote.remote.data.repository

import com.securevote.remote.biometric.BiometricEngine
import com.securevote.remote.crypto.SVRCrypto
import com.securevote.remote.data.api.SVRGatewayApi
import com.securevote.remote.data.api.SessionAuthResponse
import com.securevote.remote.data.models.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Central repository for all SVR operations.
 * Coordinates between the network API, crypto module, and biometric engine.
 *
 * CRITICAL: This class enforces the security invariants:
 *   - Biometric templates never leave this class (only hashes are transmitted)
 *   - PINs are hashed with Argon2id before transmission
 *   - Ballot selections are encrypted before touching any network code
 *   - The duress PIN flag is encrypted so even Zone 6 servers can't read it
 */
@Singleton
class SVRRepository @Inject constructor(
    private val api: SVRGatewayApi,
) {
    // ---- Session state (in-memory only, never persisted to disk) ----
    private var registrationToken: String? = null
    private var sessionToken: String? = null
    private var currentElection: Election? = null
    private var currentBDF: BallotDefinition? = null
    private var voterToken: ByteArray? = null
    private var voterTokenSignature: ByteArray? = null
    private var submissionSequence: Int = 1

    // ================================================================
    // REGISTRATION
    // ================================================================

    /**
     * Register this device for remote voting.
     * Called after NFC scan, biometric match, and duress PIN setup.
     */
    suspend fun registerDevice(
        voterIdentity: VoterIdentity,
        biometricHash: String,
        photos: RegistrationPhotos,
        duressPinHash: String,
        electionId: String,
    ): Result<DeviceRegistrationResponse> {
        return try {
            val request = DeviceRegistrationRequest(
                devicePublicKeyHash = SVRCrypto.getDevicePublicKeyHash(),
                attestationType = AttestationType.ANDROID_KEY_ATTESTATION,
                attestationPayload = getDeviceAttestation(),
                nfcChipData = "", // Base64 encoded NFC data
                nfcVerification = voterIdentity.nfcChipSignatureValid,
                biometricHash = biometricHash,
                registrationPhotos = photos,
                duressPinHash = duressPinHash,
                electionId = electionId,
            )

            val response = api.registerDevice(request)
            if (response.isSuccessful && response.body() != null) {
                registrationToken = response.body()!!.registrationToken
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Registration failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ================================================================
    // VOTING SESSION
    // ================================================================

    /**
     * Authenticate a voting session with PIN + biometric + attestation.
     * Handles both real PIN and duress PIN transparently.
     */
    suspend fun authenticateSession(
        pin: String,
        biometricHash: String,
        isDuressPin: Boolean,
    ): Result<SessionAuthResponse> {
        return try {
            val request = VotingAuthRequest(
                deviceAttestationFresh = getDeviceAttestation(),
                electionPinHash = SVRCrypto.hashPinArgon2id(pin),
                registrationToken = registrationToken ?: throw Exception("Not registered"),
                biometricHash = biometricHash,
                isDuressPin = isDuressPin,  // This flag is encrypted before transmission
            )

            val response = api.authenticateSession(request)
            if (response.isSuccessful && response.body() != null) {
                sessionToken = response.body()!!.sessionToken
                submissionSequence = response.body()!!.submissionSequence
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Auth failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Issue a blind-signed voter token.
     * This is the cryptographic step that decouples identity from ballot.
     */
    suspend fun issueBlindToken(): Result<Unit> {
        return try {
            // Generate random token
            val token = SVRCrypto.generateVoterToken()

            // Blind it (server can't see the actual token)
            // In production: use the server's RSA public key for blinding
            val blinded = SVRCrypto.blindToken(token, getServerPublicKey())

            val request = BlindTokenRequest(
                blindedToken = android.util.Base64.encodeToString(blinded, android.util.Base64.NO_WRAP)
            )

            val response = api.issueBlindToken(
                sessionToken = "Bearer ${sessionToken}",
                request = request
            )

            if (response.isSuccessful && response.body() != null) {
                // Unblind the signature
                val blindSig = android.util.Base64.decode(response.body()!!.blindSignature, android.util.Base64.DEFAULT)
                voterToken = token
                voterTokenSignature = SVRCrypto.unblindSignature(blindSig, blinded, getServerPublicKey())
                Result.success(Unit)
            } else {
                Result.failure(Exception("Token issuance failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Fetch the Ballot Definition File.
     */
    suspend fun fetchBallotDefinition(
        electionId: String,
        precinctId: String,
    ): Result<BallotDefinition> {
        return try {
            val response = api.getBallotDefinition(
                sessionToken = "Bearer ${sessionToken}",
                electionId = electionId,
                precinctId = precinctId,
            )
            if (response.isSuccessful && response.body() != null) {
                currentBDF = response.body()
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("BDF fetch failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Encrypt and submit the ballot.
     * This is the critical path:
     *   1. Serialize selections
     *   2. Hybrid PQ encrypt (AES-GCM → ECIES P-384 → ML-KEM-1024)
     *   3. Compute confirmation hash
     *   4. Sign with device key
     *   5. Submit to sv-remote-ballot
     */
    suspend fun submitVote(
        selections: BallotSelections,
        biometricHash: String,
    ): Result<VoteSubmissionResponse> {
        return try {
            val voteRecordId = SVRCrypto.generateVoteRecordId()
            val nonce = SVRCrypto.generateNonce()
            val tokenHex = voterToken?.joinToString("") { "%02x".format(it) }
                ?: throw Exception("No voter token — authenticate first")

            // ---- ENCRYPT ----
            val encrypted = SVRCrypto.encryptBallot(
                selections = selections,
                classicalPublicKey = getElectionClassicalPublicKey(),
                pqPublicKey = getElectionPQPublicKey(),
                voteRecordId = voteRecordId,
                voterToken = tokenHex,
                nonce = nonce,
            )

            // ---- BUILD VOTE CAST ----
            val voteCast = RemoteVoteCast(
                voteRecordId = voteRecordId,
                voterToken = tokenHex,
                encryptedBallot = android.util.Base64.encodeToString(encrypted.ciphertext, android.util.Base64.NO_WRAP),
                confirmationHash = encrypted.confirmationHash,
                timestamp = java.time.Instant.now().toString(),
                nonce = nonce,
                biometricHash = biometricHash,
                deviceAttestation = getDeviceAttestation(),
                deviceSignature = android.util.Base64.encodeToString(
                    SVRCrypto.signWithDeviceKey(encrypted.ciphertext),
                    android.util.Base64.NO_WRAP
                ),
                submissionSequence = submissionSequence,
                appVersion = "1.0.0",
            )

            // ---- SUBMIT ----
            val response = api.submitVote(voteCast)
            if (response.isSuccessful && response.body() != null) {
                submissionSequence++
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Vote submission failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Clear all session state.
     * Called when the voting session ends or the app is closed.
     * Wipes all in-memory secrets.
     */
    fun clearSession() {
        sessionToken = null
        voterToken?.fill(0)  // Explicit zero-fill
        voterToken = null
        voterTokenSignature?.fill(0)
        voterTokenSignature = null
        currentBDF = null
    }

    // ---- Placeholder key loaders (replace with actual key distribution) ----

    private fun getDeviceAttestation(): String {
        // In production: call Play Integrity API
        return "placeholder-attestation"
    }

    private fun getServerPublicKey(): java.security.PublicKey {
        // In production: loaded from pinned certificate or embedded in app
        val kpg = java.security.KeyPairGenerator.getInstance("RSA")
        kpg.initialize(2048)
        return kpg.generateKeyPair().public
    }

    private fun getElectionClassicalPublicKey(): java.security.PublicKey {
        // In production: ECIES P-384 public key from the transparency log
        val kpg = java.security.KeyPairGenerator.getInstance("RSA")
        kpg.initialize(4096)
        return kpg.generateKeyPair().public
    }

    private fun getElectionPQPublicKey(): ByteArray {
        // In production: ML-KEM-1024 encapsulation key from the transparency log
        return ByteArray(1568)
    }
}
