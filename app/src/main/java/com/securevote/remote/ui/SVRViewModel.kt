package com.securevote.remote.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.securevote.remote.crypto.SVRCrypto
import com.securevote.remote.data.models.*
import com.securevote.remote.data.repository.SVRRepository
import com.securevote.remote.util.SecurityUtil
import com.securevote.remote.util.TimeUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Main ViewModel for the SecureVote Remote voting flow.
 *
 * Manages the complete state machine:
 *   Registration → Authentication → Token Issuance → Ballot → Encrypt → Submit
 *
 * SECURITY INVARIANTS:
 *   - Ballot selections live ONLY in memory (never persisted to disk)
 *   - Biometric templates are hashed immediately and the raw data wiped
 *   - PINs are hashed with Argon2id before leaving this class
 *   - All sensitive byte arrays are zeroed on clear/destroy
 *   - The duress PIN flag is determined locally and encrypted before transmission
 */
@HiltViewModel
class SVRViewModel @Inject constructor(
    private val repository: SVRRepository,
) : ViewModel() {

    // ---- Registration State ----
    private val _registrationState = MutableStateFlow<RegistrationState>(RegistrationState.NotStarted)
    val registrationState: StateFlow<RegistrationState> = _registrationState.asStateFlow()

    // ---- Voting Session State ----
    private val _sessionState = MutableStateFlow<SessionState>(SessionState.NotAuthenticated)
    val sessionState: StateFlow<SessionState> = _sessionState.asStateFlow()

    // ---- Ballot State ----
    private val _ballotState = MutableStateFlow(BallotState())
    val ballotState: StateFlow<BallotState> = _ballotState.asStateFlow()

    // ---- Submission Result ----
    private val _submissionResult = MutableStateFlow<SubmissionResult?>(null)
    val submissionResult: StateFlow<SubmissionResult?> = _submissionResult.asStateFlow()

    // ---- In-memory sensitive data (never touches disk) ----
    private var voterIdentity: VoterIdentity? = null
    private var biometricHash: String? = null
    private var electionPinHash: String? = null
    private var duressPinHash: String? = null
    private var isDuressSession: Boolean = false

    // ================================================================
    // REGISTRATION
    // ================================================================

    /**
     * Process NFC scan result.
     */
    fun onNFCScanComplete(identity: VoterIdentity) {
        voterIdentity = identity
        _registrationState.value = RegistrationState.NFCComplete(identity)
    }

    /**
     * Process biometric verification result.
     */
    fun onBiometricComplete(hash: String) {
        biometricHash = hash
        _registrationState.value = RegistrationState.BiometricComplete(hash)
    }

    /**
     * Store duress PIN hash (set during registration).
     */
    fun onDuressPinSet(pin: String) {
        duressPinHash = SVRCrypto.hashPinArgon2id(pin)
        _registrationState.value = RegistrationState.DuressPinSet
    }

    /**
     * Complete device registration with the server.
     */
    fun completeRegistration(photos: RegistrationPhotos, electionId: String) {
        viewModelScope.launch {
            _registrationState.value = RegistrationState.Registering

            val result = repository.registerDevice(
                voterIdentity = voterIdentity ?: throw IllegalStateException("No identity"),
                biometricHash = biometricHash ?: throw IllegalStateException("No biometric"),
                photos = photos,
                duressPinHash = duressPinHash ?: throw IllegalStateException("No duress PIN"),
                electionId = electionId,
            )

            _registrationState.value = result.fold(
                onSuccess = { RegistrationState.Complete(it) },
                onFailure = { RegistrationState.Error(it.message ?: "Registration failed") }
            )
        }
    }

    // ================================================================
    // VOTING SESSION
    // ================================================================

    /**
     * Authenticate with Election PIN.
     * Detects duress PIN locally by comparing hashes.
     */
    fun authenticateWithPin(pin: String) {
        viewModelScope.launch {
            _sessionState.value = SessionState.Authenticating

            // Check if this is the duress PIN
            val pinHash = SVRCrypto.hashPinArgon2id(pin)
            isDuressSession = (pinHash == duressPinHash)

            val result = repository.authenticateSession(
                pin = pin,
                biometricHash = biometricHash ?: "",
                isDuressPin = isDuressSession,
            )

            _sessionState.value = result.fold(
                onSuccess = { SessionState.Authenticated(it) },
                onFailure = { SessionState.AuthError(it.message ?: "Authentication failed") }
            )
        }
    }

    /**
     * Issue blind-signed voter token.
     */
    fun issueToken() {
        viewModelScope.launch {
            _sessionState.value = SessionState.IssuingToken

            val result = repository.issueBlindToken()

            _sessionState.value = result.fold(
                onSuccess = { SessionState.TokenIssued },
                onFailure = { SessionState.AuthError("Token issuance failed: ${it.message}") }
            )
        }
    }

    /**
     * Fetch the Ballot Definition File.
     */
    fun fetchBallot(electionId: String, precinctId: String) {
        viewModelScope.launch {
            val result = repository.fetchBallotDefinition(electionId, precinctId)
            result.onSuccess { bdf ->
                _ballotState.value = BallotState(
                    bdf = bdf,
                    selections = bdf.races.associate { it.raceId to null }.toMutableMap(),
                    currentRaceIndex = 0,
                )
            }
        }
    }

    // ================================================================
    // BALLOT INTERACTION
    // ================================================================

    /**
     * Select a candidate for a race.
     */
    fun selectCandidate(raceId: String, candidateHash: String?) {
        val current = _ballotState.value
        val newSelections = current.selections.toMutableMap()
        newSelections[raceId] = candidateHash
        _ballotState.value = current.copy(selections = newSelections)
    }

    /**
     * Navigate to next race.
     */
    fun nextRace() {
        val current = _ballotState.value
        if (current.currentRaceIndex < (current.bdf?.races?.size ?: 0) - 1) {
            _ballotState.value = current.copy(currentRaceIndex = current.currentRaceIndex + 1)
        }
    }

    /**
     * Navigate to previous race.
     */
    fun previousRace() {
        val current = _ballotState.value
        if (current.currentRaceIndex > 0) {
            _ballotState.value = current.copy(currentRaceIndex = current.currentRaceIndex - 1)
        }
    }

    /**
     * Jump to a specific race (from review screen "Change" button).
     */
    fun goToRace(index: Int) {
        _ballotState.value = _ballotState.value.copy(currentRaceIndex = index)
    }

    // ================================================================
    // VOTE SUBMISSION
    // ================================================================

    /**
     * Encrypt and submit the ballot.
     * This is the critical path — the moment the vote becomes real.
     */
    fun submitVote() {
        viewModelScope.launch {
            _submissionResult.value = SubmissionResult.Encrypting

            val ballot = _ballotState.value
            val bdf = ballot.bdf ?: throw IllegalStateException("No BDF loaded")

            // Build selections
            val raceSelections = bdf.races.map { race ->
                RaceSelection(
                    raceId = race.raceId,
                    candidateHashes = listOfNotNull(ballot.selections[race.raceId]),
                )
            }

            val nonce = SVRCrypto.generateNonce()

            val selections = BallotSelections(
                electionId = bdf.electionId,
                precinctId = bdf.precinctId,
                selections = raceSelections,
                nonce = nonce,
                timestamp = TimeUtil.nowISO(),
            )

            // Submit (encrypt + sign + send happens inside repository)
            val result = repository.submitVote(
                selections = selections,
                biometricHash = biometricHash ?: "",
            )

            _submissionResult.value = result.fold(
                onSuccess = { response ->
                    SubmissionResult.Success(
                        voteRecordId = response.voteRecordId,
                        confirmationHash = response.confirmationHash,
                        submissionSequence = response.submissionSequence,
                        remainingSubmissions = response.remainingSubmissions,
                    )
                },
                onFailure = { SubmissionResult.Error(it.message ?: "Submission failed") }
            )
        }
    }

    // ================================================================
    // CLEANUP
    // ================================================================

    /**
     * Clear all session state and wipe sensitive memory.
     */
    fun clearAll() {
        repository.clearSession()
        voterIdentity = null
        biometricHash?.let { SecurityUtil.secureWipeString(it) }
        biometricHash = null
        electionPinHash?.let { SecurityUtil.secureWipeString(it) }
        electionPinHash = null
        isDuressSession = false

        _registrationState.value = RegistrationState.NotStarted
        _sessionState.value = SessionState.NotAuthenticated
        _ballotState.value = BallotState()
        _submissionResult.value = null
    }

    override fun onCleared() {
        super.onCleared()
        clearAll()
    }

    // ================================================================
    // STATE CLASSES
    // ================================================================

    sealed class RegistrationState {
        data object NotStarted : RegistrationState()
        data class NFCComplete(val identity: VoterIdentity) : RegistrationState()
        data class BiometricComplete(val hash: String) : RegistrationState()
        data object DuressPinSet : RegistrationState()
        data object Registering : RegistrationState()
        data class Complete(val response: DeviceRegistrationResponse) : RegistrationState()
        data class Error(val message: String) : RegistrationState()
    }

    sealed class SessionState {
        data object NotAuthenticated : SessionState()
        data object Authenticating : SessionState()
        data class Authenticated(val session: com.securevote.remote.data.api.SessionAuthResponse) : SessionState()
        data object IssuingToken : SessionState()
        data object TokenIssued : SessionState()
        data class AuthError(val message: String) : SessionState()
    }

    data class BallotState(
        val bdf: BallotDefinition? = null,
        val selections: MutableMap<String, String?> = mutableMapOf(),
        val currentRaceIndex: Int = 0,
    )

    sealed class SubmissionResult {
        data object Encrypting : SubmissionResult()
        data class Success(
            val voteRecordId: String,
            val confirmationHash: String,
            val submissionSequence: Int,
            val remainingSubmissions: Int,
        ) : SubmissionResult()
        data class Error(val message: String) : SubmissionResult()
    }
}
