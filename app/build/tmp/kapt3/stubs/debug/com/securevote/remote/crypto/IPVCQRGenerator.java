package com.securevote.remote.crypto;

import android.graphics.Bitmap;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.QRCodeWriter;
import com.securevote.remote.data.models.IPVCQRPayload;
import kotlinx.coroutines.flow.Flow;
import java.security.SecureRandom;

/**
 * IPVC QR Code Generator with 10-second TOTP-like rotation.
 *
 * SECURITY DESIGN:
 *  - QR code regenerates every 10 seconds with a fresh nonce
 *  - Each QR contains an HMAC proving device possession (bound to hardware key)
 *  - Each QR contains an expiry timestamp (current + 10 seconds)
 *  - The IPVC admin's scanner validates: nonce freshness, HMAC, expiry
 *  - A screenshot or photo becomes invalid within 10 seconds
 *  - Even if captured, the HMAC cannot be forged without the device's private key
 *  - FLAG_SECURE prevents screenshots anyway — this is defense-in-depth
 *
 * ATTACK MITIGATED:
 *  Voter screenshots QR → texts to corrupt admin 500 miles away → admin scans photo
 *  Result: QR is expired by the time admin receives it. Attack fails.
 *  Even with a fast relay: HMAC is bound to the device key. Admin's scanner
 *  verifies the HMAC against the device public key registered with sv-remote-auth.
 *  The device must be physically present for the HMAC to validate in real-time.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0012\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c7\u0002\u0018\u00002\u00020\u0001:\u0001\u0019B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\b\u0010\b\u001a\u00020\tH\u0002J\u0010\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\tH\u0002J.\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000e2\u0006\u0010\u0010\u001a\u00020\t2\u0006\u0010\u0011\u001a\u00020\t2\u0006\u0010\u0012\u001a\u00020\u00132\b\b\u0002\u0010\u0014\u001a\u00020\tJ\u0018\u0010\u0015\u001a\u0004\u0018\u00010\t2\u0006\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u0013R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001a"}, d2 = {"Lcom/securevote/remote/crypto/IPVCQRGenerator;", "", "()V", "NONCE_BYTES", "", "QR_ROTATION_INTERVAL_MS", "", "QR_SIZE", "generateNonce", "", "generateQRBitmap", "Landroid/graphics/Bitmap;", "content", "generateRotatingQRCodes", "Lkotlinx/coroutines/flow/Flow;", "Lcom/securevote/remote/crypto/IPVCQRGenerator$QRFrame;", "voterDocumentHash", "electionId", "deviceKeyMaterial", "", "appVersion", "validateQRPayload", "payload", "Lcom/securevote/remote/data/models/IPVCQRPayload;", "devicePublicKeyForHMAC", "QRFrame", "app_debug"})
public final class IPVCQRGenerator {
    private static final long QR_ROTATION_INTERVAL_MS = 10000L;
    private static final int QR_SIZE = 512;
    private static final int NONCE_BYTES = 16;
    @org.jetbrains.annotations.NotNull()
    public static final com.securevote.remote.crypto.IPVCQRGenerator INSTANCE = null;
    
    private IPVCQRGenerator() {
        super();
    }
    
    /**
     * Generate a continuous flow of QR code bitmaps that rotate every 10 seconds.
     * Each emission is a fresh QR with a new nonce, timestamp, and HMAC.
     *
     * @param voterDocumentHash SHA-256 of the voter's ID document number
     * @param electionId Current election ID
     * @param deviceKeyMaterial Key material for HMAC computation (derived from device key)
     */
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<com.securevote.remote.crypto.IPVCQRGenerator.QRFrame> generateRotatingQRCodes(@org.jetbrains.annotations.NotNull()
    java.lang.String voterDocumentHash, @org.jetbrains.annotations.NotNull()
    java.lang.String electionId, @org.jetbrains.annotations.NotNull()
    byte[] deviceKeyMaterial, @org.jetbrains.annotations.NotNull()
    java.lang.String appVersion) {
        return null;
    }
    
    /**
     * Generate a QR code bitmap from a string payload.
     * Uses ZXing with high error correction (L level is sufficient since
     * the QR is displayed on a clean screen, not printed on paper).
     */
    private final android.graphics.Bitmap generateQRBitmap(java.lang.String content) {
        return null;
    }
    
    /**
     * Generate a cryptographically random nonce (16 bytes, hex-encoded).
     */
    private final java.lang.String generateNonce() {
        return null;
    }
    
    /**
     * Validate a scanned QR payload (used by IPVC admin scanner app).
     * Returns null if valid, or an error message if invalid.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String validateQRPayload(@org.jetbrains.annotations.NotNull()
    com.securevote.remote.data.models.IPVCQRPayload payload, @org.jetbrains.annotations.NotNull()
    byte[] devicePublicKeyForHMAC) {
        return null;
    }
    
    /**
     * A single frame of the rotating QR display.
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u000f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\tJ\t\u0010\u0011\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0012\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0013\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\u0014\u001a\u00020\u0007H\u00c6\u0003J1\u0010\u0015\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u0007H\u00c6\u0001J\u0013\u0010\u0016\u001a\u00020\u00172\b\u0010\u0018\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0019\u001a\u00020\u001aH\u00d6\u0001J\t\u0010\u001b\u001a\u00020\u001cH\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\b\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u000f\u00a8\u0006\u001d"}, d2 = {"Lcom/securevote/remote/crypto/IPVCQRGenerator$QRFrame;", "", "bitmap", "Landroid/graphics/Bitmap;", "payload", "Lcom/securevote/remote/data/models/IPVCQRPayload;", "validForMs", "", "sequenceNumber", "(Landroid/graphics/Bitmap;Lcom/securevote/remote/data/models/IPVCQRPayload;JJ)V", "getBitmap", "()Landroid/graphics/Bitmap;", "getPayload", "()Lcom/securevote/remote/data/models/IPVCQRPayload;", "getSequenceNumber", "()J", "getValidForMs", "component1", "component2", "component3", "component4", "copy", "equals", "", "other", "hashCode", "", "toString", "", "app_debug"})
    public static final class QRFrame {
        @org.jetbrains.annotations.NotNull()
        private final android.graphics.Bitmap bitmap = null;
        @org.jetbrains.annotations.NotNull()
        private final com.securevote.remote.data.models.IPVCQRPayload payload = null;
        private final long validForMs = 0L;
        private final long sequenceNumber = 0L;
        
        @org.jetbrains.annotations.NotNull()
        public final android.graphics.Bitmap component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.securevote.remote.data.models.IPVCQRPayload component2() {
            return null;
        }
        
        public final long component3() {
            return 0L;
        }
        
        public final long component4() {
            return 0L;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.securevote.remote.crypto.IPVCQRGenerator.QRFrame copy(@org.jetbrains.annotations.NotNull()
        android.graphics.Bitmap bitmap, @org.jetbrains.annotations.NotNull()
        com.securevote.remote.data.models.IPVCQRPayload payload, long validForMs, long sequenceNumber) {
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
        
        public QRFrame(@org.jetbrains.annotations.NotNull()
        android.graphics.Bitmap bitmap, @org.jetbrains.annotations.NotNull()
        com.securevote.remote.data.models.IPVCQRPayload payload, long validForMs, long sequenceNumber) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final android.graphics.Bitmap getBitmap() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.securevote.remote.data.models.IPVCQRPayload getPayload() {
            return null;
        }
        
        public final long getValidForMs() {
            return 0L;
        }
        
        public final long getSequenceNumber() {
            return 0L;
        }
    }
}