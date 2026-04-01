package com.securevote.remote.crypto

import android.content.Context
import com.google.android.play.core.integrity.IntegrityManagerFactory
import com.google.android.play.core.integrity.IntegrityTokenRequest
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Device Attestation using Google Play Integrity API.
 *
 * Proves to sv-remote-gateway that this app is:
 *   1. Running on genuine, unmodified Android hardware
 *   2. Not rooted or running a custom ROM
 *   3. Not an emulator
 *   4. The genuine SecureVote Remote APK (not a repackaged/modified version)
 *
 * The attestation token is sent to the server, which verifies it against
 * Google's servers. This is the mobile equivalent of the TPM attestation
 * in the in-person voting machine.
 *
 * LIMITATIONS (acknowledged in ABSENTEE.md):
 *   - Requires Google Play Services (not available on some devices)
 *   - Google is a trusted third party (the in-person TPM has no such dependency)
 *   - A sufficiently advanced root hide (e.g., Magisk with DenyList) may pass
 *   - This is weaker than custom hardware TPM — accepted as a tradeoff vs. paper mail
 */
object DeviceAttestation {

    /**
     * Request a fresh Play Integrity attestation token.
     *
     * @param context Application context
     * @param nonce A server-provided nonce to prevent replay attacks.
     *              The server generates this and expects it back in the token.
     * @return Base64-encoded integrity token for server verification
     */
    suspend fun getIntegrityToken(context: Context, nonce: String): String {
        return suspendCancellableCoroutine { cont ->
            val integrityManager = IntegrityManagerFactory.create(context)

            val request = IntegrityTokenRequest.builder()
                .setNonce(nonce)
                .build()

            integrityManager.requestIntegrityToken(request)
                .addOnSuccessListener { response ->
                    cont.resume(response.token())
                }
                .addOnFailureListener { e ->
                    cont.resumeWithException(
                        AttestationException("Play Integrity request failed: ${e.message}", e)
                    )
                }
        }
    }

    /**
     * Get hardware-backed key attestation certificate chain.
     * This proves the device key was generated inside a genuine Android Keystore
     * (TEE or StrongBox).
     *
     * @return Base64-encoded X.509 certificate chain
     */
    fun getKeyAttestationChain(): String {
        val keyStore = java.security.KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)

        val alias = "svr_device_key"
        if (!keyStore.containsAlias(alias)) {
            throw AttestationException("Device key not yet created — register first")
        }

        val certChain = keyStore.getCertificateChain(alias)
            ?: throw AttestationException("No certificate chain for device key")

        // Encode each certificate as Base64
        val encoded = certChain.joinToString(",") { cert ->
            android.util.Base64.encodeToString(cert.encoded, android.util.Base64.NO_WRAP)
        }

        return encoded
    }

    /**
     * Check if the device has StrongBox (hardware security module).
     * StrongBox provides the highest level of key protection on Android.
     */
    fun hasStrongBox(context: Context): Boolean {
        return context.packageManager.hasSystemFeature(
            android.content.pm.PackageManager.FEATURE_STRONGBOX_KEYSTORE
        )
    }

    /**
     * Check if NFC is available on this device.
     */
    fun hasNFC(context: Context): Boolean {
        return android.nfc.NfcAdapter.getDefaultAdapter(context) != null
    }

    /**
     * Check minimum device requirements for SVR.
     */
    fun checkDeviceCompatibility(context: Context): DeviceCompatibility {
        val hasNfc = hasNFC(context)
        val hasStrongBox = hasStrongBox(context)
        val hasCamera = context.packageManager.hasSystemFeature(
            android.content.pm.PackageManager.FEATURE_CAMERA_ANY
        )
        val apiLevel = android.os.Build.VERSION.SDK_INT

        return DeviceCompatibility(
            compatible = hasCamera && apiLevel >= 28,
            hasNFC = hasNfc,
            hasStrongBox = hasStrongBox,
            hasCamera = hasCamera,
            apiLevel = apiLevel,
            warnings = buildList {
                if (!hasNfc) add("No NFC — optical ID scan only (reduced assurance)")
                if (!hasStrongBox) add("No StrongBox — using TEE (still hardware-backed)")
                if (apiLevel < 28) add("Android version too old — minimum API 28 required")
            }
        )
    }

    data class DeviceCompatibility(
        val compatible: Boolean,
        val hasNFC: Boolean,
        val hasStrongBox: Boolean,
        val hasCamera: Boolean,
        val apiLevel: Int,
        val warnings: List<String>,
    )

    class AttestationException(message: String, cause: Throwable? = null) : Exception(message, cause)
}
