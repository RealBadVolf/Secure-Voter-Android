package com.securevote.remote.crypto;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import com.securevote.remote.data.models.BallotSelections;
import org.bouncycastle.pqc.jcajce.provider.BouncyCastlePQCProvider;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * SVR Cryptographic Operations.
 *
 * Implements the hybrid post-quantum encryption protocol from ABSENTEE.md:
 *  Inner layer: AES-256-GCM + ECIES P-384 (classical)
 *  Outer layer: AES-256-GCM + ML-KEM-1024 (post-quantum, FIPS 203)
 *
 * All private keys live in Android Keystore (StrongBox when available).
 * No secret material is ever stored in app sandbox storage.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000N\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0012\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\u0011\n\u0002\b\n\b\u00c7\u0002\u0018\u00002\u00020\u0001:\u0004789:B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u001e\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\r2\u0006\u0010\u000f\u001a\u00020\rJ\u0016\u0010\u0010\u001a\u00020\r2\u0006\u0010\u0011\u001a\u00020\r2\u0006\u0010\u0012\u001a\u00020\u0013J\u0018\u0010\u0014\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\r2\u0006\u0010\u0015\u001a\u00020\u0013H\u0002J6\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u00132\u0006\u0010\u001b\u001a\u00020\r2\u0006\u0010\u001c\u001a\u00020\u00062\u0006\u0010\u001d\u001a\u00020\u00062\u0006\u0010\u001e\u001a\u00020\u0006J\u0006\u0010\u001f\u001a\u00020\rJ\u0006\u0010 \u001a\u00020\u0006J\u0006\u0010!\u001a\u00020\u0006J\u0006\u0010\"\u001a\u00020\u0006J\u0006\u0010#\u001a\u00020\rJ\u0006\u0010$\u001a\u00020\u0006J\u0006\u0010%\u001a\u00020&J\u001a\u0010\'\u001a\u00020\u00062\u0006\u0010(\u001a\u00020\u00062\n\b\u0002\u0010)\u001a\u0004\u0018\u00010\rJ\u0016\u0010*\u001a\u00020\u00062\u0006\u0010+\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\rJ\u0018\u0010,\u001a\u00020\r2\u0006\u0010-\u001a\u00020\r2\u0006\u0010\u001b\u001a\u00020\rH\u0002J\u000e\u0010.\u001a\u00020\r2\u0006\u0010+\u001a\u00020\rJ\u001f\u0010/\u001a\u00020\u00062\u0012\u00100\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u000601\"\u00020\u0006\u00a2\u0006\u0002\u00102J\u000e\u0010/\u001a\u00020\u00062\u0006\u0010+\u001a\u00020\rJ\u000e\u00103\u001a\u00020\r2\u0006\u0010+\u001a\u00020\rJ\u001e\u00104\u001a\u00020\r2\u0006\u00105\u001a\u00020\r2\u0006\u00106\u001a\u00020\r2\u0006\u0010\u0012\u001a\u00020\u0013R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0006X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0006X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006;"}, d2 = {"Lcom/securevote/remote/crypto/SVRCrypto;", "", "()V", "AES_GCM_TAG_LENGTH", "", "DEVICE_KEY_ALIAS", "", "HMAC_ALGO", "KEYSTORE_PROVIDER", "NONCE_LENGTH", "aesGcmEncrypt", "Lcom/securevote/remote/crypto/SVRCrypto$AESEncryptionResult;", "plaintext", "", "key", "aad", "blindToken", "token", "serverPublicKey", "Ljava/security/PublicKey;", "eciesWrapKey", "publicKey", "encryptBallot", "Lcom/securevote/remote/crypto/SVRCrypto$EncryptedBallot;", "selections", "Lcom/securevote/remote/data/models/BallotSelections;", "classicalPublicKey", "pqPublicKey", "voteRecordId", "voterToken", "nonce", "generateAESKey", "generateNonce", "generateUUID", "generateVoteRecordId", "generateVoterToken", "getDevicePublicKeyHash", "getOrCreateDeviceKeyPair", "Ljava/security/KeyPair;", "hashPinArgon2id", "pin", "salt", "hmacSha256", "data", "mlKemEncapsulate", "keyToWrap", "sha256", "sha256Hex", "parts", "", "([Ljava/lang/String;)Ljava/lang/String;", "signWithDeviceKey", "unblindSignature", "blindSignature", "blindingFactor", "AESEncryptionResult", "ClassicalPackage", "EncryptedBallot", "PQPackage", "app_debug"})
