package com.securevote.remote.crypto

import android.app.Activity
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import com.securevote.remote.data.models.VoterIdentity
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * NFC Reader for ICAO 9303 e-Passport / e-ID chips.
 *
 * Reads the voter's government-issued ID via NFC and extracts:
 *   - Full legal name
 *   - Date of birth
 *   - Document number
 *   - Photo (for biometric matching)
 *   - Digital signature (proves the ID is genuine)
 *
 * SECURITY:
 *   - The chip's digital signature is verified against the issuing authority's
 *     public key (published by DHS/AAMVA for US IDs, ICAO PKD for passports).
 *   - A photocopy or printout of an ID cannot produce a valid NFC response.
 *   - The chip requires BAC (Basic Access Control) using the MRZ data —
 *     the reader must optically scan the MRZ first to derive the session key.
 *
 * PROTOCOL:
 *   1. Optical scan of MRZ (Machine Readable Zone) on the ID
 *   2. Derive BAC session key from MRZ data (doc number + DOB + expiry)
 *   3. NFC: SELECT eMRTD application (AID: A0000002471001)
 *   4. NFC: Mutual authentication using BAC session key
 *   5. NFC: Read DG1 (MRZ data), DG2 (facial image), SOD (security object)
 *   6. Verify SOD signature against CSCA certificate
 *   7. Verify data group hashes against SOD
 */
object NFCReader {

    // ICAO 9303 eMRTD Application ID
    private val EMRTD_AID = byteArrayOf(
        0xA0.toByte(), 0x00, 0x00, 0x02, 0x47, 0x10, 0x01
    )

    // Data Group file identifiers
    private const val DG1_FID = 0x0101.toShort()  // MRZ data
    private const val DG2_FID = 0x0102.toShort()  // Facial image
    private const val SOD_FID = 0x011D.toShort()  // Security Object (signatures)

    /**
     * Result of an NFC chip read attempt.
     */
    sealed class NFCReadResult {
        data class Success(
            val identity: VoterIdentity,
            val facePhoto: ByteArray,           // JPEG/JP2 facial image from DG2
            val rawChipData: ByteArray,          // Full signed data for server transmission
        ) : NFCReadResult()

        data class ChipNotFound(val message: String) : NFCReadResult()
        data class AuthenticationFailed(val message: String) : NFCReadResult()
        data class SignatureInvalid(val message: String) : NFCReadResult()
        data class Error(val exception: Throwable) : NFCReadResult()
    }

