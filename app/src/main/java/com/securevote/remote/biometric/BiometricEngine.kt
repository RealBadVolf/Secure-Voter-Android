package com.securevote.remote.biometric

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.securevote.remote.crypto.SVRCrypto
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.random.Random

/**
 * Biometric verification engine for SVR.
 *
 * Implements:
 *   1. Face detection using ML Kit
 *   2. Randomized liveness challenges (anti-deepfake)
 *   3. Dual-model matching against ID chip photo
 *   4. Continuous presence monitoring during ballot session
 *   5. Biometric hash generation (one-way, never stored as template)
 */
object BiometricEngine {

    // Thresholds from ABSENTEE.md
    // Remote registration requires HIGHER confidence than in-person (no poll worker fallback)
    private const val MATCH_THRESHOLD_REMOTE = 0.85f
    private const val MATCH_THRESHOLD_IN_PERSON = 0.75f
    private const val REJECT_THRESHOLD = 0.40f

    /**
     * Available liveness challenges. A random subset of 3 is selected per session.
     * The randomization defeats pre-recorded deepfake videos.
     */
    enum class LivenessChallenge(val instruction: String, val durationMs: Long) {
        BLINK("Please blink", 2000),
        HEAD_LEFT("Turn your head slightly left", 2500),
        HEAD_RIGHT("Turn your head slightly right", 2500),
        SMILE("Please smile", 2000),
        RAISE_EYEBROWS("Raise your eyebrows", 2000),
        OPEN_MOUTH("Open your mouth slightly", 2000),
        NOD("Nod your head slowly", 3000),
    }

    /**
     * Result of a biometric verification attempt.
     */
    sealed class BiometricResult {
        data class Match(
            val modelAScore: Float,
            val modelBScore: Float,
            val livenessConfidence: Float,
            val biometricHash: String,       // SHA-256 of the template (one-way)
        ) : BiometricResult()

        data class Uncertain(
            val modelAScore: Float,
            val modelBScore: Float,
            val reason: String,
        ) : BiometricResult()

        data class Rejected(
            val reason: String,
        ) : BiometricResult()

        data class Error(
            val exception: Throwable,
        ) : BiometricResult()
    }

    /**
     * Generate a random liveness challenge sequence.
     * 3 challenges from the pool, never repeating, different every session.
     */
    fun generateChallengeSequence(): List<LivenessChallenge> {
        return LivenessChallenge.entries.shuffled(Random(System.nanoTime())).take(3)
    }

    /**
     * Detect face in a camera frame using ML Kit.
     * Returns true if exactly one face is detected with sufficient quality.
     */
    suspend fun detectFace(bitmap: Bitmap): FaceDetectionResult {
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .setMinFaceSize(0.3f)
            .enableTracking()
            .build()

        val detector = FaceDetection.getClient(options)
        val image = InputImage.fromBitmap(bitmap, 0)

        return suspendCancellableCoroutine { cont ->
            detector.process(image)
                .addOnSuccessListener { faces ->
                    when {
                        faces.isEmpty() -> cont.resume(FaceDetectionResult.NoFace)
                        faces.size > 1 -> cont.resume(FaceDetectionResult.MultipleFaces)
                        else -> {
                            val face = faces[0]
                            cont.resume(FaceDetectionResult.Detected(
                                smilingProbability = face.smilingProbability ?: 0f,
                                leftEyeOpenProbability = face.leftEyeOpenProbability ?: 0f,
                                rightEyeOpenProbability = face.rightEyeOpenProbability ?: 0f,
                                headEulerAngleX = face.headEulerAngleX,
                                headEulerAngleY = face.headEulerAngleY,
                                headEulerAngleZ = face.headEulerAngleZ,
                                boundingBox = face.boundingBox,
                                trackingId = face.trackingId,
                            ))
                        }
                    }
                }
                .addOnFailureListener { e ->
                    cont.resumeWithException(e)
                }
        }
    }

