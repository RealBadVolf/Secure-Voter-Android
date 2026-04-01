package com.securevote.remote.data.models

import kotlinx.serialization.Serializable

// ============================================================
// Domain Models — matches ABSENTEE.md and DATABASE.md schemas
// ============================================================

/**
 * Voter identity extracted from NFC chip (ICAO 9303).
 * Raw biometric data never leaves the device.
 */
@Serializable
data class VoterIdentity(
    val documentNumber: String,
    val fullName: String,
    val dateOfBirth: String,       // ISO 8601
    val documentExpiry: String,
    val issuingAuthority: String,
    val nfcChipSignatureValid: Boolean,
    val photoHash: String,         // SHA-256 of the chip photo, for server-side audit
)

/**
 * Device registration request sent to sv-remote-auth.
 */
@Serializable
data class DeviceRegistrationRequest(
    val devicePublicKeyHash: String,         // SHA-256 of hardware-backed public key
    val attestationType: AttestationType,
    val attestationPayload: String,          // Base64-encoded attestation certificate chain
    val nfcChipData: String,                 // Base64-encoded signed NFC data
    val nfcVerification: Boolean,
    val biometricHash: String,               // SHA-256 of on-device biometric template
    val registrationPhotos: RegistrationPhotos,
    val duressPinHash: String,               // Argon2id hash of duress PIN
    val electionId: String,
)

@Serializable
enum class AttestationType {
    ANDROID_KEY_ATTESTATION,
    ANDROID_PLAY_INTEGRITY,
}

@Serializable
data class RegistrationPhotos(
    val selfieEncrypted: String,     // Base64 AES-256-GCM encrypted
    val idPhotoEncrypted: String,
    val holdingPhotoEncrypted: String,
    val photoQualityScores: PhotoQualityScores,
)

@Serializable
data class PhotoQualityScores(
    val selfieConfidence: Float,
    val idTextReadability: Float,
    val holdingPhotoCoherence: Float,
)

/**
 * Device registration response from sv-remote-auth.
 */
@Serializable
data class DeviceRegistrationResponse(
    val registrationToken: String,   // Signed JWT binding device key to voter eligibility
    val electionId: String,
    val precinctId: String,
    val status: RegistrationStatus,
    val expiresAt: String,           // ISO 8601
)

@Serializable
enum class RegistrationStatus {
    ACTIVE,
    REVOKED,
    PIN_LOCKED,
    COOLING,
}

/**
 * IPVC QR Code payload — generated on device, scanned by IPVC admin.
 * Rotates every 10 seconds (TOTP-like nonce prevents screenshot relay attack).
 */
@Serializable
data class IPVCQRPayload(
    val devicePublicKeyHash: String,
    val voterDocumentHash: String,      // SHA-256 of ID document number
    val electionId: String,
    val timestamp: Long,                // Unix epoch seconds
    val nonce: String,                  // Random 16-byte hex, regenerated every 10s
    val hmac: String,                   // HMAC-SHA256(payload, device_private_key) — proves device possession
    val expiresAt: Long,                // timestamp + 10 seconds
    val appVersion: String,
)

/**
 * Voting session authentication request.
 */
@Serializable
data class VotingAuthRequest(
    val deviceAttestationFresh: String,      // Fresh attestation payload
    val electionPinHash: String,             // Argon2id hash
    val registrationToken: String,           // From registration step
    val biometricHash: String,               // Fresh biometric hash
    val isDuressPin: Boolean = false,        // Set locally — never transmitted as plaintext
)

/**
 * Blind token issuance.
 */
@Serializable
data class BlindTokenRequest(
    val blindedToken: String,    // Base64 RSA-blinded token
)

@Serializable
data class BlindTokenResponse(
    val blindSignature: String,  // Base64 RSA blind signature
)

/**
 * Election and ballot data from sv-remote-ballot.
 */
@Serializable
data class Election(
    val electionId: String,
    val name: String,
    val date: String,
    val precinctId: String,
    val precinctName: String,
)

@Serializable
data class BallotDefinition(
    val bdfId: String,
    val electionId: String,
    val precinctId: String,
    val bdfHash: String,                 // SHA-256 of entire BDF
    val signatures: List<BDFSignature>,  // Multi-party signatures
    val races: List<Race>,
    val languages: List<String>,
)

@Serializable
data class BDFSignature(
    val signerName: String,
    val signerRole: String,
    val signature: String,       // Base64
    val publicKey: String,       // Base64
)

@Serializable
data class Race(
    val raceId: String,
    val title: String,
    val instruction: String,     // "Vote for ONE", "Vote for up to THREE", etc.
    val description: String? = null,
    val maxSelections: Int = 1,
    val candidates: List<Candidate>,
)

@Serializable
data class Candidate(
    val candidateId: String,
    val name: String,
    val party: String,
    val candidateHash: String,   // SHA-256(FullLegalName + Party + RaceID + ElectionID + Salt)
)

/**
 * Vote selections — what gets encrypted.
 */
@Serializable
data class BallotSelections(
    val electionId: String,
    val precinctId: String,
    val selections: List<RaceSelection>,
    val nonce: String,           // 32-byte random hex
    val timestamp: String,       // ISO 8601 UTC
)

@Serializable
data class RaceSelection(
    val raceId: String,
    val candidateHashes: List<String>,  // Empty list = undervote (skipped)
)

/**
 * The RemoteVoteCast — submitted to sv-remote-ballot.
 * Matches the schema in ABSENTEE.md exactly.
 */
@Serializable
data class RemoteVoteCast(
    val voteRecordId: String,            // UUID generated on device
    val voterToken: String,              // Unblinded, signed token
    val encryptedBallot: String,         // Base64 hybrid PQ-encrypted blob
    val confirmationHash: String,        // SHA-256(encryptedBallot || voteRecordId || nonce)
    val timestamp: String,               // ISO 8601 UTC
    val nonce: String,
    val biometricHash: String,
    val deviceAttestation: String,       // Fresh attestation
    val deviceSignature: String,         // Hardware-backed signature over entire payload
    val submissionSequence: Int,         // 1 for first, 2+ for revotes
    val status: VoteStatus = VoteStatus.VALID,
    val appVersion: String,
    val protocolVersion: String = "1",
)

@Serializable
enum class VoteStatus {
    VALID,
    SUPERSEDED,
}

/**
 * Server response after vote submission.
 */
@Serializable
data class VoteSubmissionResponse(
    val voteRecordId: String,
    val confirmationHash: String,        // Echoed back for voter verification
    val serverSignature: String,         // Server-signed acknowledgment
    val submissionSequence: Int,
    val remainingSubmissions: Int,        // 5 - submissionSequence
    val merkleLeafQueued: Boolean,
)

/**
 * Verification portal response.
 */
@Serializable
data class VerificationResponse(
    val status: String,                  // "VERIFIED" or "NOT_FOUND"
    val confirmationHash: String?,
    val merkleProof: MerkleProof?,
)

@Serializable
data class MerkleProof(
    val leafHash: String,
    val path: List<MerkleNode>,
    val root: String,
)

@Serializable
data class MerkleNode(
    val hash: String,
    val direction: String,   // "left" or "right"
)