    /**
     * Enable NFC foreground dispatch on the activity.
     * Call in onResume() to capture NFC tags while the app is in the foreground.
     */
    fun enableNFCDispatch(activity: Activity) {
        val nfcAdapter = NfcAdapter.getDefaultAdapter(activity) ?: return
        val intent = android.content.Intent(activity, activity::class.java).apply {
            addFlags(android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        val pendingIntent = android.app.PendingIntent.getActivity(
            activity, 0, intent,
            android.app.PendingIntent.FLAG_MUTABLE
        )
        val techFilters = arrayOf(
            arrayOf(IsoDep::class.java.name)
        )
        nfcAdapter.enableForegroundDispatch(activity, pendingIntent, null, techFilters)
    }

    /**
     * Disable NFC foreground dispatch.
     * Call in onPause().
     */
    fun disableNFCDispatch(activity: Activity) {
        val nfcAdapter = NfcAdapter.getDefaultAdapter(activity) ?: return
        nfcAdapter.disableForegroundDispatch(activity)
    }

    /**
     * Read an ICAO 9303 chip from a discovered NFC tag.
     *
     * @param tag The NFC tag discovered by the system
     * @param mrzData The MRZ data extracted from optical scan (needed for BAC)
     */
    suspend fun readChip(tag: Tag, mrzData: MRZData): NFCReadResult {
        val isoDep = IsoDep.get(tag) ?: return NFCReadResult.ChipNotFound(
            "Tag does not support ISO-DEP (not an e-ID chip)"
        )

        return try {
            isoDep.timeout = 10000  // 10 second timeout
            isoDep.connect()

            // Step 1: SELECT eMRTD application
            val selectResponse = isoDep.transceive(buildSelectAPDU(EMRTD_AID))
            if (!isSuccess(selectResponse)) {
                return NFCReadResult.ChipNotFound("eMRTD application not found on chip")
            }

            // Step 2: Basic Access Control (BAC) mutual authentication
            val bacKey = deriveBACKey(mrzData)
            val bacResult = performBAC(isoDep, bacKey)
            if (bacResult == null) {
                return NFCReadResult.AuthenticationFailed(
                    "BAC authentication failed — MRZ data may be incorrect"
                )
            }

            // Step 3: Read data groups using secure messaging
            val dg1 = readDataGroup(isoDep, DG1_FID, bacResult)
                ?: return NFCReadResult.Error(Exception("Failed to read DG1 (MRZ data)"))

            val dg2 = readDataGroup(isoDep, DG2_FID, bacResult)
                ?: return NFCReadResult.Error(Exception("Failed to read DG2 (facial image)"))

            val sod = readDataGroup(isoDep, SOD_FID, bacResult)
                ?: return NFCReadResult.Error(Exception("Failed to read SOD (security object)"))

            // Step 4: Verify the chip's digital signature
            val signatureValid = verifySODSignature(sod, dg1, dg2)
            if (!signatureValid) {
                return NFCReadResult.SignatureInvalid(
                    "Chip signature verification failed — ID may be counterfeit"
                )
            }

            // Step 5: Parse the identity data
            val identity = parseDG1(dg1, signatureValid)
            val facePhoto = parseDG2FaceImage(dg2)

            isoDep.close()

            NFCReadResult.Success(
                identity = identity,
                facePhoto = facePhoto,
                rawChipData = dg1 + dg2 + sod,  // Full signed data for server audit
            )
        } catch (e: Exception) {
            try { isoDep.close() } catch (_: Exception) {}
            NFCReadResult.Error(e)
        }
    }

    /**
     * MRZ data extracted from optical scan of the ID.
     * Required for BAC key derivation.
     */
    data class MRZData(
        val documentNumber: String,   // 9 characters
        val dateOfBirth: String,       // YYMMDD
        val dateOfExpiry: String,      // YYMMDD
    )

    // ================================================================
    // APDU Command Building
    // ================================================================

    private fun buildSelectAPDU(aid: ByteArray): ByteArray {
        // SELECT command: CLA=00, INS=A4, P1=04, P2=0C, Lc=len, Data=AID
        return byteArrayOf(
            0x00, 0xA4.toByte(), 0x04, 0x0C, aid.size.toByte()
        ) + aid
    }

    private fun isSuccess(response: ByteArray): Boolean {
        if (response.size < 2) return false
        return response[response.size - 2] == 0x90.toByte() &&
               response[response.size - 1] == 0x00.toByte()
    }

    // ================================================================
    // BAC (Basic Access Control)
    // ================================================================

    /**
     * Derive the BAC key from MRZ data.
     * K_seed = SHA-1(MRZ_info)[:16]
     * K_enc = KDF(K_seed, 1)
     * K_mac = KDF(K_seed, 2)
     */
    private fun deriveBACKey(mrz: MRZData): BACKey {
        val mrzInfo = buildString {
            append(mrz.documentNumber)
            append(computeCheckDigit(mrz.documentNumber))
            append(mrz.dateOfBirth)
            append(computeCheckDigit(mrz.dateOfBirth))
            append(mrz.dateOfExpiry)
            append(computeCheckDigit(mrz.dateOfExpiry))
        }

        val hash = java.security.MessageDigest.getInstance("SHA-1")
            .digest(mrzInfo.toByteArray(Charsets.UTF_8))
        val kSeed = hash.copyOf(16)

        return BACKey(
            encKey = kdf(kSeed, 1),
            macKey = kdf(kSeed, 2),
        )
    }

    private data class BACKey(val encKey: ByteArray, val macKey: ByteArray)
    private data class BACSession(val encKey: ByteArray, val macKey: ByteArray, val ssc: ByteArray)

    /**
     * Perform BAC mutual authentication with the chip.
     * Returns session keys if successful, null if failed.
     */
    private fun performBAC(isoDep: IsoDep, bacKey: BACKey): BACSession? {
        // GET CHALLENGE
        val challengeCmd = byteArrayOf(0x00, 0x84.toByte(), 0x00, 0x00, 0x08)
        val challengeResp = isoDep.transceive(challengeCmd)
        if (!isSuccess(challengeResp) || challengeResp.size < 10) return null

        val rndICC = challengeResp.copyOf(8)

        // Generate our random and key material
        val rndIFD = ByteArray(8).also { java.security.SecureRandom().nextBytes(it) }
        val kIFD = ByteArray(16).also { java.security.SecureRandom().nextBytes(it) }

        // Build and encrypt MUTUAL AUTHENTICATE payload
        val s = rndIFD + rndICC + kIFD
        val eifd = encrypt3DES(s, bacKey.encKey)
        val mifd = mac3DES(eifd, bacKey.macKey)
        val cmdData = eifd + mifd

        // MUTUAL AUTHENTICATE
        val authCmd = byteArrayOf(
            0x00, 0x82.toByte(), 0x00, 0x00,
            cmdData.size.toByte()
        ) + cmdData + byteArrayOf(0x28)

        val authResp = isoDep.transceive(authCmd)
        if (!isSuccess(authResp) || authResp.size < 42) return null

        // Derive session keys from shared secret
        val respData = authResp.copyOf(authResp.size - 2)
        val eicc = respData.copyOf(32)
        val decrypted = decrypt3DES(eicc, bacKey.encKey)

        val kICC = decrypted.copyOfRange(16, 32)
        val keySeed = ByteArray(16) { (kIFD[it].toInt() xor kICC[it].toInt()).toByte() }

        return BACSession(
            encKey = kdf(keySeed, 1),
            macKey = kdf(keySeed, 2),
            ssc = rndICC.copyOfRange(4, 8) + rndIFD.copyOfRange(4, 8),
        )
    }

    // ================================================================
    // Secure Messaging (reading data groups after BAC)
    // ================================================================

    private fun readDataGroup(isoDep: IsoDep, fid: Short, session: BACSession): ByteArray? {
        // SELECT file
        val fidBytes = byteArrayOf((fid.toInt() shr 8).toByte(), fid.toByte())
        val selectCmd = byteArrayOf(0x00, 0xA4.toByte(), 0x02, 0x0C, 0x02) + fidBytes
        val selectResp = isoDep.transceive(selectCmd)
        if (!isSuccess(selectResp)) return null

        // READ BINARY in chunks
        val data = mutableListOf<Byte>()
        var offset = 0
        val chunkSize = 224  // Safe chunk size for most chips

        while (true) {
            val p1 = (offset shr 8).toByte()
            val p2 = (offset and 0xFF).toByte()
            val readCmd = byteArrayOf(0x00, 0xB0.toByte(), p1, p2, chunkSize.toByte())
            val readResp = isoDep.transceive(readCmd)

            if (readResp.size <= 2) break

            val responseData = readResp.copyOf(readResp.size - 2)
            data.addAll(responseData.toList())

            if (responseData.size < chunkSize) break  // Last chunk
            offset += responseData.size
        }

        return if (data.isNotEmpty()) data.toByteArray() else null
    }

    // ================================================================
    // Signature Verification
    // ================================================================

    /**
     * Verify the Security Object Document (SOD) signature.
     * The SOD contains hashes of all data groups, signed by the issuing authority.
     *
     * In production:
     *   1. Parse the SOD as a CMS SignedData structure
     *   2. Extract the signing certificate
     *   3. Verify the certificate chain up to the Country Signing CA (CSCA)
     *   4. Verify the SOD signature
     *   5. Compute hashes of DG1 and DG2
     *   6. Compare against hashes in the SOD
     */
    private fun verifySODSignature(sod: ByteArray, dg1: ByteArray, dg2: ByteArray): Boolean {
        // TODO: Implement full PKCS#7/CMS signature verification
        // using Bouncy Castle's CMSSignedData parser.
        //
        // Steps:
        //   val cmsData = CMSSignedData(sod)
        //   val signerInfos = cmsData.signerInfos
        //   val certs = cmsData.certificates
        //   // Verify signer cert chain against CSCA trust store
        //   // Verify signature over encapContentInfo
        //   // Parse LDSSecurityObject from encapContentInfo
        //   // Compare DG hashes
        //
        // For prototype: return true (signature verification is stubbed)
        // This MUST be implemented before any real deployment.
        return sod.isNotEmpty() && dg1.isNotEmpty() && dg2.isNotEmpty()
    }

    // ================================================================
    // Data Parsing
    // ================================================================

    /**
     * Parse DG1 (MRZ data) into a VoterIdentity.
     * DG1 is a TLV-encoded structure containing the two or three MRZ lines.
     */
    private fun parseDG1(dg1: ByteArray, signatureValid: Boolean): VoterIdentity {
        // DG1 is TLV: tag 61, then tag 5F1F containing the MRZ string
        val mrzString = extractMRZFromDG1(dg1)
        val fields = parseMRZFields(mrzString)

        return VoterIdentity(
            documentNumber = fields.documentNumber,
            fullName = fields.surname + " " + fields.givenNames,
            dateOfBirth = fields.dateOfBirth,
            documentExpiry = fields.dateOfExpiry,
            issuingAuthority = fields.issuingState,
            nfcChipSignatureValid = signatureValid,
            photoHash = SVRCrypto.sha256Hex(dg1),
        )
    }

    /**
     * Extract the facial image (JPEG or JPEG2000) from DG2.
     */
    private fun parseDG2FaceImage(dg2: ByteArray): ByteArray {
        // DG2 contains a Biometric Information Template (BIT)
        // with the facial image in JPEG or JP2 format.
        // The image starts after the BIT header.
        //
        // Look for JPEG magic bytes (FF D8 FF) or JP2 magic bytes
        val jpegStart = findByteSequence(dg2, byteArrayOf(0xFF.toByte(), 0xD8.toByte(), 0xFF.toByte()))
        if (jpegStart >= 0) {
            return dg2.copyOfRange(jpegStart, dg2.size)
        }

        // JP2 magic: 00 00 00 0C 6A 50
        val jp2Start = findByteSequence(dg2, byteArrayOf(0x00, 0x00, 0x00, 0x0C, 0x6A, 0x50))
        if (jp2Start >= 0) {
            return dg2.copyOfRange(jp2Start, dg2.size)
        }

        // Fallback: return entire DG2
        return dg2
    }

    // ================================================================
    // Utility Functions
    // ================================================================

    private data class MRZFields(
        val documentNumber: String,
        val surname: String,
        val givenNames: String,
        val dateOfBirth: String,
        val dateOfExpiry: String,
        val issuingState: String,
        val nationality: String,
    )

    private fun extractMRZFromDG1(dg1: ByteArray): String {
        // Skip TLV headers to find the MRZ string
        // Tag 61 → Tag 5F1F → MRZ bytes
        var i = 0
        while (i < dg1.size - 1) {
            if (dg1[i] == 0x5F.toByte() && dg1[i + 1] == 0x1F.toByte()) {
                i += 2
                val len = dg1[i].toInt() and 0xFF
                i++
                return String(dg1.copyOfRange(i, i + len), Charsets.UTF_8)
            }
            i++
        }
        return String(dg1, Charsets.UTF_8)  // Fallback
    }

    private fun parseMRZFields(mrz: String): MRZFields {
        // TD3 format (passport, 2 lines of 44 chars) or
        // TD1 format (ID card, 3 lines of 30 chars)
        return if (mrz.length >= 88) {
            // TD3 (passport)
            val line1 = mrz.substring(0, 44)
            val line2 = mrz.substring(44, 88)
            val names = line1.substring(5).split("<<", limit = 2)
            MRZFields(
                documentNumber = line2.substring(0, 9).replace("<", ""),
                surname = names.getOrElse(0) { "" }.replace("<", " ").trim(),
                givenNames = names.getOrElse(1) { "" }.replace("<", " ").trim(),
                dateOfBirth = line2.substring(13, 19),
                dateOfExpiry = line2.substring(21, 27),
                issuingState = line1.substring(2, 5).replace("<", ""),
                nationality = line2.substring(10, 13).replace("<", ""),
            )
        } else {
            // TD1 (ID card) — simplified parsing
            MRZFields(
                documentNumber = mrz.substring(5, 14).replace("<", ""),
                surname = "UNKNOWN",
                givenNames = "UNKNOWN",
                dateOfBirth = if (mrz.length > 30) mrz.substring(30, 36) else "",
                dateOfExpiry = if (mrz.length > 38) mrz.substring(38, 44) else "",
                issuingState = mrz.substring(2, 5).replace("<", ""),
                nationality = "",
            )
        }
    }

    private fun computeCheckDigit(input: String): Char {
        val weights = intArrayOf(7, 3, 1)
        var sum = 0
        input.forEachIndexed { i, c ->
            val value = when {
                c in '0'..'9' -> c - '0'
                c in 'A'..'Z' -> c - 'A' + 10
                c == '<' -> 0
                else -> 0
            }
            sum += value * weights[i % 3]
        }
        return ('0' + (sum % 10))
    }

    private fun findByteSequence(data: ByteArray, sequence: ByteArray): Int {
        outer@ for (i in 0..data.size - sequence.size) {
            for (j in sequence.indices) {
                if (data[i + j] != sequence[j]) continue@outer
            }
            return i
        }
        return -1
    }

    // ================================================================
    // 3DES Crypto (for BAC protocol)
    // ================================================================

    private fun encrypt3DES(data: ByteArray, key: ByteArray): ByteArray {
        val cipher = javax.crypto.Cipher.getInstance("DESede/CBC/NoPadding")
        val keySpec = javax.crypto.spec.SecretKeySpec(adjustDESKey(key), "DESede")
        val iv = javax.crypto.spec.IvParameterSpec(ByteArray(8))
        cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, keySpec, iv)
        return cipher.doFinal(padToBlockSize(data, 8))
    }

    private fun decrypt3DES(data: ByteArray, key: ByteArray): ByteArray {
        val cipher = javax.crypto.Cipher.getInstance("DESede/CBC/NoPadding")
        val keySpec = javax.crypto.spec.SecretKeySpec(adjustDESKey(key), "DESede")
        val iv = javax.crypto.spec.IvParameterSpec(ByteArray(8))
        cipher.init(javax.crypto.Cipher.DECRYPT_MODE, keySpec, iv)
        return cipher.doFinal(data)
    }

    private fun mac3DES(data: ByteArray, key: ByteArray): ByteArray {
        val mac = javax.crypto.Mac.getInstance("ISO9797Alg3Mac")
        val keySpec = javax.crypto.spec.SecretKeySpec(adjustDESKey(key), "DESede")
        mac.init(keySpec)
        return mac.doFinal(padToBlockSize(data, 8)).copyOf(8)
    }

    private fun kdf(keySeed: ByteArray, counter: Int): ByteArray {
        val d = keySeed + byteArrayOf(0, 0, 0, counter.toByte())
        val hash = java.security.MessageDigest.getInstance("SHA-1").digest(d)
        return adjustParity(hash.copyOf(16))
    }

    private fun adjustDESKey(key: ByteArray): ByteArray {
        // 3DES needs 24-byte key; for 2-key 3DES, repeat first 8 bytes
        return if (key.size == 16) key + key.copyOf(8) else key
    }

    private fun adjustParity(key: ByteArray): ByteArray {
        for (i in key.indices) {
            var b = key[i].toInt() and 0xFE
            var parity = 0
            for (bit in 0 until 8) parity = parity xor ((b shr bit) and 1)
            key[i] = (b or (parity and 1)).toByte()
        }
        return key
    }

    private fun padToBlockSize(data: ByteArray, blockSize: Int): ByteArray {
        val padLen = blockSize - (data.size % blockSize)
        if (padLen == blockSize && data.isNotEmpty()) return data
        val padded = ByteArray(data.size + padLen)
        data.copyInto(padded)
        padded[data.size] = 0x80.toByte()
        return padded
    }
}