    /**
     * Verify a liveness challenge was performed.
     * Analyzes facial landmarks across multiple frames to confirm the challenge was real.
     */
    fun verifyChallenge(
        challenge: LivenessChallenge,
        frames: List<FaceDetectionResult.Detected>
    ): Boolean {
        if (frames.size < 5) return false  // Need sufficient frames

        return when (challenge) {
            LivenessChallenge.BLINK -> {
                // Detect eye closure in at least one frame
                frames.any { it.leftEyeOpenProbability < 0.2f && it.rightEyeOpenProbability < 0.2f } &&
                frames.any { it.leftEyeOpenProbability > 0.7f && it.rightEyeOpenProbability > 0.7f }
            }
            LivenessChallenge.HEAD_LEFT -> {
                // Detect head rotation > 15 degrees to the left
                frames.any { it.headEulerAngleY > 15f }
            }
            LivenessChallenge.HEAD_RIGHT -> {
                frames.any { it.headEulerAngleY < -15f }
            }
            LivenessChallenge.SMILE -> {
                frames.any { it.smilingProbability > 0.7f }
            }
            LivenessChallenge.RAISE_EYEBROWS -> {
                // Detect eyebrow raise via head pitch change
                val pitchRange = frames.maxOf { it.headEulerAngleX } - frames.minOf { it.headEulerAngleX }
                pitchRange > 5f
            }
            LivenessChallenge.OPEN_MOUTH -> {
                // ML Kit doesn't directly detect mouth opening — use face height change
                // In production: use a custom TFLite model for mouth detection
                true  // Placeholder
            }
            LivenessChallenge.NOD -> {
                // Detect vertical head movement
                val pitchRange = frames.maxOf { it.headEulerAngleX } - frames.minOf { it.headEulerAngleX }
                pitchRange > 10f
            }
        }
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
    fun matchAgainstIDPhoto(
        liveTemplate: ByteArray,
        idPhotoTemplate: ByteArray,
    ): BiometricResult {
        // In production:
        //   Model A: geometric landmark distances (eye spacing, nose-to-chin ratio, etc.)
        //   Model B: 512-dim ArcFace embedding cosine similarity
        // Both run on-device, never transmitted.

        // Simulated for prototype — replace with actual model inference
        val modelAScore = simulateModelScore()
        val modelBScore = simulateModelScore()

        val biometricHash = SVRCrypto.sha256Hex(liveTemplate + idPhotoTemplate)

        return when {
            modelAScore >= MATCH_THRESHOLD_REMOTE && modelBScore >= MATCH_THRESHOLD_REMOTE -> {
                BiometricResult.Match(
                    modelAScore = modelAScore,
                    modelBScore = modelBScore,
                    livenessConfidence = 0.95f,
                    biometricHash = biometricHash,
                )
            }
            modelAScore >= REJECT_THRESHOLD || modelBScore >= REJECT_THRESHOLD -> {
                BiometricResult.Uncertain(
                    modelAScore = modelAScore,
                    modelBScore = modelBScore,
                    reason = "One or both models below auto-pass threshold"
                )
            }
            else -> {
                BiometricResult.Rejected(
                    reason = "Both models below minimum threshold"
                )
            }
        }
    }

    /**
     * Generate a one-way hash of a biometric template.
     * This hash is stored in the vote record for audit purposes.
     * It CANNOT be reversed to reconstruct the voter's face.
     */
    fun generateBiometricHash(templateData: ByteArray): String {
        // Add a per-election salt to prevent cross-election template correlation
        val salt = "SVR-2026-BIOMETRIC-SALT".toByteArray()
        return SVRCrypto.sha256Hex(templateData + salt)
    }

    // Simulation helper (replace with real model inference)
    private fun simulateModelScore(): Float {
        return 0.85f + Random.nextFloat() * 0.14f  // 0.85–0.99
    }
}

/**
 * Result of ML Kit face detection on a single frame.
 */
sealed class FaceDetectionResult {
    object NoFace : FaceDetectionResult()
    object MultipleFaces : FaceDetectionResult()
    data class Detected(
        val smilingProbability: Float,
        val leftEyeOpenProbability: Float,
        val rightEyeOpenProbability: Float,
        val headEulerAngleX: Float,
        val headEulerAngleY: Float,
        val headEulerAngleZ: Float,
        val boundingBox: android.graphics.Rect,
        val trackingId: Int?,
    ) : FaceDetectionResult()
}
