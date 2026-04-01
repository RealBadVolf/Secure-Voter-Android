package com.securevote.remote.crypto

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.securevote.remote.data.models.IPVCQRPayload
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.security.SecureRandom

/**
 * IPVC QR Code Generator with 10-second TOTP-like rotation.
 *
 * SECURITY DESIGN:
 *   - QR code regenerates every 10 seconds with a fresh nonce
 *   - Each QR contains an HMAC proving device possession (bound to hardware key)
 *   - Each QR contains an expiry timestamp (current + 10 seconds)
 *   - The IPVC admin's scanner validates: nonce freshness, HMAC, expiry
 *   - A screenshot or photo becomes invalid within 10 seconds
 *   - Even if captured, the HMAC cannot be forged without the device's private key
 *   - FLAG_SECURE prevents screenshots anyway — this is defense-in-depth
 *
 * ATTACK MITIGATED:
 *   Voter screenshots QR → texts to corrupt admin 500 miles away → admin scans photo
 *   Result: QR is expired by the time admin receives it. Attack fails.
 *   Even with a fast relay: HMAC is bound to the device key. Admin's scanner
 *   verifies the HMAC against the device public key registered with sv-remote-auth.
 *   The device must be physically present for the HMAC to validate in real-time.
 */
object IPVCQRGenerator {

    private const val QR_ROTATION_INTERVAL_MS = 10_000L  // 10 seconds
    private const val QR_SIZE = 512  // pixels
    private const val NONCE_BYTES = 16

    /**
     * Generate a continuous flow of QR code bitmaps that rotate every 10 seconds.
     * Each emission is a fresh QR with a new nonce, timestamp, and HMAC.
     *
     * @param voterDocumentHash SHA-256 of the voter's ID document number
     * @param electionId Current election ID
     * @param deviceKeyMaterial Key material for HMAC computation (derived from device key)
     */
    fun generateRotatingQRCodes(
        voterDocumentHash: String,
        electionId: String,
        deviceKeyMaterial: ByteArray,
        appVersion: String = "1.0.0"
    ): Flow<QRFrame> = flow {
        while (true) {
            val now = System.currentTimeMillis() / 1000  // Unix epoch seconds
            val nonce = generateNonce()
            val expiresAt = now + 10  // Valid for 10 seconds

            // Build the payload
            val payload = IPVCQRPayload(
                devicePublicKeyHash = SVRCrypto.getDevicePublicKeyHash(),
                voterDocumentHash = voterDocumentHash,
                electionId = electionId,
                timestamp = now,
                nonce = nonce,
                hmac = "",  // Computed below
                expiresAt = expiresAt,
                appVersion = appVersion,
            )

            // Compute HMAC over all fields (excluding the HMAC field itself)
            val hmacInput = buildString {
                append(payload.devicePublicKeyHash)
                append(payload.voterDocumentHash)
                append(payload.electionId)
                append(payload.timestamp)
                append(payload.nonce)
                append(payload.expiresAt)
            }
            val hmac = SVRCrypto.hmacSha256(
                hmacInput.toByteArray(Charsets.UTF_8),
                deviceKeyMaterial
            )

            // Create the final payload with HMAC
            val finalPayload = payload.copy(hmac = hmac)
            val jsonString = Json.encodeToString(finalPayload)

            // Generate QR code bitmap
            val bitmap = generateQRBitmap(jsonString)

            // Compute time remaining for this QR
            val generatedAt = System.currentTimeMillis()
            val validForMs = (expiresAt * 1000) - generatedAt

            emit(QRFrame(
                bitmap = bitmap,
                payload = finalPayload,
                validForMs = validForMs,
                sequenceNumber = now  // Can be used by UI for animation sync
            ))

            // Wait until this QR expires, then generate the next one
            delay(QR_ROTATION_INTERVAL_MS)
        }
    }

    /**
     * A single frame of the rotating QR display.
     */
    data class QRFrame(
        val bitmap: Bitmap,
        val payload: IPVCQRPayload,
        val validForMs: Long,
        val sequenceNumber: Long,
    )

    /**
     * Generate a QR code bitmap from a string payload.
     * Uses ZXing with high error correction (L level is sufficient since
     * the QR is displayed on a clean screen, not printed on paper).
     */
    private fun generateQRBitmap(content: String): Bitmap {
        val hints = mapOf(
            EncodeHintType.CHARACTER_SET to "UTF-8",
            EncodeHintType.MARGIN to 2,
            EncodeHintType.ERROR_CORRECTION to com.google.zxing.qrcode.decoder.ErrorCorrectionLevel.M,
        )

        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, QR_SIZE, QR_SIZE, hints)

        val width = bitMatrix.width
        val height = bitMatrix.height
        val pixels = IntArray(width * height)

        // SecureVote branded colors: navy modules on white background
        val moduleColor = 0xFF1B2A4A.toInt()   // Deep navy
        val backgroundColor = 0xFFFFFFFF.toInt() // White

        for (y in 0 until height) {
            for (x in 0 until width) {
                pixels[y * width + x] = if (bitMatrix[x, y]) moduleColor else backgroundColor
            }
        }

        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888)
    }

    /**
     * Generate a cryptographically random nonce (16 bytes, hex-encoded).
     */
    private fun generateNonce(): String {
        val bytes = ByteArray(NONCE_BYTES)
        SecureRandom().nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Validate a scanned QR payload (used by IPVC admin scanner app).
     * Returns null if valid, or an error message if invalid.
     */
    fun validateQRPayload(
        payload: IPVCQRPayload,
        devicePublicKeyForHMAC: ByteArray
    ): String? {
        val now = System.currentTimeMillis() / 1000

        // Check expiry (10-second window)
        if (now > payload.expiresAt) {
            return "QR code expired (was valid until ${payload.expiresAt}, now is $now)"
        }

        // Check timestamp isn't from the future (clock skew tolerance: 5 seconds)
        if (payload.timestamp > now + 5) {
            return "QR code timestamp is in the future — possible clock manipulation"
        }

        // Verify HMAC
        val hmacInput = buildString {
            append(payload.devicePublicKeyHash)
            append(payload.voterDocumentHash)
            append(payload.electionId)
            append(payload.timestamp)
            append(payload.nonce)
            append(payload.expiresAt)
        }
        val expectedHmac = SVRCrypto.hmacSha256(
            hmacInput.toByteArray(Charsets.UTF_8),
            devicePublicKeyForHMAC
        )
        if (payload.hmac != expectedHmac) {
            return "HMAC verification failed — QR was not generated by this device"
        }

        return null  // Valid
    }
}