public final class SVRCrypto {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEYSTORE_PROVIDER = "AndroidKeyStore";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String DEVICE_KEY_ALIAS = "svr_device_key";
    private static final int AES_GCM_TAG_LENGTH = 128;
    private static final int NONCE_LENGTH = 32;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String HMAC_ALGO = "HmacSHA256";
    @org.jetbrains.annotations.NotNull()
    public static final com.securevote.remote.crypto.SVRCrypto INSTANCE = null;
    
    private SVRCrypto() {
        super();
    }
    
    /**
     * Generate or retrieve the device's hardware-backed signing key pair.
     * This key is created in the Secure Enclave / StrongBox and NEVER leaves hardware.
     * Used for: request signing, device attestation binding, QR HMAC.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.security.KeyPair getOrCreateDeviceKeyPair() {
        return null;
    }
    
    /**
     * Get the SHA-256 hash of the device's public key.
     * This is the device identity used in registration and QR codes.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getDevicePublicKeyHash() {
        return null;
    }
    
    /**
     * Sign data with the device's hardware-backed private key.
     */
    @org.jetbrains.annotations.NotNull()
    public final byte[] signWithDeviceKey(@org.jetbrains.annotations.NotNull()
    byte[] data) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final byte[] sha256(@org.jetbrains.annotations.NotNull()
    byte[] data) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String sha256Hex(@org.jetbrains.annotations.NotNull()
    byte[] data) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String sha256Hex(@org.jetbrains.annotations.NotNull()
    java.lang.String... parts) {
        return null;
    }
    
    /**
     * Compute HMAC-SHA256 using the device's private key material.
     * Used to prove device possession in the rotating IPVC QR code.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String hmacSha256(@org.jetbrains.annotations.NotNull()
    byte[] data, @org.jetbrains.annotations.NotNull()
    byte[] key) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String generateNonce() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String generateUUID() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String generateVoteRecordId() {
        return null;
    }
    
    /**
     * Encrypt with AES-256-GCM.
     * @param plaintext Data to encrypt
     * @param aad Additional Authenticated Data (VoteRecordID || VoterToken || Timestamp)
     * @return Encrypted result with IV
     */
    @org.jetbrains.annotations.NotNull()
    public final com.securevote.remote.crypto.SVRCrypto.AESEncryptionResult aesGcmEncrypt(@org.jetbrains.annotations.NotNull()
    byte[] plaintext, @org.jetbrains.annotations.NotNull()
    byte[] key, @org.jetbrains.annotations.NotNull()
    byte[] aad) {
        return null;
    }
    
    /**
     * Generate a random AES-256 key.
     */
    @org.jetbrains.annotations.NotNull()
    public final byte[] generateAESKey() {
        return null;
    }
    
