package com.securevote.remote.util

import android.app.Activity
import android.view.WindowManager
import java.security.SecureRandom
import java.util.Arrays

/**
 * Security utilities for SecureVote Remote.
 */
object SecurityUtil {

    /**
     * Securely wipe a byte array by overwriting with random data, then zeros.
     * Prevents compiler/JIT from optimizing away the wipe.
     *
     * This mirrors the memwipe() utility in the Go application layer
     * (see APPLICATION.md — "Memory wiping" section).
     */
    fun secureWipe(data: ByteArray?) {
        if (data == null || data.isEmpty()) return

        // First pass: random data (defeats cold boot attacks that look for zeroed regions)
        SecureRandom().nextBytes(data)

        // Second pass: zeros
        Arrays.fill(data, 0.toByte())

        // Third pass: force the JVM to not optimize this away
        // by reading from the array (prevents dead store elimination)
        @Suppress("UNUSED_VARIABLE")
        var sink = 0
        for (b in data) sink = sink or b.toInt()
    }

    /**
     * Securely wipe a char array (for PIN data).
     */
    fun secureWipe(data: CharArray?) {
        if (data == null || data.isEmpty()) return
        Arrays.fill(data, '\u0000')
    }

    /**
     * Securely wipe a string by clearing its backing char array.
     * NOTE: Strings in Java/Kotlin are immutable and pooled — this is best-effort.
     * For truly sensitive data, use CharArray instead of String.
     */
    fun secureWipeString(data: String?) {
        if (data == null) return
        try {
            val field = String::class.java.getDeclaredField("value")
            field.isAccessible = true
            val chars = field.get(data)
            if (chars is ByteArray) Arrays.fill(chars, 0.toByte())
            if (chars is CharArray) Arrays.fill(chars, '\u0000')
        } catch (_: Exception) {
            // Reflection may fail on some VMs — accepted limitation
        }
    }

    /**
     * Assert FLAG_SECURE on an activity's window.
     * Called in onCreate and onResume as defense-in-depth.
     */
    fun enforceScreenSecurity(activity: Activity) {
        activity.window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
    }

    /**
     * Check if the device is likely rooted.
     * This is a heuristic check — not as reliable as Play Integrity,
     * but provides an additional signal.
     */
    fun isDevicePotentiallyRooted(): Boolean {
        val rootIndicators = listOf(
            "/system/app/Superuser.apk",
            "/system/xbin/su",
            "/system/bin/su",
            "/sbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/data/local/su",
            "/system/bin/failsafe/su",
        )

        return rootIndicators.any { path ->
            try {
                java.io.File(path).exists()
            } catch (_: Exception) {
                false
            }
        }
    }

    /**
     * Check if the app is running in an emulator.
     */
    fun isEmulator(): Boolean {
        return (android.os.Build.FINGERPRINT.startsWith("generic")
                || android.os.Build.FINGERPRINT.startsWith("unknown")
                || android.os.Build.MODEL.contains("google_sdk")
                || android.os.Build.MODEL.contains("Emulator")
                || android.os.Build.MODEL.contains("Android SDK built for x86")
                || android.os.Build.MANUFACTURER.contains("Genymotion")
                || android.os.Build.BRAND.startsWith("generic")
                || android.os.Build.DEVICE.startsWith("generic")
                || "google_sdk" == android.os.Build.PRODUCT)
    }

    /**
     * Check if an accessibility service that could read screen content is active.
     * Allowlists known assistive technologies (TalkBack, etc.).
     */
    fun hasUnknownAccessibilityService(context: android.content.Context): Boolean {
        val am = context.getSystemService(android.content.Context.ACCESSIBILITY_SERVICE)
                as android.view.accessibility.AccessibilityManager

        if (!am.isEnabled) return false

        val allowedPackages = setOf(
            "com.google.android.marvin.talkback",   // Google TalkBack
            "com.samsung.accessibility",             // Samsung accessibility
            "com.android.switchaccess",              // Switch Access
        )

        val enabledServices = am.getEnabledAccessibilityServiceList(
            android.accessibilityservice.AccessibilityServiceInfo.FEEDBACK_ALL_MASK
        )

        return enabledServices.any { service ->
            val pkg = service.resolveInfo.serviceInfo.packageName
            pkg !in allowedPackages
        }
    }
}

/**
 * Timestamp utilities matching the SecureVote protocol.
 */
object TimeUtil {
    /**
     * Current UTC timestamp in ISO 8601 format.
     */
    fun nowISO(): String = java.time.Instant.now().toString()

    /**
     * Current Unix epoch seconds.
     */
    fun nowEpochSeconds(): Long = System.currentTimeMillis() / 1000
}
