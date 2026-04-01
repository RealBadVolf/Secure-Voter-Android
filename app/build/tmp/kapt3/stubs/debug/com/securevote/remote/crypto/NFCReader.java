package com.securevote.remote.crypto;

import android.app.Activity;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import com.securevote.remote.data.models.VoterIdentity;

/**
 * NFC Reader for ICAO 9303 e-Passport / e-ID chips.
 *
 * Reads the voter's government-issued ID via NFC and extracts:
 *  - Full legal name
 *  - Date of birth
 *  - Document number
 *  - Photo (for biometric matching)
 *  - Digital signature (proves the ID is genuine)
 *
 * SECURITY:
 *  - The chip's digital signature is verified against the issuing authority's
 *    public key (published by DHS/AAMVA for US IDs, ICAO PKD for passports).
 *  - A photocopy or printout of an ID cannot produce a valid NFC response.
 *  - The chip requires BAC (Basic Access Control) using the MRZ data —
 *    the reader must optically scan the MRZ first to derive the session key.
 *
 * PROTOCOL:
 *  1. Optical scan of MRZ (Machine Readable Zone) on the ID
 *  2. Derive BAC session key from MRZ data (doc number + DOB + expiry)
 *  3. NFC: SELECT eMRTD application (AID: A0000002471001)
 *  4. NFC: Mutual authentication using BAC session key
 *  5. NFC: Read DG1 (MRZ data), DG2 (facial image), SOD (security object)
 *  6. Verify SOD signature against CSCA certificate
 *  7. Verify data group hashes against SOD
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000~\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\n\n\u0002\b\u0002\n\u0002\u0010\u0012\n\u0002\b\u0007\n\u0002\u0010\f\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\r\b\u00c7\u0002\u0018\u00002\u00020\u0001:\u0005CDEFGB\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\t\u001a\u00020\u00072\u0006\u0010\n\u001a\u00020\u0007H\u0002J\u0010\u0010\u000b\u001a\u00020\u00072\u0006\u0010\n\u001a\u00020\u0007H\u0002J\u0010\u0010\f\u001a\u00020\u00072\u0006\u0010\r\u001a\u00020\u0007H\u0002J\u0010\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011H\u0002J\u0018\u0010\u0012\u001a\u00020\u00072\u0006\u0010\u0013\u001a\u00020\u00072\u0006\u0010\n\u001a\u00020\u0007H\u0002J\u0010\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u0017H\u0002J\u000e\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u001bJ\u000e\u0010\u001c\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u001bJ\u0018\u0010\u001d\u001a\u00020\u00072\u0006\u0010\u0013\u001a\u00020\u00072\u0006\u0010\n\u001a\u00020\u0007H\u0002J\u0010\u0010\u001e\u001a\u00020\u00112\u0006\u0010\u001f\u001a\u00020\u0007H\u0002J\u0018\u0010 \u001a\u00020!2\u0006\u0010\u0013\u001a\u00020\u00072\u0006\u0010\"\u001a\u00020\u0007H\u0002J\u0010\u0010#\u001a\u00020$2\u0006\u0010%\u001a\u00020\u0007H\u0002J\u0018\u0010&\u001a\u00020\u00072\u0006\u0010\'\u001a\u00020\u00072\u0006\u0010(\u001a\u00020!H\u0002J\u0018\u0010)\u001a\u00020\u00072\u0006\u0010\u0013\u001a\u00020\u00072\u0006\u0010\n\u001a\u00020\u0007H\u0002J\u0018\u0010*\u001a\u00020\u00072\u0006\u0010\u0013\u001a\u00020\u00072\u0006\u0010+\u001a\u00020!H\u0002J\u0018\u0010,\u001a\u00020-2\u0006\u0010\u001f\u001a\u00020\u00072\u0006\u0010.\u001a\u00020$H\u0002J\u0010\u0010/\u001a\u00020\u00072\u0006\u00100\u001a\u00020\u0007H\u0002J\u0010\u00101\u001a\u0002022\u0006\u0010\u0016\u001a\u00020\u0011H\u0002J\u001a\u00103\u001a\u0004\u0018\u0001042\u0006\u00105\u001a\u0002062\u0006\u00107\u001a\u00020\u0015H\u0002J\u001e\u00108\u001a\u0002092\u0006\u0010:\u001a\u00020;2\u0006\u0010<\u001a\u00020\u0017H\u0086@\u00a2\u0006\u0002\u0010=J\"\u0010>\u001a\u0004\u0018\u00010\u00072\u0006\u00105\u001a\u0002062\u0006\u0010?\u001a\u00020\u00042\u0006\u0010@\u001a\u000204H\u0002J \u0010A\u001a\u00020$2\u0006\u0010B\u001a\u00020\u00072\u0006\u0010\u001f\u001a\u00020\u00072\u0006\u00100\u001a\u00020\u0007H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006H"}, d2 = {"Lcom/securevote/remote/crypto/NFCReader;", "", "()V", "DG1_FID", "", "DG2_FID", "EMRTD_AID", "", "SOD_FID", "adjustDESKey", "key", "adjustParity", "buildSelectAPDU", "aid", "computeCheckDigit", "", "input", "", "decrypt3DES", "data", "deriveBACKey", "Lcom/securevote/remote/crypto/NFCReader$BACKey;", "mrz", "Lcom/securevote/remote/crypto/NFCReader$MRZData;", "disableNFCDispatch", "", "activity", "Landroid/app/Activity;", "enableNFCDispatch", "encrypt3DES", "extractMRZFromDG1", "dg1", "findByteSequence", "", "sequence", "isSuccess", "", "response", "kdf", "keySeed", "counter", "mac3DES", "padToBlockSize", "blockSize", "parseDG1", "Lcom/securevote/remote/data/models/VoterIdentity;", "signatureValid", "parseDG2FaceImage", "dg2", "parseMRZFields", "Lcom/securevote/remote/crypto/NFCReader$MRZFields;", "performBAC", "Lcom/securevote/remote/crypto/NFCReader$BACSession;", "isoDep", "Landroid/nfc/tech/IsoDep;", "bacKey", "readChip", "Lcom/securevote/remote/crypto/NFCReader$NFCReadResult;", "tag", "Landroid/nfc/Tag;", "mrzData", "(Landroid/nfc/Tag;Lcom/securevote/remote/crypto/NFCReader$MRZData;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "readDataGroup", "fid", "session", "verifySODSignature", "sod", "BACKey", "BACSession", "MRZData", "MRZFields", "NFCReadResult", "app_debug"})
public final class NFCReader {
    @org.jetbrains.annotations.NotNull()
    private static final byte[] EMRTD_AID = {(byte)-96, (byte)0, (byte)0, (byte)2, (byte)71, (byte)16, (byte)1};
    private static final short DG1_FID = (short)257;
    private static final short DG2_FID = (short)258;
    private static final short SOD_FID = (short)285;
    @org.jetbrains.annotations.NotNull()
    public static final com.securevote.remote.crypto.NFCReader INSTANCE = null;
    
    private NFCReader() {
        super();
    }
    
    /**
     * Enable NFC foreground dispatch on the activity.
     * Call in onResume() to capture NFC tags while the app is in the foreground.
     */
    public final void enableNFCDispatch(@org.jetbrains.annotations.NotNull()
    android.app.Activity activity) {
    }
    
    /**
     * Disable NFC foreground dispatch.
     * Call in onPause().
     */
    public final void disableNFCDispatch(@org.jetbrains.annotations.NotNull()
    android.app.Activity activity) {
    }
    
    /**
     * Read an ICAO 9303 chip from a discovered NFC tag.
     *
     * @param tag The NFC tag discovered by the system
     * @param mrzData The MRZ data extracted from optical scan (needed for BAC)
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object readChip(@org.jetbrains.annotations.NotNull()
    android.nfc.Tag tag, @org.jetbrains.annotations.NotNull()
    com.securevote.remote.crypto.NFCReader.MRZData mrzData, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.securevote.remote.crypto.NFCReader.NFCReadResult> $completion) {
        return null;
    }
    
    private final byte[] buildSelectAPDU(byte[] aid) {
        return null;
    }
    
    private final boolean isSuccess(byte[] response) {
        return false;
    }
    
    /**
     * Derive the BAC key from MRZ data.
     * K_seed = SHA-1(MRZ_info)[:16]
     * K_enc = KDF(K_seed, 1)
     * K_mac = KDF(K_seed, 2)
     */
    private final com.securevote.remote.crypto.NFCReader.BACKey deriveBACKey(com.securevote.remote.crypto.NFCReader.MRZData mrz) {
        return null;
    }
    
    /**
     * Perform BAC mutual authentication with the chip.
     * Returns session keys if successful, null if failed.
     */
    private final com.securevote.remote.crypto.NFCReader.BACSession performBAC(android.nfc.tech.IsoDep isoDep, com.securevote.remote.crypto.NFCReader.BACKey bacKey) {
        return null;
    }
    
    private final byte[] readDataGroup(android.nfc.tech.IsoDep isoDep, short fid, com.securevote.remote.crypto.NFCReader.BACSession session) {
        return null;
    }
    
    /**
     * Verify the Security Object Document (SOD) signature.
     * The SOD contains hashes of all data groups, signed by the issuing authority.
     *
     * In production:
     *  1. Parse the SOD as a CMS SignedData structure
     *  2. Extract the signing certificate
     *  3. Verify the certificate chain up to the Country Signing CA (CSCA)
     *  4. Verify the SOD signature
     *  5. Compute hashes of DG1 and DG2
     *  6. Compare against hashes in the SOD
     */
    private final boolean verifySODSignature(byte[] sod, byte[] dg1, byte[] dg2) {
        return false;
    }
    
    /**
     * Parse DG1 (MRZ data) into a VoterIdentity.
     * DG1 is a TLV-encoded structure containing the two or three MRZ lines.
     */
    private final com.securevote.remote.data.models.VoterIdentity parseDG1(byte[] dg1, boolean signatureValid) {
        return null;
    }
    
    /**
     * Extract the facial image (JPEG or JPEG2000) from DG2.
     */
    private final byte[] parseDG2FaceImage(byte[] dg2) {
        return null;
    }
    
    private final java.lang.String extractMRZFromDG1(byte[] dg1) {
        return null;
    }
    
    private final com.securevote.remote.crypto.NFCReader.MRZFields parseMRZFields(java.lang.String mrz) {
        return null;
    }
    
    private final char computeCheckDigit(java.lang.String input) {
        return '\u0000';
    }
    
    private final int findByteSequence(byte[] data, byte[] sequence) {
        return 0;
    }
    
    private final byte[] encrypt3DES(byte[] data, byte[] key) {
        return null;
    }
    
    private final byte[] decrypt3DES(byte[] data, byte[] key) {
        return null;
    }
    
    private final byte[] mac3DES(byte[] data, byte[] key) {
        return null;
    }
    
    private final byte[] kdf(byte[] keySeed, int counter) {
        return null;
    }
    
    private final byte[] adjustDESKey(byte[] key) {
        return null;
    }
    
    private final byte[] adjustParity(byte[] key) {
        return null;
    }
    
    private final byte[] padToBlockSize(byte[] data, int blockSize) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0012\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0082\b\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0005J\t\u0010\t\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\n\u001a\u00020\u0003H\u00c6\u0003J\u001d\u0010\u000b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\f\u001a\u00020\r2\b\u0010\u000e\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u000f\u001a\u00020\u0010H\u00d6\u0001J\t\u0010\u0011\u001a\u00020\u0012H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\u0007\u00a8\u0006\u0013"}, d2 = {"Lcom/securevote/remote/crypto/NFCReader$BACKey;", "", "encKey", "", "macKey", "([B[B)V", "getEncKey", "()[B", "getMacKey", "component1", "component2", "copy", "equals", "", "other", "hashCode", "", "toString", "", "app_debug"})
    static final class BACKey {
        @org.jetbrains.annotations.NotNull()
        private final byte[] encKey = null;
        @org.jetbrains.annotations.NotNull()
        private final byte[] macKey = null;
        
        @org.jetbrains.annotations.NotNull()
        public final byte[] component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final byte[] component2() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.securevote.remote.crypto.NFCReader.BACKey copy(@org.jetbrains.annotations.NotNull()
        byte[] encKey, @org.jetbrains.annotations.NotNull()
        byte[] macKey) {
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
        
        public BACKey(@org.jetbrains.annotations.NotNull()
        byte[] encKey, @org.jetbrains.annotations.NotNull()
        byte[] macKey) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final byte[] getEncKey() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final byte[] getMacKey() {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0012\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0082\b\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0006J\t\u0010\u000b\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\r\u001a\u00020\u0003H\u00c6\u0003J\'\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0012\u001a\u00020\u0013H\u00d6\u0001J\t\u0010\u0014\u001a\u00020\u0015H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\bR\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\b\u00a8\u0006\u0016"}, d2 = {"Lcom/securevote/remote/crypto/NFCReader$BACSession;", "", "encKey", "", "macKey", "ssc", "([B[B[B)V", "getEncKey", "()[B", "getMacKey", "getSsc", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "", "toString", "", "app_debug"})
    static final class BACSession {
        @org.jetbrains.annotations.NotNull()
        private final byte[] encKey = null;
        @org.jetbrains.annotations.NotNull()
        private final byte[] macKey = null;
        @org.jetbrains.annotations.NotNull()
        private final byte[] ssc = null;
        
        @org.jetbrains.annotations.NotNull()
        public final byte[] component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final byte[] component2() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final byte[] component3() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.securevote.remote.crypto.NFCReader.BACSession copy(@org.jetbrains.annotations.NotNull()
        byte[] encKey, @org.jetbrains.annotations.NotNull()
        byte[] macKey, @org.jetbrains.annotations.NotNull()
        byte[] ssc) {
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
        
        public BACSession(@org.jetbrains.annotations.NotNull()
        byte[] encKey, @org.jetbrains.annotations.NotNull()
        byte[] macKey, @org.jetbrains.annotations.NotNull()
        byte[] ssc) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final byte[] getEncKey() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final byte[] getMacKey() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final byte[] getSsc() {
            return null;
        }
    }
    
    /**
     * MRZ data extracted from optical scan of the ID.
     * Required for BAC key derivation.
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0006J\t\u0010\u000b\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\r\u001a\u00020\u0003H\u00c6\u0003J\'\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0012\u001a\u00020\u0013H\u00d6\u0001J\t\u0010\u0014\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\bR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\b\u00a8\u0006\u0015"}, d2 = {"Lcom/securevote/remote/crypto/NFCReader$MRZData;", "", "documentNumber", "", "dateOfBirth", "dateOfExpiry", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "getDateOfBirth", "()Ljava/lang/String;", "getDateOfExpiry", "getDocumentNumber", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "", "toString", "app_debug"})
    public static final class MRZData {
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String documentNumber = null;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String dateOfBirth = null;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String dateOfExpiry = null;
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component2() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component3() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.securevote.remote.crypto.NFCReader.MRZData copy(@org.jetbrains.annotations.NotNull()
        java.lang.String documentNumber, @org.jetbrains.annotations.NotNull()
        java.lang.String dateOfBirth, @org.jetbrains.annotations.NotNull()
        java.lang.String dateOfExpiry) {
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
        
        public MRZData(@org.jetbrains.annotations.NotNull()
        java.lang.String documentNumber, @org.jetbrains.annotations.NotNull()
        java.lang.String dateOfBirth, @org.jetbrains.annotations.NotNull()
        java.lang.String dateOfExpiry) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getDocumentNumber() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getDateOfBirth() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getDateOfExpiry() {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0018\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0082\b\u0018\u00002\u00020\u0001B=\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\u0006\u0010\u0007\u001a\u00020\u0003\u0012\u0006\u0010\b\u001a\u00020\u0003\u0012\u0006\u0010\t\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\nJ\t\u0010\u0013\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0014\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0015\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0016\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0017\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0018\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0019\u001a\u00020\u0003H\u00c6\u0003JO\u0010\u001a\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\u00032\b\b\u0002\u0010\t\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\u001b\u001a\u00020\u001c2\b\u0010\u001d\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001e\u001a\u00020\u001fH\u00d6\u0001J\t\u0010 \u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0007\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\fR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\fR\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\fR\u0011\u0010\b\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\fR\u0011\u0010\t\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\fR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\f\u00a8\u0006!"}, d2 = {"Lcom/securevote/remote/crypto/NFCReader$MRZFields;", "", "documentNumber", "", "surname", "givenNames", "dateOfBirth", "dateOfExpiry", "issuingState", "nationality", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "getDateOfBirth", "()Ljava/lang/String;", "getDateOfExpiry", "getDocumentNumber", "getGivenNames", "getIssuingState", "getNationality", "getSurname", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "copy", "equals", "", "other", "hashCode", "", "toString", "app_debug"})
    static final class MRZFields {
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String documentNumber = null;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String surname = null;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String givenNames = null;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String dateOfBirth = null;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String dateOfExpiry = null;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String issuingState = null;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String nationality = null;
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component2() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component3() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component4() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component5() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component6() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component7() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.securevote.remote.crypto.NFCReader.MRZFields copy(@org.jetbrains.annotations.NotNull()
        java.lang.String documentNumber, @org.jetbrains.annotations.NotNull()
        java.lang.String surname, @org.jetbrains.annotations.NotNull()
        java.lang.String givenNames, @org.jetbrains.annotations.NotNull()
        java.lang.String dateOfBirth, @org.jetbrains.annotations.NotNull()
        java.lang.String dateOfExpiry, @org.jetbrains.annotations.NotNull()
        java.lang.String issuingState, @org.jetbrains.annotations.NotNull()
        java.lang.String nationality) {
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
        
        public MRZFields(@org.jetbrains.annotations.NotNull()
        java.lang.String documentNumber, @org.jetbrains.annotations.NotNull()
        java.lang.String surname, @org.jetbrains.annotations.NotNull()
        java.lang.String givenNames, @org.jetbrains.annotations.NotNull()
        java.lang.String dateOfBirth, @org.jetbrains.annotations.NotNull()
        java.lang.String dateOfExpiry, @org.jetbrains.annotations.NotNull()
        java.lang.String issuingState, @org.jetbrains.annotations.NotNull()
        java.lang.String nationality) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getDocumentNumber() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getSurname() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getGivenNames() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getDateOfBirth() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getDateOfExpiry() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getIssuingState() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getNationality() {
            return null;
        }
    }
    
    /**
     * Result of an NFC chip read attempt.
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b7\u0018\u00002\u00020\u0001:\u0005\u0003\u0004\u0005\u0006\u0007B\u0007\b\u0004\u00a2\u0006\u0002\u0010\u0002\u0082\u0001\u0005\b\t\n\u000b\f\u00a8\u0006\r"}, d2 = {"Lcom/securevote/remote/crypto/NFCReader$NFCReadResult;", "", "()V", "AuthenticationFailed", "ChipNotFound", "Error", "SignatureInvalid", "Success", "Lcom/securevote/remote/crypto/NFCReader$NFCReadResult$AuthenticationFailed;", "Lcom/securevote/remote/crypto/NFCReader$NFCReadResult$ChipNotFound;", "Lcom/securevote/remote/crypto/NFCReader$NFCReadResult$Error;", "Lcom/securevote/remote/crypto/NFCReader$NFCReadResult$SignatureInvalid;", "Lcom/securevote/remote/crypto/NFCReader$NFCReadResult$Success;", "app_debug"})
    public static abstract class NFCReadResult {
        
        private NFCReadResult() {
            super();
        }
        
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\t\u0010\u0007\u001a\u00020\u0003H\u00c6\u0003J\u0013\u0010\b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u00d6\u0003J\t\u0010\r\u001a\u00020\u000eH\u00d6\u0001J\t\u0010\u000f\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0010"}, d2 = {"Lcom/securevote/remote/crypto/NFCReader$NFCReadResult$AuthenticationFailed;", "Lcom/securevote/remote/crypto/NFCReader$NFCReadResult;", "message", "", "(Ljava/lang/String;)V", "getMessage", "()Ljava/lang/String;", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "app_debug"})
        public static final class AuthenticationFailed extends com.securevote.remote.crypto.NFCReader.NFCReadResult {
            @org.jetbrains.annotations.NotNull()
            private final java.lang.String message = null;
            
            @org.jetbrains.annotations.NotNull()
            public final java.lang.String component1() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final com.securevote.remote.crypto.NFCReader.NFCReadResult.AuthenticationFailed copy(@org.jetbrains.annotations.NotNull()
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
            
            public AuthenticationFailed(@org.jetbrains.annotations.NotNull()
            java.lang.String message) {
            }
            
            @org.jetbrains.annotations.NotNull()
            public final java.lang.String getMessage() {
                return null;
            }
        }
        
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\t\u0010\u0007\u001a\u00020\u0003H\u00c6\u0003J\u0013\u0010\b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u00d6\u0003J\t\u0010\r\u001a\u00020\u000eH\u00d6\u0001J\t\u0010\u000f\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0010"}, d2 = {"Lcom/securevote/remote/crypto/NFCReader$NFCReadResult$ChipNotFound;", "Lcom/securevote/remote/crypto/NFCReader$NFCReadResult;", "message", "", "(Ljava/lang/String;)V", "getMessage", "()Ljava/lang/String;", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "app_debug"})
        public static final class ChipNotFound extends com.securevote.remote.crypto.NFCReader.NFCReadResult {
            @org.jetbrains.annotations.NotNull()
            private final java.lang.String message = null;
            
            @org.jetbrains.annotations.NotNull()
            public final java.lang.String component1() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final com.securevote.remote.crypto.NFCReader.NFCReadResult.ChipNotFound copy(@org.jetbrains.annotations.NotNull()
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
            
            public ChipNotFound(@org.jetbrains.annotations.NotNull()
            java.lang.String message) {
            }
            
            @org.jetbrains.annotations.NotNull()
            public final java.lang.String getMessage() {
                return null;
            }
        }
        
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0003\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\t\u0010\u0007\u001a\u00020\u0003H\u00c6\u0003J\u0013\u0010\b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u00d6\u0003J\t\u0010\r\u001a\u00020\u000eH\u00d6\u0001J\t\u0010\u000f\u001a\u00020\u0010H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0011"}, d2 = {"Lcom/securevote/remote/crypto/NFCReader$NFCReadResult$Error;", "Lcom/securevote/remote/crypto/NFCReader$NFCReadResult;", "exception", "", "(Ljava/lang/Throwable;)V", "getException", "()Ljava/lang/Throwable;", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "", "app_debug"})
        public static final class Error extends com.securevote.remote.crypto.NFCReader.NFCReadResult {
            @org.jetbrains.annotations.NotNull()
            private final java.lang.Throwable exception = null;
            
            @org.jetbrains.annotations.NotNull()
            public final java.lang.Throwable component1() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final com.securevote.remote.crypto.NFCReader.NFCReadResult.Error copy(@org.jetbrains.annotations.NotNull()
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
        
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\t\u0010\u0007\u001a\u00020\u0003H\u00c6\u0003J\u0013\u0010\b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u00d6\u0003J\t\u0010\r\u001a\u00020\u000eH\u00d6\u0001J\t\u0010\u000f\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0010"}, d2 = {"Lcom/securevote/remote/crypto/NFCReader$NFCReadResult$SignatureInvalid;", "Lcom/securevote/remote/crypto/NFCReader$NFCReadResult;", "message", "", "(Ljava/lang/String;)V", "getMessage", "()Ljava/lang/String;", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "app_debug"})
        public static final class SignatureInvalid extends com.securevote.remote.crypto.NFCReader.NFCReadResult {
            @org.jetbrains.annotations.NotNull()
            private final java.lang.String message = null;
            
            @org.jetbrains.annotations.NotNull()
            public final java.lang.String component1() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final com.securevote.remote.crypto.NFCReader.NFCReadResult.SignatureInvalid copy(@org.jetbrains.annotations.NotNull()
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
            
            public SignatureInvalid(@org.jetbrains.annotations.NotNull()
            java.lang.String message) {
            }
            
            @org.jetbrains.annotations.NotNull()
            public final java.lang.String getMessage() {
                return null;
            }
        }
        
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0012\n\u0002\b\f\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0007J\t\u0010\r\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u000e\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u000f\u001a\u00020\u0005H\u00c6\u0003J\'\u0010\u0010\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010\u0011\u001a\u00020\u00122\b\u0010\u0013\u001a\u0004\u0018\u00010\u0014H\u00d6\u0003J\t\u0010\u0015\u001a\u00020\u0016H\u00d6\u0001J\t\u0010\u0017\u001a\u00020\u0018H\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\t\u00a8\u0006\u0019"}, d2 = {"Lcom/securevote/remote/crypto/NFCReader$NFCReadResult$Success;", "Lcom/securevote/remote/crypto/NFCReader$NFCReadResult;", "identity", "Lcom/securevote/remote/data/models/VoterIdentity;", "facePhoto", "", "rawChipData", "(Lcom/securevote/remote/data/models/VoterIdentity;[B[B)V", "getFacePhoto", "()[B", "getIdentity", "()Lcom/securevote/remote/data/models/VoterIdentity;", "getRawChipData", "component1", "component2", "component3", "copy", "equals", "", "other", "", "hashCode", "", "toString", "", "app_debug"})
        public static final class Success extends com.securevote.remote.crypto.NFCReader.NFCReadResult {
            @org.jetbrains.annotations.NotNull()
            private final com.securevote.remote.data.models.VoterIdentity identity = null;
            @org.jetbrains.annotations.NotNull()
            private final byte[] facePhoto = null;
            @org.jetbrains.annotations.NotNull()
            private final byte[] rawChipData = null;
            
            @org.jetbrains.annotations.NotNull()
            public final com.securevote.remote.data.models.VoterIdentity component1() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final byte[] component2() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final byte[] component3() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final com.securevote.remote.crypto.NFCReader.NFCReadResult.Success copy(@org.jetbrains.annotations.NotNull()
            com.securevote.remote.data.models.VoterIdentity identity, @org.jetbrains.annotations.NotNull()
            byte[] facePhoto, @org.jetbrains.annotations.NotNull()
            byte[] rawChipData) {
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
            com.securevote.remote.data.models.VoterIdentity identity, @org.jetbrains.annotations.NotNull()
            byte[] facePhoto, @org.jetbrains.annotations.NotNull()
            byte[] rawChipData) {
            }
            
            @org.jetbrains.annotations.NotNull()
            public final com.securevote.remote.data.models.VoterIdentity getIdentity() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final byte[] getFacePhoto() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final byte[] getRawChipData() {
                return null;
            }
        }
    }
}