    /**
     * Encrypt ballot selections using the hybrid PQ scheme from ABSENTEE.md:
     *
     *  1. Serialize selections to JSON
     *  2. Classical layer: AES-256-GCM, key wrapped with ECIES P-384
     *  3. PQ layer: AES-256-GCM, key encapsulated with ML-KEM-1024
     *  4. Compute confirmation hash
     *
     * @param selections The voter's ballot selections
     * @param classicalPublicKey Election's ECIES P-384 public key
     * @param pqPublicKey Election's ML-KEM-1024 encapsulation key
     * @param voteRecordId For AAD binding
     * @param voterToken For AAD binding
     * @param nonce Unique nonce for this vote
     */
    @org.jetbrains.annotations.NotNull()
    public final com.securevote.remote.crypto.SVRCrypto.EncryptedBallot encryptBallot(@org.jetbrains.annotations.NotNull()
    com.securevote.remote.data.models.BallotSelections selections, @org.jetbrains.annotations.NotNull()
    java.security.PublicKey classicalPublicKey, @org.jetbrains.annotations.NotNull()
    byte[] pqPublicKey, @org.jetbrains.annotations.NotNull()
    java.lang.String voteRecordId, @org.jetbrains.annotations.NotNull()
    java.lang.String voterToken, @org.jetbrains.annotations.NotNull()
    java.lang.String nonce) {
        return null;
    }
    
    /**
     * ECIES key wrapping using P-384.
     * In production, this uses proper ECIES-KEM from Bouncy Castle.
     */
    private final byte[] eciesWrapKey(byte[] key, java.security.PublicKey publicKey) {
        return null;
    }
    
    /**
     * ML-KEM-1024 key encapsulation.
     * Uses Bouncy Castle's PQC provider (FIPS 203 implementation).
     */
    private final byte[] mlKemEncapsulate(byte[] keyToWrap, byte[] pqPublicKey) {
        return null;
    }
    
    /**
     * Generate a random voter token.
     */
    @org.jetbrains.annotations.NotNull()
    public final byte[] generateVoterToken() {
        return null;
    }
    
    /**
     * Blind a voter token for RSA blind signature.
     * The blinded token is sent to sv-remote-auth, which signs it
     * without seeing the actual token value.
     */
    @org.jetbrains.annotations.NotNull()
    public final byte[] blindToken(@org.jetbrains.annotations.NotNull()
    byte[] token, @org.jetbrains.annotations.NotNull()
    java.security.PublicKey serverPublicKey) {
        return null;
    }
    
    /**
     * Unblind a signed token.
     * Returns the final (token, signature) pair that proves
     * "a registered voter cast this ballot" without revealing which voter.
     */
    @org.jetbrains.annotations.NotNull()
    public final byte[] unblindSignature(@org.jetbrains.annotations.NotNull()
    byte[] blindSignature, @org.jetbrains.annotations.NotNull()
    byte[] blindingFactor, @org.jetbrains.annotations.NotNull()
    java.security.PublicKey serverPublicKey) {
        return null;
    }
    
