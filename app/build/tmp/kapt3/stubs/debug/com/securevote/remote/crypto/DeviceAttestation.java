package com.securevote.remote.crypto;

import android.content.Context;
import com.google.android.play.core.integrity.IntegrityManagerFactory;
import com.google.android.play.core.integrity.IntegrityTokenRequest;

/**
 * Device Attestation using Google Play Integrity API.
 *
 * Proves to sv-remote-gateway that this app is:
 *  1. Running on genuine, unmodified Android hardware
 *  2. Not rooted or running a custom ROM
 *  3. Not an emulator
 *  4. The genuine SecureVote Remote APK (not a repackaged/modified version)
 *
 * The attestation token is sent to the server, which verifies it against
 * Google's servers. This is the mobile equivalent of the TPM attestation
 * in the in-person voting machine.
 *
 * LIMITATIONS (acknowledged in ABSENTEE.md):
 *  - Requires Google Play Services (not available on some devices)
 *  - Google is a trusted third party (the in-person TPM has no such dependency)
 *  - A sufficiently advanced root hide (e.g., Magisk with DenyList) may pass
 *  - This is weaker than custom hardware TPM — accepted as a tradeoff vs. paper mail
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u00c7\u0002\u0018\u00002\u00020\u0001:\u0002\u000f\u0010B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006J\u001e\u0010\u0007\u001a\u00020\b2\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\t\u001a\u00020\bH\u0086@\u00a2\u0006\u0002\u0010\nJ\u0006\u0010\u000b\u001a\u00020\bJ\u000e\u0010\f\u001a\u00020\r2\u0006\u0010\u0005\u001a\u00020\u0006J\u000e\u0010\u000e\u001a\u00020\r2\u0006\u0010\u0005\u001a\u00020\u0006\u00a8\u0006\u0011"}, d2 = {"Lcom/securevote/remote/crypto/DeviceAttestation;", "", "()V", "checkDeviceCompatibility", "Lcom/securevote/remote/crypto/DeviceAttestation$DeviceCompatibility;", "context", "Landroid/content/Context;", "getIntegrityToken", "", "nonce", "(Landroid/content/Context;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getKeyAttestationChain", "hasNFC", "", "hasStrongBox", "AttestationException", "DeviceCompatibility", "app_debug"})
public final class DeviceAttestation {
    @org.jetbrains.annotations.NotNull()
    public static final com.securevote.remote.crypto.DeviceAttestation INSTANCE = null;
    
    private DeviceAttestation() {
        super();
    }
    
    /**
     * Request a fresh Play Integrity attestation token.
     *
     * @param context Application context
     * @param nonce A server-provided nonce to prevent replay attacks.
     *             The server generates this and expects it back in the token.
     * @return Base64-encoded integrity token for server verification
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getIntegrityToken(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String nonce, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.String> $completion) {
        return null;
    }
    
    /**
     * Get hardware-backed key attestation certificate chain.
     * This proves the device key was generated inside a genuine Android Keystore
     * (TEE or StrongBox).
     *
     * @return Base64-encoded X.509 certificate chain
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getKeyAttestationChain() {
        return null;
    }
    
    /**
     * Check if the device has StrongBox (hardware security module).
     * StrongBox provides the highest level of key protection on Android.
     */
    public final boolean hasStrongBox(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return false;
    }
    
    /**
     * Check if NFC is available on this device.
     */
    public final boolean hasNFC(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return false;
    }
    
    /**
     * Check minimum device requirements for SVR.
     */
    @org.jetbrains.annotations.NotNull()
    public final com.securevote.remote.crypto.DeviceAttestation.DeviceCompatibility checkDeviceCompatibility(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0003\n\u0002\b\u0002\b\u0007\u0018\u00002\u00060\u0001j\u0002`\u0002B\u0019\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u00a2\u0006\u0002\u0010\u0007\u00a8\u0006\b"}, d2 = {"Lcom/securevote/remote/crypto/DeviceAttestation$AttestationException;", "Ljava/lang/Exception;", "Lkotlin/Exception;", "message", "", "cause", "", "(Ljava/lang/String;Ljava/lang/Throwable;)V", "app_debug"})
    public static final class AttestationException extends java.lang.Exception {
        
        public AttestationException(@org.jetbrains.annotations.NotNull()
        java.lang.String message, @org.jetbrains.annotations.Nullable()
        java.lang.Throwable cause) {
            super();
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\b\n\u0000\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\u0016\b\u0087\b\u0018\u00002\u00020\u0001B;\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\n\u00a2\u0006\u0002\u0010\fJ\t\u0010\u0016\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0017\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0018\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0019\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\bH\u00c6\u0003J\u000f\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u000b0\nH\u00c6\u0003JK\u0010\u001c\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\b2\u000e\b\u0002\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\nH\u00c6\u0001J\u0013\u0010\u001d\u001a\u00020\u00032\b\u0010\u001e\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001f\u001a\u00020\bH\u00d6\u0001J\t\u0010 \u001a\u00020\u000bH\u00d6\u0001R\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0010R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0010R\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0010R\u0017\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015\u00a8\u0006!"}, d2 = {"Lcom/securevote/remote/crypto/DeviceAttestation$DeviceCompatibility;", "", "compatible", "", "hasNFC", "hasStrongBox", "hasCamera", "apiLevel", "", "warnings", "", "", "(ZZZZILjava/util/List;)V", "getApiLevel", "()I", "getCompatible", "()Z", "getHasCamera", "getHasNFC", "getHasStrongBox", "getWarnings", "()Ljava/util/List;", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "equals", "other", "hashCode", "toString", "app_debug"})
    public static final class DeviceCompatibility {
        private final boolean compatible = false;
        private final boolean hasNFC = false;
        private final boolean hasStrongBox = false;
        private final boolean hasCamera = false;
        private final int apiLevel = 0;
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<java.lang.String> warnings = null;
        
        public final boolean component1() {
            return false;
        }
        
        public final boolean component2() {
            return false;
        }
        
        public final boolean component3() {
            return false;
        }
        
        public final boolean component4() {
            return false;
        }
        
        public final int component5() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<java.lang.String> component6() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.securevote.remote.crypto.DeviceAttestation.DeviceCompatibility copy(boolean compatible, boolean hasNFC, boolean hasStrongBox, boolean hasCamera, int apiLevel, @org.jetbrains.annotations.NotNull()
        java.util.List<java.lang.String> warnings) {
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
        
        public DeviceCompatibility(boolean compatible, boolean hasNFC, boolean hasStrongBox, boolean hasCamera, int apiLevel, @org.jetbrains.annotations.NotNull()
        java.util.List<java.lang.String> warnings) {
            super();
        }
        
        public final boolean getCompatible() {
            return false;
        }
        
        public final boolean getHasNFC() {
            return false;
        }
        
        public final boolean getHasStrongBox() {
            return false;
        }
        
        public final boolean getHasCamera() {
            return false;
        }
        
        public final int getApiLevel() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<java.lang.String> getWarnings() {
            return null;
        }
    }
}