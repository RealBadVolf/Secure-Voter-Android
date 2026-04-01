package com.securevote.remote.data.api;

import com.securevote.remote.data.models.*;
import retrofit2.Response;
import retrofit2.http.*;

/**
 * sv-remote-gateway REST API.
 * All endpoints use TLS 1.3 with certificate pinning.
 * Base URL: https://remote.securevote.gov/api/v1
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000h\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\u001e\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\u0007J2\u0010\b\u001a\b\u0012\u0004\u0012\u00020\t0\u00032\b\b\u0001\u0010\n\u001a\u00020\u000b2\b\b\u0001\u0010\f\u001a\u00020\u000b2\b\b\u0001\u0010\r\u001a\u00020\u000bH\u00a7@\u00a2\u0006\u0002\u0010\u000eJ(\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00100\u00032\b\b\u0001\u0010\u0011\u001a\u00020\u000b2\b\b\u0001\u0010\f\u001a\u00020\u000bH\u00a7@\u00a2\u0006\u0002\u0010\u0012J\u001e\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00100\u00032\b\b\u0001\u0010\u0014\u001a\u00020\u0015H\u00a7@\u00a2\u0006\u0002\u0010\u0016J(\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00180\u00032\b\b\u0001\u0010\n\u001a\u00020\u000b2\b\b\u0001\u0010\u0005\u001a\u00020\u0019H\u00a7@\u00a2\u0006\u0002\u0010\u001aJ(\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u00100\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u001c2\b\b\u0003\u0010\u001d\u001a\u00020\u000bH\u00a7@\u00a2\u0006\u0002\u0010\u001eJ\u001e\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\u00100\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u001cH\u00a7@\u00a2\u0006\u0002\u0010 J\u001e\u0010!\u001a\b\u0012\u0004\u0012\u00020\"0\u00032\b\b\u0001\u0010#\u001a\u00020$H\u00a7@\u00a2\u0006\u0002\u0010%J\u001e\u0010&\u001a\b\u0012\u0004\u0012\u00020\'0\u00032\b\b\u0001\u0010\u0005\u001a\u00020(H\u00a7@\u00a2\u0006\u0002\u0010)\u00a8\u0006*"}, d2 = {"Lcom/securevote/remote/data/api/SVRGatewayApi;", "", "authenticateSession", "Lretrofit2/Response;", "Lcom/securevote/remote/data/api/SessionAuthResponse;", "request", "Lcom/securevote/remote/data/models/VotingAuthRequest;", "(Lcom/securevote/remote/data/models/VotingAuthRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getBallotDefinition", "Lcom/securevote/remote/data/models/BallotDefinition;", "sessionToken", "", "electionId", "precinctId", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getRegistrationStatus", "Lcom/securevote/remote/data/models/DeviceRegistrationResponse;", "token", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "ipvcRegister", "payload", "Lcom/securevote/remote/data/api/IPVCRegistrationRequest;", "(Lcom/securevote/remote/data/api/IPVCRegistrationRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "issueBlindToken", "Lcom/securevote/remote/data/models/BlindTokenResponse;", "Lcom/securevote/remote/data/models/BlindTokenRequest;", "(Ljava/lang/String;Lcom/securevote/remote/data/models/BlindTokenRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "reRegisterDevice", "Lcom/securevote/remote/data/models/DeviceRegistrationRequest;", "flag", "(Lcom/securevote/remote/data/models/DeviceRegistrationRequest;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "registerDevice", "(Lcom/securevote/remote/data/models/DeviceRegistrationRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "submitVote", "Lcom/securevote/remote/data/models/VoteSubmissionResponse;", "voteCast", "Lcom/securevote/remote/data/models/RemoteVoteCast;", "(Lcom/securevote/remote/data/models/RemoteVoteCast;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "verifyVote", "Lcom/securevote/remote/data/models/VerificationResponse;", "Lcom/securevote/remote/data/api/VerifyRequest;", "(Lcom/securevote/remote/data/api/VerifyRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public abstract interface SVRGatewayApi {
    
    /**
     * Register a new device for remote voting.
     * Three-factor auth must be completed on-device before calling.
     */
    @retrofit2.http.POST(value = "register/device")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object registerDevice(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.securevote.remote.data.models.DeviceRegistrationRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.securevote.remote.data.models.DeviceRegistrationResponse>> $completion);
    
    /**
     * Re-register on a new device (lost/replaced phone).
     * Server performs additional photo comparison against original registration.
     * Rate limited: max 2 re-registrations per election.
     * Triggers 4-hour cooling period.
     */
    @retrofit2.http.POST(value = "register/device/reregister")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object reRegisterDevice(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.securevote.remote.data.models.DeviceRegistrationRequest request, @retrofit2.http.Header(value = "X-Reregistration")
    @org.jetbrains.annotations.NotNull()
    java.lang.String flag, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.securevote.remote.data.models.DeviceRegistrationResponse>> $completion);
    
    /**
     * IPVC agent-assisted registration.
     * Called by the IPVC workstation, not the voter's phone.
     * The QR code scanned by the agent contains the device binding data.
     */
    @retrofit2.http.POST(value = "register/ipvc")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object ipvcRegister(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.securevote.remote.data.api.IPVCRegistrationRequest payload, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.securevote.remote.data.models.DeviceRegistrationResponse>> $completion);
    
    /**
     * Check registration status.
     */
    @retrofit2.http.GET(value = "register/status")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getRegistrationStatus(@retrofit2.http.Header(value = "Authorization")
    @org.jetbrains.annotations.NotNull()
    java.lang.String token, @retrofit2.http.Query(value = "election_id")
    @org.jetbrains.annotations.NotNull()
    java.lang.String electionId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.securevote.remote.data.models.DeviceRegistrationResponse>> $completion);
    
    /**
     * Authenticate for a voting session.
     * Validates PIN, biometric hash, device attestation, and registration token.
     * Returns session token for blind token issuance.
     */
    @retrofit2.http.POST(value = "auth/session")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object authenticateSession(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.securevote.remote.data.models.VotingAuthRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.securevote.remote.data.api.SessionAuthResponse>> $completion);
    
    /**
     * Request blind token issuance.
     * Must be called after successful session authentication.
     */
    @retrofit2.http.POST(value = "auth/token")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object issueBlindToken(@retrofit2.http.Header(value = "Authorization")
    @org.jetbrains.annotations.NotNull()
    java.lang.String sessionToken, @retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.securevote.remote.data.models.BlindTokenRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.securevote.remote.data.models.BlindTokenResponse>> $completion);
    
    /**
     * Fetch the Ballot Definition File for the voter's precinct.
     * Returns the signed BDF with all races, candidates, and hashes.
     */
    @retrofit2.http.GET(value = "ballot/bdf")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getBallotDefinition(@retrofit2.http.Header(value = "Authorization")
    @org.jetbrains.annotations.NotNull()
    java.lang.String sessionToken, @retrofit2.http.Query(value = "election_id")
    @org.jetbrains.annotations.NotNull()
    java.lang.String electionId, @retrofit2.http.Query(value = "precinct_id")
    @org.jetbrains.annotations.NotNull()
    java.lang.String precinctId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.securevote.remote.data.models.BallotDefinition>> $completion);
    
    /**
     * Submit an encrypted vote.
     * The server cannot read the ballot contents — they're PQ-encrypted
     * with keys held only by the air-gapped tabulation HSM.
     */
    @retrofit2.http.POST(value = "ballot/submit")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object submitVote(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.securevote.remote.data.models.RemoteVoteCast voteCast, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.securevote.remote.data.models.VoteSubmissionResponse>> $completion);
    
    /**
     * Verify a vote exists in the Merkle tree.
     * Public endpoint, rate-limited.
     */
    @retrofit2.http.POST(value = "verify")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object verifyVote(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.securevote.remote.data.api.VerifyRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.securevote.remote.data.models.VerificationResponse>> $completion);
    
    /**
     * sv-remote-gateway REST API.
     * All endpoints use TLS 1.3 with certificate pinning.
     * Base URL: https://remote.securevote.gov/api/v1
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 3, xi = 48)
    public static final class DefaultImpls {
    }
}