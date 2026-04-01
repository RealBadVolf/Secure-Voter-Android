package com.securevote.remote.biometric;

import android.graphics.Bitmap;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.securevote.remote.crypto.SVRCrypto;

/**
 * Biometric verification engine for SVR.
 *
 * Implements:
 *  1. Face detection using ML Kit
 *  2. Randomized liveness challenges (anti-deepfake)
 *  3. Dual-model matching against ID chip photo
 *  4. Continuous presence monitoring during ballot session
 *  5. Biometric hash generation (one-way, never stored as template)
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0012\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c7\u0002\u0018\u00002\u00020\u0001:\u0002\u001d\u001eB\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0016\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0086@\u00a2\u0006\u0002\u0010\u000bJ\u000e\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000fJ\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00120\u0011J\u0016\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u000f2\u0006\u0010\u0016\u001a\u00020\u000fJ\b\u0010\u0017\u001a\u00020\u0004H\u0002J\u001c\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u00122\f\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u001c0\u0011R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001f"}, d2 = {"Lcom/securevote/remote/biometric/BiometricEngine;", "", "()V", "MATCH_THRESHOLD_IN_PERSON", "", "MATCH_THRESHOLD_REMOTE", "REJECT_THRESHOLD", "detectFace", "Lcom/securevote/remote/biometric/FaceDetectionResult;", "bitmap", "Landroid/graphics/Bitmap;", "(Landroid/graphics/Bitmap;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "generateBiometricHash", "", "templateData", "", "generateChallengeSequence", "", "Lcom/securevote/remote/biometric/BiometricEngine$LivenessChallenge;", "matchAgainstIDPhoto", "Lcom/securevote/remote/biometric/BiometricEngine$BiometricResult;", "liveTemplate", "idPhotoTemplate", "simulateModelScore", "verifyChallenge", "", "challenge", "frames", "Lcom/securevote/remote/biometric/FaceDetectionResult$Detected;", "BiometricResult", "LivenessChallenge", "app_debug"})
public final class BiometricEngine {
    private static final float MATCH_THRESHOLD_REMOTE = 0.85F;
    private static final float MATCH_THRESHOLD_IN_PERSON = 0.75F;
    private static final float REJECT_THRESHOLD = 0.4F;
    @org.jetbrains.annotations.NotNull()
    public static final com.securevote.remote.biometric.BiometricEngine INSTANCE = null;
    
    private BiometricEngine() {
        super();
    }
    
    /**
     * Generate a random liveness challenge sequence.
     * 3 challenges from the pool, never repeating, different every session.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.securevote.remote.biometric.BiometricEngine.LivenessChallenge> generateChallengeSequence() {
        return null;
    }
    
    /**
     * Detect face in a camera frame using ML Kit.
     * Returns true if exactly one face is detected with sufficient quality.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object detectFace(@org.jetbrains.annotations.NotNull()
    android.graphics.Bitmap bitmap, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.securevote.remote.biometric.FaceDetectionResult> $completion) {
        return null;
    }
    
    /**
     * Verify a liveness challenge was performed.
     * Analyzes facial landmarks across multiple frames to confirm the challenge was real.
     */
    public final boolean verifyChallenge(@org.jetbrains.annotations.NotNull()
    com.securevote.remote.biometric.BiometricEngine.LivenessChallenge challenge, @org.jetbrains.annotations.NotNull()
    java.util.List<com.securevote.remote.biometric.FaceDetectionResult.Detected> frames) {
        return false;
    }
    
    /**
     * Run the full dual-model biometric match.
     *
     * Model A: Geometric face template comparison (lightweight, runs on CPU)
     * Model B: Deep embedding comparison (ArcFace-class, runs on GPU/NNAPI)
     *
     * Both models must exceed the threshold for auto-pass.
     * If either is uncertain but above reject threshold → IPVC fallback.
     * If both below reject threshold → rejected.
     */
    @org.jetbrains.annotations.NotNull()
    public final com.securevote.remote.biometric.BiometricEngine.BiometricResult matchAgainstIDPhoto(@org.jetbrains.annotations.NotNull()
    byte[] liveTemplate, @org.jetbrains.annotations.NotNull()
    byte[] idPhotoTemplate) {
        return null;
    }
    
    /**
     * Generate a one-way hash of a biometric template.
     * This hash is stored in the vote record for audit purposes.
     * It CANNOT be reversed to reconstruct the voter's face.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String generateBiometricHash(@org.jetbrains.annotations.NotNull()
    byte[] templateData) {
        return null;
    }
    
    private final float simulateModelScore() {
        return 0.0F;
    }
    
    /**
     * Result of a biometric verification attempt.
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b7\u0018\u00002\u00020\u0001:\u0004\u0003\u0004\u0005\u0006B\u0007\b\u0004\u00a2\u0006\u0002\u0010\u0002\u0082\u0001\u0004\u0007\b\t\n\u00a8\u0006\u000b"}, d2 = {"Lcom/securevote/remote/biometric/BiometricEngine$BiometricResult;", "", "()V", "Error", "Match", "Rejected", "Uncertain", "Lcom/securevote/remote/biometric/BiometricEngine$BiometricResult$Error;", "Lcom/securevote/remote/biometric/BiometricEngine$BiometricResult$Match;", "Lcom/securevote/remote/biometric/BiometricEngine$BiometricResult$Rejected;", "Lcom/securevote/remote/biometric/BiometricEngine$BiometricResult$Uncertain;", "app_debug"})
    public static abstract class BiometricResult {
        
        private BiometricResult() {
            super();
        }
        
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0003\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\t\u0010\u0007\u001a\u00020\u0003H\u00c6\u0003J\u0013\u0010\b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u00d6\u0003J\t\u0010\r\u001a\u00020\u000eH\u00d6\u0001J\t\u0010\u000f\u001a\u00020\u0010H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0011"}, d2 = {"Lcom/securevote/remote/biometric/BiometricEngine$BiometricResult$Error;", "Lcom/securevote/remote/biometric/BiometricEngine$BiometricResult;", "exception", "", "(Ljava/lang/Throwable;)V", "getException", "()Ljava/lang/Throwable;", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "", "app_debug"})
        public static final class Error extends com.securevote.remote.biometric.BiometricEngine.BiometricResult {
            @org.jetbrains.annotations.NotNull()
            private final java.lang.Throwable exception = null;
            
            @org.jetbrains.annotations.NotNull()
            public final java.lang.Throwable component1() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final com.securevote.remote.biometric.BiometricEngine.BiometricResult.Error copy(@org.jetbrains.annotations.NotNull()
            java.lang.Throwable exception) {
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
            java.lang.Throwable exception) {
            }
            
            @org.jetbrains.annotations.NotNull()
            public final java.lang.Throwable getException() {
                return null;
            }
        }
        
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\r\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ\t\u0010\u000f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0010\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0011\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0012\u001a\u00020\u0007H\u00c6\u0003J1\u0010\u0013\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u0007H\u00c6\u0001J\u0013\u0010\u0014\u001a\u00020\u00152\b\u0010\u0016\u001a\u0004\u0018\u00010\u0017H\u00d6\u0003J\t\u0010\u0018\u001a\u00020\u0019H\u00d6\u0001J\t\u0010\u001a\u001a\u00020\u0007H\u00d6\u0001R\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\fR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\f\u00a8\u0006\u001b"}, d2 = {"Lcom/securevote/remote/biometric/BiometricEngine$BiometricResult$Match;", "Lcom/securevote/remote/biometric/BiometricEngine$BiometricResult;", "modelAScore", "", "modelBScore", "livenessConfidence", "biometricHash", "", "(FFFLjava/lang/String;)V", "getBiometricHash", "()Ljava/lang/String;", "getLivenessConfidence", "()F", "getModelAScore", "getModelBScore", "component1", "component2", "component3", "component4", "copy", "equals", "", "other", "", "hashCode", "", "toString", "app_debug"})
        public static final class Match extends com.securevote.remote.biometric.BiometricEngine.BiometricResult {
            private final float modelAScore = 0.0F;
            private final float modelBScore = 0.0F;
            private final float livenessConfidence = 0.0F;
            @org.jetbrains.annotations.NotNull()
            private final java.lang.String biometricHash = null;
            
            public final float component1() {
                return 0.0F;
            }
            
            public final float component2() {
                return 0.0F;
            }
            
            public final float component3() {
                return 0.0F;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final java.lang.String component4() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final com.securevote.remote.biometric.BiometricEngine.BiometricResult.Match copy(float modelAScore, float modelBScore, float livenessConfidence, @org.jetbrains.annotations.NotNull()
            java.lang.String biometricHash) {
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
            
            public Match(float modelAScore, float modelBScore, float livenessConfidence, @org.jetbrains.annotations.NotNull()
            java.lang.String biometricHash) {
            }
            
            public final float getModelAScore() {
                return 0.0F;
            }
            
            public final float getModelBScore() {
                return 0.0F;
            }
            
            public final float getLivenessConfidence() {
                return 0.0F;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final java.lang.String getBiometricHash() {
                return null;
            }
        }
        
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\t\u0010\u0007\u001a\u00020\u0003H\u00c6\u0003J\u0013\u0010\b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u00d6\u0003J\t\u0010\r\u001a\u00020\u000eH\u00d6\u0001J\t\u0010\u000f\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0010"}, d2 = {"Lcom/securevote/remote/biometric/BiometricEngine$BiometricResult$Rejected;", "Lcom/securevote/remote/biometric/BiometricEngine$BiometricResult;", "reason", "", "(Ljava/lang/String;)V", "getReason", "()Ljava/lang/String;", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "app_debug"})
        public static final class Rejected extends com.securevote.remote.biometric.BiometricEngine.BiometricResult {
            @org.jetbrains.annotations.NotNull()
            private final java.lang.String reason = null;
            
            @org.jetbrains.annotations.NotNull()
            public final java.lang.String component1() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final com.securevote.remote.biometric.BiometricEngine.BiometricResult.Rejected copy(@org.jetbrains.annotations.NotNull()
            java.lang.String reason) {
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
            
            public Rejected(@org.jetbrains.annotations.NotNull()
            java.lang.String reason) {
            }
            
            @org.jetbrains.annotations.NotNull()
            public final java.lang.String getReason() {
                return null;
            }
        }
        
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u000b\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\u0002\u0010\u0007J\t\u0010\r\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u000e\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u000f\u001a\u00020\u0006H\u00c6\u0003J\'\u0010\u0010\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u0006H\u00c6\u0001J\u0013\u0010\u0011\u001a\u00020\u00122\b\u0010\u0013\u001a\u0004\u0018\u00010\u0014H\u00d6\u0003J\t\u0010\u0015\u001a\u00020\u0016H\u00d6\u0001J\t\u0010\u0017\u001a\u00020\u0006H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\tR\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\f\u00a8\u0006\u0018"}, d2 = {"Lcom/securevote/remote/biometric/BiometricEngine$BiometricResult$Uncertain;", "Lcom/securevote/remote/biometric/BiometricEngine$BiometricResult;", "modelAScore", "", "modelBScore", "reason", "", "(FFLjava/lang/String;)V", "getModelAScore", "()F", "getModelBScore", "getReason", "()Ljava/lang/String;", "component1", "component2", "component3", "copy", "equals", "", "other", "", "hashCode", "", "toString", "app_debug"})
        public static final class Uncertain extends com.securevote.remote.biometric.BiometricEngine.BiometricResult {
            private final float modelAScore = 0.0F;
            private final float modelBScore = 0.0F;
            @org.jetbrains.annotations.NotNull()
            private final java.lang.String reason = null;
            
            public final float component1() {
                return 0.0F;
            }
            
            public final float component2() {
                return 0.0F;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final java.lang.String component3() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final com.securevote.remote.biometric.BiometricEngine.BiometricResult.Uncertain copy(float modelAScore, float modelBScore, @org.jetbrains.annotations.NotNull()
            java.lang.String reason) {
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
            
            public Uncertain(float modelAScore, float modelBScore, @org.jetbrains.annotations.NotNull()
            java.lang.String reason) {
            }
            
            public final float getModelAScore() {
                return 0.0F;
            }
            
            public final float getModelBScore() {
                return 0.0F;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final java.lang.String getReason() {
                return null;
            }
        }
    }
    
    /**
     * Available liveness challenges. A random subset of 3 is selected per session.
     * The randomization defeats pre-recorded deepfake videos.
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0002\b\r\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0017\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nj\u0002\b\u000bj\u0002\b\fj\u0002\b\rj\u0002\b\u000ej\u0002\b\u000fj\u0002\b\u0010j\u0002\b\u0011\u00a8\u0006\u0012"}, d2 = {"Lcom/securevote/remote/biometric/BiometricEngine$LivenessChallenge;", "", "instruction", "", "durationMs", "", "(Ljava/lang/String;ILjava/lang/String;J)V", "getDurationMs", "()J", "getInstruction", "()Ljava/lang/String;", "BLINK", "HEAD_LEFT", "HEAD_RIGHT", "SMILE", "RAISE_EYEBROWS", "OPEN_MOUTH", "NOD", "app_debug"})
    public static enum LivenessChallenge {
        /*public static final*/ BLINK /* = new BLINK(null, 0L) */,
        /*public static final*/ HEAD_LEFT /* = new HEAD_LEFT(null, 0L) */,
        /*public static final*/ HEAD_RIGHT /* = new HEAD_RIGHT(null, 0L) */,
        /*public static final*/ SMILE /* = new SMILE(null, 0L) */,
        /*public static final*/ RAISE_EYEBROWS /* = new RAISE_EYEBROWS(null, 0L) */,
        /*public static final*/ OPEN_MOUTH /* = new OPEN_MOUTH(null, 0L) */,
        /*public static final*/ NOD /* = new NOD(null, 0L) */;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String instruction = null;
        private final long durationMs = 0L;
        
        LivenessChallenge(java.lang.String instruction, long durationMs) {
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getInstruction() {
            return null;
        }
        
        public final long getDurationMs() {
            return 0L;
        }
        
        @org.jetbrains.annotations.NotNull()
        public static kotlin.enums.EnumEntries<com.securevote.remote.biometric.BiometricEngine.LivenessChallenge> getEntries() {
            return null;
        }
    }
}