package com.securevote.remote.ui;

import androidx.lifecycle.ViewModel;
import com.securevote.remote.crypto.SVRCrypto;
import com.securevote.remote.data.models.*;
import com.securevote.remote.data.repository.SVRRepository;
import com.securevote.remote.util.SecurityUtil;
import com.securevote.remote.util.TimeUtil;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.flow.StateFlow;
import javax.inject.Inject;

/**
 * Main ViewModel for the SecureVote Remote voting flow.
 *
 * Manages the complete state machine:
 *  Registration → Authentication → Token Issuance → Ballot → Encrypt → Submit
 *
 * SECURITY INVARIANTS:
 *  - Ballot selections live ONLY in memory (never persisted to disk)
 *  - Biometric templates are hashed immediately and the raw data wiped
 *  - PINs are hashed with Argon2id before leaving this class
 *  - All sensitive byte arrays are zeroed on clear/destroy
 *  - The duress PIN flag is determined locally and encrypted before transmission
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000d\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0012\b\u0007\u0018\u00002\u00020\u0001:\u0004:;<=B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020\u0013J\u0006\u0010#\u001a\u00020!J\u0016\u0010$\u001a\u00020!2\u0006\u0010%\u001a\u00020&2\u0006\u0010\'\u001a\u00020\u0013J\u0016\u0010(\u001a\u00020!2\u0006\u0010\'\u001a\u00020\u00132\u0006\u0010)\u001a\u00020\u0013J\u000e\u0010*\u001a\u00020!2\u0006\u0010+\u001a\u00020,J\u0006\u0010-\u001a\u00020!J\u0006\u0010.\u001a\u00020!J\u000e\u0010/\u001a\u00020!2\u0006\u00100\u001a\u00020\u0013J\b\u00101\u001a\u00020!H\u0014J\u000e\u00102\u001a\u00020!2\u0006\u0010\"\u001a\u00020\u0013J\u000e\u00103\u001a\u00020!2\u0006\u00104\u001a\u00020\u001fJ\u0006\u00105\u001a\u00020!J\u0018\u00106\u001a\u00020!2\u0006\u00107\u001a\u00020\u00132\b\u00108\u001a\u0004\u0018\u00010\u0013J\u0006\u00109\u001a\u00020!R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00020\t0\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\f\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\r0\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00070\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0010\u0010\u0012\u001a\u0004\u0018\u00010\u0013X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0014\u001a\u0004\u0018\u00010\u0013X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0015\u001a\u0004\u0018\u00010\u0013X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u0017X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\t0\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0011R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u000b0\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0011R\u0019\u0010\u001c\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\r0\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u0011R\u0010\u0010\u001e\u001a\u0004\u0018\u00010\u001fX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006>"}, d2 = {"Lcom/securevote/remote/ui/SVRViewModel;", "Landroidx/lifecycle/ViewModel;", "repository", "Lcom/securevote/remote/data/repository/SVRRepository;", "(Lcom/securevote/remote/data/repository/SVRRepository;)V", "_ballotState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/securevote/remote/ui/SVRViewModel$BallotState;", "_registrationState", "Lcom/securevote/remote/ui/SVRViewModel$RegistrationState;", "_sessionState", "Lcom/securevote/remote/ui/SVRViewModel$SessionState;", "_submissionResult", "Lcom/securevote/remote/ui/SVRViewModel$SubmissionResult;", "ballotState", "Lkotlinx/coroutines/flow/StateFlow;", "getBallotState", "()Lkotlinx/coroutines/flow/StateFlow;", "biometricHash", "", "duressPinHash", "electionPinHash", "isDuressSession", "", "registrationState", "getRegistrationState", "sessionState", "getSessionState", "submissionResult", "getSubmissionResult", "voterIdentity", "Lcom/securevote/remote/data/models/VoterIdentity;", "authenticateWithPin", "", "pin", "clearAll", "completeRegistration", "photos", "Lcom/securevote/remote/data/models/RegistrationPhotos;", "electionId", "fetchBallot", "precinctId", "goToRace", "index", "", "issueToken", "nextRace", "onBiometricComplete", "hash", "onCleared", "onDuressPinSet", "onNFCScanComplete", "identity", "previousRace", "selectCandidate", "raceId", "candidateHash", "submitVote", "BallotState", "RegistrationState", "SessionState", "SubmissionResult", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class SVRViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.securevote.remote.data.repository.SVRRepository repository = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.securevote.remote.ui.SVRViewModel.RegistrationState> _registrationState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.securevote.remote.ui.SVRViewModel.RegistrationState> registrationState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.securevote.remote.ui.SVRViewModel.SessionState> _sessionState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.securevote.remote.ui.SVRViewModel.SessionState> sessionState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.securevote.remote.ui.SVRViewModel.BallotState> _ballotState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.securevote.remote.ui.SVRViewModel.BallotState> ballotState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.securevote.remote.ui.SVRViewModel.SubmissionResult> _submissionResult = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.securevote.remote.ui.SVRViewModel.SubmissionResult> submissionResult = null;
    @org.jetbrains.annotations.Nullable()
    private com.securevote.remote.data.models.VoterIdentity voterIdentity;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String biometricHash;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String electionPinHash;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String duressPinHash;
    private boolean isDuressSession = false;
    
    @javax.inject.Inject()
    public SVRViewModel(@org.jetbrains.annotations.NotNull()
    com.securevote.remote.data.repository.SVRRepository repository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.securevote.remote.ui.SVRViewModel.RegistrationState> getRegistrationState() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.securevote.remote.ui.SVRViewModel.SessionState> getSessionState() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.securevote.remote.ui.SVRViewModel.BallotState> getBallotState() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.securevote.remote.ui.SVRViewModel.SubmissionResult> getSubmissionResult() {
        return null;
    }
    
    /**
     * Process NFC scan result.
     */
    public final void onNFCScanComplete(@org.jetbrains.annotations.NotNull()
    com.securevote.remote.data.models.VoterIdentity identity) {
    }
    
    /**
     * Process biometric verification result.
     */
    public final void onBiometricComplete(@org.jetbrains.annotations.NotNull()
    java.lang.String hash) {
    }
    
    /**
     * Store duress PIN hash (set during registration).
     */
    public final void onDuressPinSet(@org.jetbrains.annotations.NotNull()
    java.lang.String pin) {
    }
    
    /**
     * Complete device registration with the server.
     */
    public final void completeRegistration(@org.jetbrains.annotations.NotNull()
    com.securevote.remote.data.models.RegistrationPhotos photos, @org.jetbrains.annotations.NotNull()
    java.lang.String electionId) {
    }
    
    /**
     * Authenticate with Election PIN.
     * Detects duress PIN locally by comparing hashes.
     */
    public final void authenticateWithPin(@org.jetbrains.annotations.NotNull()
    java.lang.String pin) {
    }
    
    /**
     * Issue blind-signed voter token.
     */
    public final void issueToken() {
    }
    
    /**
     * Fetch the Ballot Definition File.
     */
    public final void fetchBallot(@org.jetbrains.annotations.NotNull()
    java.lang.String electionId, @org.jetbrains.annotations.NotNull()
    java.lang.String precinctId) {
    }
    
    /**
     * Select a candidate for a race.
     */
    public final void selectCandidate(@org.jetbrains.annotations.NotNull()
    java.lang.String raceId, @org.jetbrains.annotations.Nullable()
    java.lang.String candidateHash) {
    }
    
    /**
     * Navigate to next race.
     */
    public final void nextRace() {
    }
    
    /**
     * Navigate to previous race.
     */
    public final void previousRace() {
    }
    
    /**
     * Jump to a specific race (from review screen "Change" button).
     */
    public final void goToRace(int index) {
    }
    
    /**
     * Encrypt and submit the ballot.
     * This is the critical path — the moment the vote becomes real.
     */
    public final void submitVote() {
    }
    
    /**
     * Clear all session state and wipe sensitive memory.
     */
    public final void clearAll() {
    }
    
    @java.lang.Override()
    protected void onCleared() {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010%\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0087\b\u0018\u00002\u00020\u0001B3\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\u0016\b\u0002\u0010\u0004\u001a\u0010\u0012\u0004\u0012\u00020\u0006\u0012\u0006\u0012\u0004\u0018\u00010\u00060\u0005\u0012\b\b\u0002\u0010\u0007\u001a\u00020\b\u00a2\u0006\u0002\u0010\tJ\u000b\u0010\u0010\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u0017\u0010\u0011\u001a\u0010\u0012\u0004\u0012\u00020\u0006\u0012\u0006\u0012\u0004\u0018\u00010\u00060\u0005H\u00c6\u0003J\t\u0010\u0012\u001a\u00020\bH\u00c6\u0003J7\u0010\u0013\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\u0016\b\u0002\u0010\u0004\u001a\u0010\u0012\u0004\u0012\u00020\u0006\u0012\u0006\u0012\u0004\u0018\u00010\u00060\u00052\b\b\u0002\u0010\u0007\u001a\u00020\bH\u00c6\u0001J\u0013\u0010\u0014\u001a\u00020\u00152\b\u0010\u0016\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0017\u001a\u00020\bH\u00d6\u0001J\t\u0010\u0018\u001a\u00020\u0006H\u00d6\u0001R\u0013\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u001f\u0010\u0004\u001a\u0010\u0012\u0004\u0012\u00020\u0006\u0012\u0006\u0012\u0004\u0018\u00010\u00060\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f\u00a8\u0006\u0019"}, d2 = {"Lcom/securevote/remote/ui/SVRViewModel$BallotState;", "", "bdf", "Lcom/securevote/remote/data/models/BallotDefinition;", "selections", "", "", "currentRaceIndex", "", "(Lcom/securevote/remote/data/models/BallotDefinition;Ljava/util/Map;I)V", "getBdf", "()Lcom/securevote/remote/data/models/BallotDefinition;", "getCurrentRaceIndex", "()I", "getSelections", "()Ljava/util/Map;", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "toString", "app_debug"})
    public static final class BallotState {
        @org.jetbrains.annotations.Nullable()
        private final com.securevote.remote.data.models.BallotDefinition bdf = null;
        @org.jetbrains.annotations.NotNull()
        private final java.util.Map<java.lang.String, java.lang.String> selections = null;
        private final int currentRaceIndex = 0;
        
        @org.jetbrains.annotations.Nullable()
        public final com.securevote.remote.data.models.BallotDefinition component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.Map<java.lang.String, java.lang.String> component2() {
            return null;
        }
        
        public final int component3() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.securevote.remote.ui.SVRViewModel.BallotState copy(@org.jetbrains.annotations.Nullable()
        com.securevote.remote.data.models.BallotDefinition bdf, @org.jetbrains.annotations.NotNull()
        java.util.Map<java.lang.String, java.lang.String> selections, int currentRaceIndex) {
            return null;
        }
        
        @java.lang.Override()
        public boolean equals(@org.jetbrains.annotations.Nullable()
        java.lang.Object other) {
            return false;
        }
        
        @java.lang.Override()
        public int hashCode() {
            return 0;
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public java.lang.String toString() {
            return null;
        }
        
        public BallotState(@org.jetbrains.annotations.Nullable()
        com.securevote.remote.data.models.BallotDefinition bdf, @org.jetbrains.annotations.NotNull()
        java.util.Map<java.lang.String, java.lang.String> selections, int currentRaceIndex) {
            super();
        }
        
        @org.jetbrains.annotations.Nullable()
        public final com.securevote.remote.data.models.BallotDefinition getBdf() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.Map<java.lang.String, java.lang.String> getSelections() {
            return null;
        }
        
        public final int getCurrentRaceIndex() {
            return 0;
        }
        
        public BallotState() {
            super();
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b7\u0018\u00002\u00020\u0001:\u0007\u0003\u0004\u0005\u0006\u0007\b\tB\u0007\b\u0004\u00a2\u0006\u0002\u0010\u0002\u0082\u0001\u0007\n\u000b\f\r\u000e\u000f\u0010\u00a8\u0006\u0011"}, d2 = {"Lcom/securevote/remote/ui/SVRViewModel$RegistrationState;", "", "()V", "BiometricComplete", "Complete", "DuressPinSet", "Error", "NFCComplete", "NotStarted", "Registering", "Lcom/securevote/remote/ui/SVRViewModel$RegistrationState$BiometricComplete;", "Lcom/securevote/remote/ui/SVRViewModel$RegistrationState$Complete;", "Lcom/securevote/remote/ui/SVRViewModel$RegistrationState$DuressPinSet;", "Lcom/securevote/remote/ui/SVRViewModel$RegistrationState$Error;", "Lcom/securevote/remote/ui/SVRViewModel$RegistrationState$NFCComplete;", "Lcom/securevote/remote/ui/SVRViewModel$RegistrationState$NotStarted;", "Lcom/securevote/remote/ui/SVRViewModel$RegistrationState$Registering;", "app_debug"})
    public static abstract class RegistrationState {
        
        private RegistrationState() {
            super();
        }
        
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\t\u0010\u0007\u001a\u00020\u0003H\u00c6\u0003J\u0013\u0010\b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u00d6\u0003J\t\u0010\r\u001a\u00020\u000eH\u00d6\u0001J\t\u0010\u000f\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0010"}, d2 = {"Lcom/securevote/remote/ui/SVRViewModel$RegistrationState$BiometricComplete;", "Lcom/securevote/remote/ui/SVRViewModel$RegistrationState;", "hash", "", "(Ljava/lang/String;)V", "getHash", "()Ljava/lang/String;", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "app_debug"})
        public static final class BiometricComplete extends com.securevote.remote.ui.SVRViewModel.RegistrationState {
            @org.jetbrains.annotations.NotNull()
            private final java.lang.String hash = null;
            
            @org.jetbrains.annotations.NotNull()
            public final java.lang.String component1() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final com.securevote.remote.ui.SVRViewModel.RegistrationState.BiometricComplete copy(@org.jetbrains.annotations.NotNull()
            java.lang.String hash) {
                return null;
            }
            
            @java.lang.Override()
            public boolean equals(@org.jetbrains.annotations.Nullable()
            java.lang.Object other) {
                return false;
            }
            
            @java.lang.Override()
            public int hashCode() {
                return 0;
            }
            
            @java.lang.Override()
            @org.jetbrains.annotations.NotNull()
            public java.lang.String toString() {
                return null;
            }
            
            public BiometricComplete(@org.jetbrains.annotations.NotNull()
            java.lang.String hash) {
            }
            
            @org.jetbrains.annotations.NotNull()
            public final java.lang.String getHash() {
                return null;
            }
        }
        
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\t\u0010\u0007\u001a\u00020\u0003H\u00c6\u0003J\u0013\u0010\b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u00d6\u0003J\t\u0010\r\u001a\u00020\u000eH\u00d6\u0001J\t\u0010\u000f\u001a\u00020\u0010H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0011"}, d2 = {"Lcom/securevote/remote/ui/SVRViewModel$RegistrationState$Complete;", "Lcom/securevote/remote/ui/SVRViewModel$RegistrationState;", "response", "Lcom/securevote/remote/data/models/DeviceRegistrationResponse;", "(Lcom/securevote/remote/data/models/DeviceRegistrationResponse;)V", "getResponse", "()Lcom/securevote/remote/data/models/DeviceRegistrationResponse;", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "", "app_debug"})
        public static final class Complete extends com.securevote.remote.ui.SVRViewModel.RegistrationState {
            @org.jetbrains.annotations.NotNull()
            private final com.securevote.remote.data.models.DeviceRegistrationResponse response = null;
            
            @org.jetbrains.annotations.NotNull()
            public final com.securevote.remote.data.models.DeviceRegistrationResponse component1() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final com.securevote.remote.ui.SVRViewModel.RegistrationState.Complete copy(@org.jetbrains.annotations.NotNull()
            com.securevote.remote.data.models.DeviceRegistrationResponse response) {
                return null;
            }
            
            @java.lang.Override()
            public boolean equals(@org.jetbrains.annotations.Nullable()
            java.lang.Object other) {
                return false;
            }
            
            @java.lang.Override()
            public int hashCode() {
                return 0;
            }
            
            @java.lang.Override()
            @org.jetbrains.annotations.NotNull()
            public java.lang.String toString() {
                return null;
            }
            
            public Complete(@org.jetbrains.annotations.NotNull()
            com.securevote.remote.data.models.DeviceRegistrationResponse response) {
            }
            
            @org.jetbrains.annotations.NotNull()
            public final com.securevote.remote.data.models.DeviceRegistrationResponse getResponse() {
                return null;
            }
        }
        
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u00c7\n\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0013\u0010\u0003\u001a\u00020\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006H\u00d6\u0003J\t\u0010\u0007\u001a\u00020\bH\u00d6\u0001J\t\u0010\t\u001a\u00020\nH\u00d6\u0001\u00a8\u0006\u000b"}, d2 = {"Lcom/securevote/remote/ui/SVRViewModel$RegistrationState$DuressPinSet;", "Lcom/securevote/remote/ui/SVRViewModel$RegistrationState;", "()V", "equals", "", "other", "", "hashCode", "", "toString", "", "app_debug"})
        public static final class DuressPinSet extends com.securevote.remote.ui.SVRViewModel.RegistrationState {
            @org.jetbrains.annotations.NotNull()
            public static final com.securevote.remote.ui.SVRViewModel.RegistrationState.DuressPinSet INSTANCE = null;
            
            @java.lang.Override()
            public boolean equals(@org.jetbrains.annotations.Nullable()
            java.lang.Object other) {
                return false;
            }
            
            @java.lang.Override()
            public int hashCode() {
                return 0;
            }
            
            @java.lang.Override()
            @org.jetbrains.annotations.NotNull()
            public java.lang.String toString() {
                return null;
            }
            
            private DuressPinSet() {
            }
        }
        
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\t\u0010\u0007\u001a\u00020\u0003H\u00c6\u0003J\u0013\u0010\b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u00d6\u0003J\t\u0010\r\u001a\u00020\u000eH\u00d6\u0001J\t\u0010\u000f\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0010"}, d2 = {"Lcom/securevote/remote/ui/SVRViewModel$RegistrationState$Error;", "Lcom/securevote/remote/ui/SVRViewModel$RegistrationState;", "message", "", "(Ljava/lang/String;)V", "getMessage", "()Ljava/lang/String;", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "app_debug"})
        public static final class Error extends com.securevote.remote.ui.SVRViewModel.RegistrationState {
            @org.jetbrains.annotations.NotNull()
            private final java.lang.String message = null;
            
            @org.jetbrains.annotations.NotNull()
            public final java.lang.String component1() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final com.securevote.remote.ui.SVRViewModel.RegistrationState.Error copy(@org.jetbrains.annotations.NotNull()
            java.lang.String message) {
                return null;
            }
            
            @java.lang.Override()
            public boolean equals(@org.jetbrains.annotations.Nullable()
            java.lang.Object other) {
                return false;
            }
            
            @java.lang.Override()
            public int hashCode() {
                return 0;
            }
            
            @java.lang.Override()
            @org.jetbrains.annotations.NotNull()
            public java.lang.String toString() {
                return null;
            }
            
            public Error(@org.jetbrains.annotations.NotNull()
            java.lang.String message) {
            }
            
            @org.jetbrains.annotations.NotNull()
            public final java.lang.String getMessage() {
                return null;
            }
        }
        
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\t\u0010\u0007\u001a\u00020\u0003H\u00c6\u0003J\u0013\u0010\b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u00d6\u0003J\t\u0010\r\u001a\u00020\u000eH\u00d6\u0001J\t\u0010\u000f\u001a\u00020\u0010H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0011"}, d2 = {"Lcom/securevote/remote/ui/SVRViewModel$RegistrationState$NFCComplete;", "Lcom/securevote/remote/ui/SVRViewModel$RegistrationState;", "identity", "Lcom/securevote/remote/data/models/VoterIdentity;", "(Lcom/securevote/remote/data/models/VoterIdentity;)V", "getIdentity", "()Lcom/securevote/remote/data/models/VoterIdentity;", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "", "app_debug"})
        public static final class NFCComplete extends com.securevote.remote.ui.SVRViewModel.RegistrationState {
            @org.jetbrains.annotations.NotNull()
            private final com.securevote.remote.data.models.VoterIdentity identity = null;
            
            @org.jetbrains.annotations.NotNull()
            public final com.securevote.remote.data.models.VoterIdentity component1() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final com.securevote.remote.ui.SVRViewModel.RegistrationState.NFCComplete copy(@org.jetbrains.annotations.NotNull()
            com.securevote.remote.data.models.VoterIdentity identity) {
                return null;
            }
            
            @java.lang.Override()
            public boolean equals(@org.jetbrains.annotations.Nullable()
            java.lang.Object other) {
                return false;
            }
            
            @java.lang.Override()
            public int hashCode() {
                return 0;
            }
            
            @java.lang.Override()
            @org.jetbrains.annotations.NotNull()
            public java.lang.String toString() {
                return null;
            }
            
            public NFCComplete(@org.jetbrains.annotations.NotNull()
            com.securevote.remote.data.models.VoterIdentity identity) {
            }
            
            @org.jetbrains.annotations.NotNull()
            public final com.securevote.remote.data.models.VoterIdentity getIdentity() {
                return null;
            }
        }
        
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u00c7\n\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0013\u0010\u0003\u001a\u00020\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006H\u00d6\u0003J\t\u0010\u0007\u001a\u00020\bH\u00d6\u0001J\t\u0010\t\u001a\u00020\nH\u00d6\u0001\u00a8\u0006\u000b"}, d2 = {"Lcom/securevote/remote/ui/SVRViewModel$RegistrationState$NotStarted;", "Lcom/securevote/remote/ui/SVRViewModel$RegistrationState;", "()V", "equals", "", "other", "", "hashCode", "", "toString", "", "app_debug"})
        public static final class NotStarted extends com.securevote.remote.ui.SVRViewModel.RegistrationState {
            @org.jetbrains.annotations.NotNull()
            public static final com.securevote.remote.ui.SVRViewModel.RegistrationState.NotStarted INSTANCE = null;
            
            @java.lang.Override()
            public boolean equals(@org.jetbrains.annotations.Nullable()
            java.lang.Object other) {
                return false;
            }
            
            @java.lang.Override()
            public int hashCode() {
                return 0;
            }
            
            @java.lang.Override()
            @org.jetbrains.annotations.NotNull()
            public java.lang.String toString() {
                return null;
            }
            
            private NotStarted() {
            }
        }
        
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u00c7\n\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0013\u0010\u0003\u001a\u00020\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006H\u00d6\u0003J\t\u0010\u0007\u001a\u00020\bH\u00d6\u0001J\t\u0010\t\u001a\u00020\nH\u00d6\u0001\u00a8\u0006\u000b"}, d2 = {"Lcom/securevote/remote/ui/SVRViewModel$RegistrationState$Registering;", "Lcom/securevote/remote/ui/SVRViewModel$RegistrationState;", "()V", "equals", "", "other", "", "hashCode", "", "toString", "", "app_debug"})
        public static final class Registering extends com.securevote.remote.ui.SVRViewModel.RegistrationState {
            @org.jetbrains.annotations.NotNull()
            public static final com.securevote.remote.ui.SVRViewModel.RegistrationState.Registering INSTANCE = null;
            
            @java.lang.Override()
            public boolean equals(@org.jetbrains.annotations.Nullable()
            java.lang.Object other) {
                return false;
            }
            
            @java.lang.Override()
            public int hashCode() {
                return 0;
            }
            
            @java.lang.Override()
            @org.jetbrains.annotations.NotNull()
            public java.lang.String toString() {
                return null;
            }
            
            private Registering() {
            }
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b7\u0018\u00002\u00020\u0001:\u0006\u0003\u0004\u0005\u0006\u0007\bB\u0007\b\u0004\u00a2\u0006\u0002\u0010\u0002\u0082\u0001\u0006\t\n\u000b\f\r\u000e\u00a8\u0006\u000f"}, d2 = {"Lcom/securevote/remote/ui/SVRViewModel$SessionState;", "", "()V", "AuthError", "Authenticated", "Authenticating", "IssuingToken", "NotAuthenticated", "TokenIssued", "Lcom/securevote/remote/ui/SVRViewModel$SessionState$AuthError;", "Lcom/securevote/remote/ui/SVRViewModel$SessionState$Authenticated;", "Lcom/securevote/remote/ui/SVRViewModel$SessionState$Authenticating;", "Lcom/securevote/remote/ui/SVRViewModel$SessionState$IssuingToken;", "Lcom/securevote/remote/ui/SVRViewModel$SessionState$NotAuthenticated;", "Lcom/securevote/remote/ui/SVRViewModel$SessionState$TokenIssued;", "app_debug"})
    public static abstract class SessionState {
        
        private SessionState() {
            super();
        }
        
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\t\u0010\u0007\u001a\u00020\u0003H\u00c6\u0003J\u0013\u0010\b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u00d6\u0003J\t\u0010\r\u001a\u00020\u000eH\u00d6\u0001J\t\u0010\u000f\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0010"}, d2 = {"Lcom/securevote/remote/ui/SVRViewModel$SessionState$AuthError;", "Lcom/securevote/remote/ui/SVRViewModel$SessionState;", "message", "", "(Ljava/lang/String;)V", "getMessage", "()Ljava/lang/String;", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "app_debug"})
        public static final class AuthError extends com.securevote.remote.ui.SVRViewModel.SessionState {
            @org.jetbrains.annotations.NotNull()
            private final java.lang.String message = null;
            
            @org.jetbrains.annotations.NotNull()
            public final java.lang.String component1() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final com.securevote.remote.ui.SVRViewModel.SessionState.AuthError copy(@org.jetbrains.annotations.NotNull()
            java.lang.String message) {
                return null;
            }
            
            @java.lang.Override()
            public boolean equals(@org.jetbrains.annotations.Nullable()
            java.lang.Object other) {
                return false;
            }
            
            @java.lang.Override()
            public int hashCode() {
                return 0;
            }
            
            @java.lang.Override()
            @org.jetbrains.annotations.NotNull()
            public java.lang.String toString() {
                return null;
            }
            
            public AuthError(@org.jetbrains.annotations.NotNull()
            java.lang.String message) {
            }
            
            @org.jetbrains.annotations.NotNull()
            public final java.lang.String getMessage() {
                return null;
            }
        }
        
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\t\u0010\u0007\u001a\u00020\u0003H\u00c6\u0003J\u0013\u0010\b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u00d6\u0003J\t\u0010\r\u001a\u00020\u000eH\u00d6\u0001J\t\u0010\u000f\u001a\u00020\u0010H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0011"}, d2 = {"Lcom/securevote/remote/ui/SVRViewModel$SessionState$Authenticated;", "Lcom/securevote/remote/ui/SVRViewModel$SessionState;", "session", "Lcom/securevote/remote/data/api/SessionAuthResponse;", "(Lcom/securevote/remote/data/api/SessionAuthResponse;)V", "getSession", "()Lcom/securevote/remote/data/api/SessionAuthResponse;", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "", "app_debug"})
        public static final class Authenticated extends com.securevote.remote.ui.SVRViewModel.SessionState {
            @org.jetbrains.annotations.NotNull()
            private final com.securevote.remote.data.api.SessionAuthResponse session = null;
            
            @org.jetbrains.annotations.NotNull()
            public final com.securevote.remote.data.api.SessionAuthResponse component1() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final com.securevote.remote.ui.SVRViewModel.SessionState.Authenticated copy(@org.jetbrains.annotations.NotNull()
            com.securevote.remote.data.api.SessionAuthResponse session) {
                return null;
            }
            
            @java.lang.Override()
            public boolean equals(@org.jetbrains.annotations.Nullable()
            java.lang.Object other) {
                return false;
            }
            
            @java.lang.Override()
            public int hashCode() {
                return 0;
            }
            
            @java.lang.Override()
            @org.jetbrains.annotations.NotNull()
            public java.lang.String toString() {
                return null;
            }
            
            public Authenticated(@org.jetbrains.annotations.NotNull()
            com.securevote.remote.data.api.SessionAuthResponse session) {
            }
            
            @org.jetbrains.annotations.NotNull()
            public final com.securevote.remote.data.api.SessionAuthResponse getSession() {
                return null;
            }
        }
        
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u00c7\n\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0013\u0010\u0003\u001a\u00020\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006H\u00d6\u0003J\t\u0010\u0007\u001a\u00020\bH\u00d6\u0001J\t\u0010\t\u001a\u00020\nH\u00d6\u0001\u00a8\u0006\u000b"}, d2 = {"Lcom/securevote/remote/ui/SVRViewModel$SessionState$Authenticating;", "Lcom/securevote/remote/ui/SVRViewModel$SessionState;", "()V", "equals", "", "other", "", "hashCode", "", "toString", "", "app_debug"})
        public static final class Authenticating extends com.securevote.remote.ui.SVRViewModel.SessionState {
            @org.jetbrains.annotations.NotNull()
            public static final com.securevote.remote.ui.SVRViewModel.SessionState.Authenticating INSTANCE = null;
            
            @java.lang.Override()
            public boolean equals(@org.jetbrains.annotations.Nullable()
            java.lang.Object other) {
                return false;
            }
            
            @java.lang.Override()
            public int hashCode() {
                return 0;
            }
            
            @java.lang.Override()
            @org.jetbrains.annotations.NotNull()
            public java.lang.String toString() {
                return null;
            }
            
            private Authenticating() {
            }
        }
        
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u00c7\n\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0013\u0010\u0003\u001a\u00020\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006H\u00d6\u0003J\t\u0010\u0007\u001a\u00020\bH\u00d6\u0001J\t\u0010\t\u001a\u00020\nH\u00d6\u0001\u00a8\u0006\u000b"}, d2 = {"Lcom/securevote/remote/ui/SVRViewModel$SessionState$IssuingToken;", "Lcom/securevote/remote/ui/SVRViewModel$SessionState;", "()V", "equals", "", "other", "", "hashCode", "", "toString", "", "app_debug"})
        public static final class IssuingToken extends com.securevote.remote.ui.SVRViewModel.SessionState {
            @org.jetbrains.annotations.NotNull()
            public static final com.securevote.remote.ui.SVRViewModel.SessionState.IssuingToken INSTANCE = null;
            
            @java.lang.Override()
            public boolean equals(@org.jetbrains.annotations.Nullable()
            java.lang.Object other) {
                return false;
            }
            
            @java.lang.Override()
            public int hashCode() {
                return 0;
            }
            
            @java.lang.Override()
            @org.jetbrains.annotations.NotNull()
            public java.lang.String toString() {
                return null;
            }
            
            private IssuingToken() {
            }
        }
        
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u00c7\n\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0013\u0010\u0003\u001a\u00020\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006H\u00d6\u0003J\t\u0010\u0007\u001a\u00020\bH\u00d6\u0001J\t\u0010\t\u001a\u00020\nH\u00d6\u0001\u00a8\u0006\u000b"}, d2 = {"Lcom/securevote/remote/ui/SVRViewModel$SessionState$NotAuthenticated;", "Lcom/securevote/remote/ui/SVRViewModel$SessionState;", "()V", "equals", "", "other", "", "hashCode", "", "toString", "", "app_debug"})
        public static final class NotAuthenticated extends com.securevote.remote.ui.SVRViewModel.SessionState {
            @org.jetbrains.annotations.NotNull()
            public static final com.securevote.remote.ui.SVRViewModel.SessionState.NotAuthenticated INSTANCE = null;
            
            @java.lang.Override()
            public boolean equals(@org.jetbrains.annotations.Nullable()
            java.lang.Object other) {
                return false;
            }
            
            @java.lang.Override()
            public int hashCode() {
                return 0;
            }
            
            @java.lang.Override()
            @org.jetbrains.annotations.NotNull()
            public java.lang.String toString() {
                return null;
            }
            
            private NotAuthenticated() {
            }
        }
        
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u00c7\n\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0013\u0010\u0003\u001a\u00020\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006H\u00d6\u0003J\t\u0010\u0007\u001a\u00020\bH\u00d6\u0001J\t\u0010\t\u001a\u00020\nH\u00d6\u0001\u00a8\u0006\u000b"}, d2 = {"Lcom/securevote/remote/ui/SVRViewModel$SessionState$TokenIssued;", "Lcom/securevote/remote/ui/SVRViewModel$SessionState;", "()V", "equals", "", "other", "", "hashCode", "", "toString", "", "app_debug"})
        public static final class TokenIssued extends com.securevote.remote.ui.SVRViewModel.SessionState {
            @org.jetbrains.annotations.NotNull()
            public static final com.securevote.remote.ui.SVRViewModel.SessionState.TokenIssued INSTANCE = null;
            
            @java.lang.Override()
            public boolean equals(@org.jetbrains.annotations.Nullable()
            java.lang.Object other) {
                return false;
            }
            
            @java.lang.Override()
            public int hashCode() {
                return 0;
            }
            
            @java.lang.Override()
            @org.jetbrains.annotations.NotNull()
            public java.lang.String toString() {
                return null;
            }
            
            private TokenIssued() {
            }
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b7\u0018\u00002\u00020\u0001:\u0003\u0003\u0004\u0005B\u0007\b\u0004\u00a2\u0006\u0002\u0010\u0002\u0082\u0001\u0003\u0006\u0007\b\u00a8\u0006\t"}, d2 = {"Lcom/securevote/remote/ui/SVRViewModel$SubmissionResult;", "", "()V", "Encrypting", "Error", "Success", "Lcom/securevote/remote/ui/SVRViewModel$SubmissionResult$Encrypting;", "Lcom/securevote/remote/ui/SVRViewModel$SubmissionResult$Error;", "Lcom/securevote/remote/ui/SVRViewModel$SubmissionResult$Success;", "app_debug"})
    public static abstract class SubmissionResult {
        
        private SubmissionResult() {
            super();
        }
        
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u00c7\n\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0013\u0010\u0003\u001a\u00020\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006H\u00d6\u0003J\t\u0010\u0007\u001a\u00020\bH\u00d6\u0001J\t\u0010\t\u001a\u00020\nH\u00d6\u0001\u00a8\u0006\u000b"}, d2 = {"Lcom/securevote/remote/ui/SVRViewModel$SubmissionResult$Encrypting;", "Lcom/securevote/remote/ui/SVRViewModel$SubmissionResult;", "()V", "equals", "", "other", "", "hashCode", "", "toString", "", "app_debug"})
        public static final class Encrypting extends com.securevote.remote.ui.SVRViewModel.SubmissionResult {
            @org.jetbrains.annotations.NotNull()
            public static final com.securevote.remote.ui.SVRViewModel.SubmissionResult.Encrypting INSTANCE = null;
            
            @java.lang.Override()
            public boolean equals(@org.jetbrains.annotations.Nullable()
            java.lang.Object other) {
                return false;
            }
            
            @java.lang.Override()
            public int hashCode() {
                return 0;
            }
            
            @java.lang.Override()
            @org.jetbrains.annotations.NotNull()
            public java.lang.String toString() {
                return null;
            }
            
            private Encrypting() {
            }
        }
        
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\t\u0010\u0007\u001a\u00020\u0003H\u00c6\u0003J\u0013\u0010\b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u00d6\u0003J\t\u0010\r\u001a\u00020\u000eH\u00d6\u0001J\t\u0010\u000f\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0010"}, d2 = {"Lcom/securevote/remote/ui/SVRViewModel$SubmissionResult$Error;", "Lcom/securevote/remote/ui/SVRViewModel$SubmissionResult;", "message", "", "(Ljava/lang/String;)V", "getMessage", "()Ljava/lang/String;", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "app_debug"})
        public static final class Error extends com.securevote.remote.ui.SVRViewModel.SubmissionResult {
            @org.jetbrains.annotations.NotNull()
            private final java.lang.String message = null;
            
            @org.jetbrains.annotations.NotNull()
            public final java.lang.String component1() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final com.securevote.remote.ui.SVRViewModel.SubmissionResult.Error copy(@org.jetbrains.annotations.NotNull()
            java.lang.String message) {
                return null;
            }
            
            @java.lang.Override()
            public boolean equals(@org.jetbrains.annotations.Nullable()
            java.lang.Object other) {
                return false;
            }
            
            @java.lang.Override()
            public int hashCode() {
                return 0;
            }
            
            @java.lang.Override()
            @org.jetbrains.annotations.NotNull()
            public java.lang.String toString() {
                return null;
            }
            
            public Error(@org.jetbrains.annotations.NotNull()
            java.lang.String message) {
            }
            
            @org.jetbrains.annotations.NotNull()
            public final java.lang.String getMessage() {
                return null;
            }
        }
        
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u000e\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0003\b\u0087\b\u0018\u00002\u00020\u0001B%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\u0006\u00a2\u0006\u0002\u0010\bJ\t\u0010\u000f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0010\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0011\u001a\u00020\u0006H\u00c6\u0003J\t\u0010\u0012\u001a\u00020\u0006H\u00c6\u0003J1\u0010\u0013\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\u0006H\u00c6\u0001J\u0013\u0010\u0014\u001a\u00020\u00152\b\u0010\u0016\u001a\u0004\u0018\u00010\u0017H\u00d6\u0003J\t\u0010\u0018\u001a\u00020\u0006H\u00d6\u0001J\t\u0010\u0019\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0007\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\fR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\n\u00a8\u0006\u001a"}, d2 = {"Lcom/securevote/remote/ui/SVRViewModel$SubmissionResult$Success;", "Lcom/securevote/remote/ui/SVRViewModel$SubmissionResult;", "voteRecordId", "", "confirmationHash", "submissionSequence", "", "remainingSubmissions", "(Ljava/lang/String;Ljava/lang/String;II)V", "getConfirmationHash", "()Ljava/lang/String;", "getRemainingSubmissions", "()I", "getSubmissionSequence", "getVoteRecordId", "component1", "component2", "component3", "component4", "copy", "equals", "", "other", "", "hashCode", "toString", "app_debug"})
        public static final class Success extends com.securevote.remote.ui.SVRViewModel.SubmissionResult {
            @org.jetbrains.annotations.NotNull()
            private final java.lang.String voteRecordId = null;
            @org.jetbrains.annotations.NotNull()
            private final java.lang.String confirmationHash = null;
            private final int submissionSequence = 0;
            private final int remainingSubmissions = 0;
            
            @org.jetbrains.annotations.NotNull()
            public final java.lang.String component1() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final java.lang.String component2() {
                return null;
            }
            
            public final int component3() {
                return 0;
            }
            
            public final int component4() {
                return 0;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final com.securevote.remote.ui.SVRViewModel.SubmissionResult.Success copy(@org.jetbrains.annotations.NotNull()
            java.lang.String voteRecordId, @org.jetbrains.annotations.NotNull()
            java.lang.String confirmationHash, int submissionSequence, int remainingSubmissions) {
                return null;
            }
            
            @java.lang.Override()
            public boolean equals(@org.jetbrains.annotations.Nullable()
            java.lang.Object other) {
                return false;
            }
            
            @java.lang.Override()
            public int hashCode() {
                return 0;
            }
            
            @java.lang.Override()
            @org.jetbrains.annotations.NotNull()
            public java.lang.String toString() {
                return null;
            }
            
            public Success(@org.jetbrains.annotations.NotNull()
            java.lang.String voteRecordId, @org.jetbrains.annotations.NotNull()
            java.lang.String confirmationHash, int submissionSequence, int remainingSubmissions) {
            }
            
            @org.jetbrains.annotations.NotNull()
            public final java.lang.String getVoteRecordId() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final java.lang.String getConfirmationHash() {
                return null;
            }
            
            public final int getSubmissionSequence() {
                return 0;
            }
            
            public final int getRemainingSubmissions() {
                return 0;
            }
        }
    }
}