    /**
     * Hash a PIN with Argon2id.
     * Parameters tuned for mobile devices: moderate memory, reasonable time.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String hashPinArgon2id(@org.jetbrains.annotations.NotNull()
    java.lang.String pin, @org.jetbrains.annotations.Nullable()
    byte[] salt) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0012\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0006J\t\u0010\u000b\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\r\u001a\u00020\u0003H\u00c6\u0003J\'\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0012\u001a\u00020\u0013H\u00d6\u0001J\t\u0010\u0014\u001a\u00020\u0015H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\bR\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\b\u00a8\u0006\u0016"}, d2 = {"Lcom/securevote/remote/crypto/SVRCrypto$AESEncryptionResult;", "", "ciphertext", "", "iv", "tag", "([B[B[B)V", "getCiphertext", "()[B", "getIv", "getTag", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "", "toString", "", "app_debug"})
    public static final class AESEncryptionResult {
        @org.jetbrains.annotations.NotNull()
        private final byte[] ciphertext = null;
        @org.jetbrains.annotations.NotNull()
        private final byte[] iv = null;
        @org.jetbrains.annotations.NotNull()
        private final byte[] tag = null;
        
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
        public final com.securevote.remote.crypto.SVRCrypto.AESEncryptionResult copy(@org.jetbrains.annotations.NotNull()
        byte[] ciphertext, @org.jetbrains.annotations.NotNull()
        byte[] iv, @org.jetbrains.annotations.NotNull()
        byte[] tag) {
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
        
        public AESEncryptionResult(@org.jetbrains.annotations.NotNull()
        byte[] ciphertext, @org.jetbrains.annotations.NotNull()
        byte[] iv, @org.jetbrains.annotations.NotNull()
        byte[] tag) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final byte[] getCiphertext() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final byte[] getIv() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final byte[] getTag() {
            return null;
        }
    }
    
    @kotlinx.serialization.Serializable()
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0012\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0087\b\u0018\u0000 #2\u00020\u0001:\u0002\"#B7\b\u0011\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0005\u0012\b\u0010\u0007\u001a\u0004\u0018\u00010\u0005\u0012\b\u0010\b\u001a\u0004\u0018\u00010\t\u00a2\u0006\u0002\u0010\nB\u001d\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u000bJ\t\u0010\u0010\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0011\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0012\u001a\u00020\u0005H\u00c6\u0003J\'\u0010\u0013\u001a\u00020\u00002\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010\u0014\u001a\u00020\u00152\b\u0010\u0016\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0017\u001a\u00020\u0003H\u00d6\u0001J\t\u0010\u0018\u001a\u00020\u0019H\u00d6\u0001J&\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u00002\u0006\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020 H\u00c1\u0001\u00a2\u0006\u0002\b!R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\rR\u0011\u0010\u0007\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\r\u00a8\u0006$"}, d2 = {"Lcom/securevote/remote/crypto/SVRCrypto$ClassicalPackage;", "", "seen1", "", "ciphertext", "", "iv", "wrappedKey", "serializationConstructorMarker", "Lkotlinx/serialization/internal/SerializationConstructorMarker;", "(I[B[B[BLkotlinx/serialization/internal/SerializationConstructorMarker;)V", "([B[B[B)V", "getCiphertext", "()[B", "getIv", "getWrappedKey", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "toString", "", "write$Self", "", "self", "output", "Lkotlinx/serialization/encoding/CompositeEncoder;", "serialDesc", "Lkotlinx/serialization/descriptors/SerialDescriptor;", "write$Self$app_debug", "$serializer", "Companion", "app_debug"})
    public static final class ClassicalPackage {
        @org.jetbrains.annotations.NotNull()
        private final byte[] ciphertext = null;
        @org.jetbrains.annotations.NotNull()
        private final byte[] iv = null;
        @org.jetbrains.annotations.NotNull()
        private final byte[] wrappedKey = null;
        @org.jetbrains.annotations.NotNull()
        public static final com.securevote.remote.crypto.SVRCrypto.ClassicalPackage.Companion Companion = null;
        
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
        public final com.securevote.remote.crypto.SVRCrypto.ClassicalPackage copy(@org.jetbrains.annotations.NotNull()
        byte[] ciphertext, @org.jetbrains.annotations.NotNull()
        byte[] iv, @org.jetbrains.annotations.NotNull()
        byte[] wrappedKey) {
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
        
        @kotlin.jvm.JvmStatic()
        public static final void write$Self$app_debug(@org.jetbrains.annotations.NotNull()
        com.securevote.remote.crypto.SVRCrypto.ClassicalPackage self, @org.jetbrains.annotations.NotNull()
        kotlinx.serialization.encoding.CompositeEncoder output, @org.jetbrains.annotations.NotNull()
        kotlinx.serialization.descriptors.SerialDescriptor serialDesc) {
        }
        
        public ClassicalPackage(@org.jetbrains.annotations.NotNull()
        byte[] ciphertext, @org.jetbrains.annotations.NotNull()
        byte[] iv, @org.jetbrains.annotations.NotNull()
        byte[] wrappedKey) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final byte[] getCiphertext() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final byte[] getIv() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final byte[] getWrappedKey() {
            return null;
        }
        
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c7\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0003J\u0018\u0010\b\u001a\f\u0012\b\u0012\u0006\u0012\u0002\b\u00030\n0\tH\u00d6\u0001\u00a2\u0006\u0002\u0010\u000bJ\u0011\u0010\f\u001a\u00020\u00022\u0006\u0010\r\u001a\u00020\u000eH\u00d6\u0001J\u0019\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0002H\u00d6\u0001R\u0014\u0010\u0004\u001a\u00020\u00058VX\u00d6\u0005\u00a2\u0006\u0006\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\u0014"}, d2 = {"com/securevote/remote/crypto/SVRCrypto.ClassicalPackage.$serializer", "Lkotlinx/serialization/internal/GeneratedSerializer;", "Lcom/securevote/remote/crypto/SVRCrypto$ClassicalPackage;", "()V", "descriptor", "Lkotlinx/serialization/descriptors/SerialDescriptor;", "getDescriptor", "()Lkotlinx/serialization/descriptors/SerialDescriptor;", "childSerializers", "", "Lkotlinx/serialization/KSerializer;", "()[Lkotlinx/serialization/KSerializer;", "deserialize", "decoder", "Lkotlinx/serialization/encoding/Decoder;", "serialize", "", "encoder", "Lkotlinx/serialization/encoding/Encoder;", "value", "app_debug"})
        @java.lang.Deprecated()
        public static final class $serializer implements kotlinx.serialization.internal.GeneratedSerializer<com.securevote.remote.crypto.SVRCrypto.ClassicalPackage> {
            @org.jetbrains.annotations.NotNull()
            public static final com.securevote.remote.crypto.SVRCrypto.ClassicalPackage.$serializer INSTANCE = null;
            
            @java.lang.Override()
            @org.jetbrains.annotations.NotNull()
            public kotlinx.serialization.KSerializer<?>[] childSerializers() {
                return null;
            }
            
            @java.lang.Override()
            @org.jetbrains.annotations.NotNull()
            public com.securevote.remote.crypto.SVRCrypto.ClassicalPackage deserialize(@org.jetbrains.annotations.NotNull()
            kotlinx.serialization.encoding.Decoder decoder) {
                return null;
            }
            
            @java.lang.Override()
            @org.jetbrains.annotations.NotNull()
            public kotlinx.serialization.descriptors.SerialDescriptor getDescriptor() {
                return null;
            }
            
            @java.lang.Override()
            public void serialize(@org.jetbrains.annotations.NotNull()
            kotlinx.serialization.encoding.Encoder encoder, @org.jetbrains.annotations.NotNull()
            com.securevote.remote.crypto.SVRCrypto.ClassicalPackage value) {
            }
            
            private $serializer() {
                super();
            }
            
            @java.lang.Override()
            @org.jetbrains.annotations.NotNull()
            public kotlinx.serialization.KSerializer<?>[] typeParametersSerializers() {
                return null;
            }
        }
        
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000f\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004H\u00c6\u0001\u00a8\u0006\u0006"}, d2 = {"Lcom/securevote/remote/crypto/SVRCrypto$ClassicalPackage$Companion;", "", "()V", "serializer", "Lkotlinx/serialization/KSerializer;", "Lcom/securevote/remote/crypto/SVRCrypto$ClassicalPackage;", "app_debug"})
        public static final class Companion {
            
            @org.jetbrains.annotations.NotNull()
            public final kotlinx.serialization.KSerializer<com.securevote.remote.crypto.SVRCrypto.ClassicalPackage> serializer() {
                return null;
            }
            
            private Companion() {
                super();
            }
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0012\n\u0000\n\u0002\u0010\u000e\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\t\u0010\u000b\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\f\u001a\u00020\u0005H\u00c6\u0003J\u001d\u0010\r\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0011\u001a\u00020\u0012H\u00d6\u0001J\t\u0010\u0013\u001a\u00020\u0005H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n\u00a8\u0006\u0014"}, d2 = {"Lcom/securevote/remote/crypto/SVRCrypto$EncryptedBallot;", "", "ciphertext", "", "confirmationHash", "", "([BLjava/lang/String;)V", "getCiphertext", "()[B", "getConfirmationHash", "()Ljava/lang/String;", "component1", "component2", "copy", "equals", "", "other", "hashCode", "", "toString", "app_debug"})
    public static final class EncryptedBallot {
        @org.jetbrains.annotations.NotNull()
        private final byte[] ciphertext = null;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String confirmationHash = null;
        
        @org.jetbrains.annotations.NotNull()
        public final byte[] component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component2() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.securevote.remote.crypto.SVRCrypto.EncryptedBallot copy(@org.jetbrains.annotations.NotNull()
        byte[] ciphertext, @org.jetbrains.annotations.NotNull()
        java.lang.String confirmationHash) {
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
        
        public EncryptedBallot(@org.jetbrains.annotations.NotNull()
        byte[] ciphertext, @org.jetbrains.annotations.NotNull()
        java.lang.String confirmationHash) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final byte[] getCiphertext() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getConfirmationHash() {
            return null;
        }
    }
    
    @kotlinx.serialization.Serializable()
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0012\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0087\b\u0018\u0000 #2\u00020\u0001:\u0002\"#B7\b\u0011\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0005\u0012\b\u0010\u0007\u001a\u0004\u0018\u00010\u0005\u0012\b\u0010\b\u001a\u0004\u0018\u00010\t\u00a2\u0006\u0002\u0010\nB\u001d\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u000bJ\t\u0010\u0010\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0011\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0012\u001a\u00020\u0005H\u00c6\u0003J\'\u0010\u0013\u001a\u00020\u00002\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010\u0014\u001a\u00020\u00152\b\u0010\u0016\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0017\u001a\u00020\u0003H\u00d6\u0001J\t\u0010\u0018\u001a\u00020\u0019H\u00d6\u0001J&\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u00002\u0006\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020 H\u00c1\u0001\u00a2\u0006\u0002\b!R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\rR\u0011\u0010\u0007\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\r\u00a8\u0006$"}, d2 = {"Lcom/securevote/remote/crypto/SVRCrypto$PQPackage;", "", "seen1", "", "pqCiphertext", "", "pqIv", "pqKemCiphertext", "serializationConstructorMarker", "Lkotlinx/serialization/internal/SerializationConstructorMarker;", "(I[B[B[BLkotlinx/serialization/internal/SerializationConstructorMarker;)V", "([B[B[B)V", "getPqCiphertext", "()[B", "getPqIv", "getPqKemCiphertext", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "toString", "", "write$Self", "", "self", "output", "Lkotlinx/serialization/encoding/CompositeEncoder;", "serialDesc", "Lkotlinx/serialization/descriptors/SerialDescriptor;", "write$Self$app_debug", "$serializer", "Companion", "app_debug"})
    public static final class PQPackage {
        @org.jetbrains.annotations.NotNull()
        private final byte[] pqCiphertext = null;
        @org.jetbrains.annotations.NotNull()
        private final byte[] pqIv = null;
        @org.jetbrains.annotations.NotNull()
        private final byte[] pqKemCiphertext = null;
        @org.jetbrains.annotations.NotNull()
        public static final com.securevote.remote.crypto.SVRCrypto.PQPackage.Companion Companion = null;
        
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
        public final com.securevote.remote.crypto.SVRCrypto.PQPackage copy(@org.jetbrains.annotations.NotNull()
        byte[] pqCiphertext, @org.jetbrains.annotations.NotNull()
        byte[] pqIv, @org.jetbrains.annotations.NotNull()
        byte[] pqKemCiphertext) {
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
        
        @kotlin.jvm.JvmStatic()
        public static final void write$Self$app_debug(@org.jetbrains.annotations.NotNull()
        com.securevote.remote.crypto.SVRCrypto.PQPackage self, @org.jetbrains.annotations.NotNull()
        kotlinx.serialization.encoding.CompositeEncoder output, @org.jetbrains.annotations.NotNull()
        kotlinx.serialization.descriptors.SerialDescriptor serialDesc) {
        }
        
        public PQPackage(@org.jetbrains.annotations.NotNull()
        byte[] pqCiphertext, @org.jetbrains.annotations.NotNull()
        byte[] pqIv, @org.jetbrains.annotations.NotNull()
        byte[] pqKemCiphertext) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final byte[] getPqCiphertext() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final byte[] getPqIv() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final byte[] getPqKemCiphertext() {
            return null;
        }
        
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c7\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0003J\u0018\u0010\b\u001a\f\u0012\b\u0012\u0006\u0012\u0002\b\u00030\n0\tH\u00d6\u0001\u00a2\u0006\u0002\u0010\u000bJ\u0011\u0010\f\u001a\u00020\u00022\u0006\u0010\r\u001a\u00020\u000eH\u00d6\u0001J\u0019\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0002H\u00d6\u0001R\u0014\u0010\u0004\u001a\u00020\u00058VX\u00d6\u0005\u00a2\u0006\u0006\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\u0014"}, d2 = {"com/securevote/remote/crypto/SVRCrypto.PQPackage.$serializer", "Lkotlinx/serialization/internal/GeneratedSerializer;", "Lcom/securevote/remote/crypto/SVRCrypto$PQPackage;", "()V", "descriptor", "Lkotlinx/serialization/descriptors/SerialDescriptor;", "getDescriptor", "()Lkotlinx/serialization/descriptors/SerialDescriptor;", "childSerializers", "", "Lkotlinx/serialization/KSerializer;", "()[Lkotlinx/serialization/KSerializer;", "deserialize", "decoder", "Lkotlinx/serialization/encoding/Decoder;", "serialize", "", "encoder", "Lkotlinx/serialization/encoding/Encoder;", "value", "app_debug"})
        @java.lang.Deprecated()
        public static final class $serializer implements kotlinx.serialization.internal.GeneratedSerializer<com.securevote.remote.crypto.SVRCrypto.PQPackage> {
            @org.jetbrains.annotations.NotNull()
            public static final com.securevote.remote.crypto.SVRCrypto.PQPackage.$serializer INSTANCE = null;
            
            @java.lang.Override()
            @org.jetbrains.annotations.NotNull()
            public kotlinx.serialization.KSerializer<?>[] childSerializers() {
                return null;
            }
            
            @java.lang.Override()
            @org.jetbrains.annotations.NotNull()
            public com.securevote.remote.crypto.SVRCrypto.PQPackage deserialize(@org.jetbrains.annotations.NotNull()
            kotlinx.serialization.encoding.Decoder decoder) {
                return null;
            }
            
            @java.lang.Override()
            @org.jetbrains.annotations.NotNull()
            public kotlinx.serialization.descriptors.SerialDescriptor getDescriptor() {
                return null;
            }
            
            @java.lang.Override()
            public void serialize(@org.jetbrains.annotations.NotNull()
            kotlinx.serialization.encoding.Encoder encoder, @org.jetbrains.annotations.NotNull()
            com.securevote.remote.crypto.SVRCrypto.PQPackage value) {
            }
            
            private $serializer() {
                super();
            }
            
            @java.lang.Override()
            @org.jetbrains.annotations.NotNull()
            public kotlinx.serialization.KSerializer<?>[] typeParametersSerializers() {
                return null;
            }
        }
        
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000f\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004H\u00c6\u0001\u00a8\u0006\u0006"}, d2 = {"Lcom/securevote/remote/crypto/SVRCrypto$PQPackage$Companion;", "", "()V", "serializer", "Lkotlinx/serialization/KSerializer;", "Lcom/securevote/remote/crypto/SVRCrypto$PQPackage;", "app_debug"})
        public static final class Companion {
            
            @org.jetbrains.annotations.NotNull()
            public final kotlinx.serialization.KSerializer<com.securevote.remote.crypto.SVRCrypto.PQPackage> serializer() {
                return null;
            }
            
            private Companion() {
                super();
            }
        }
    }
}