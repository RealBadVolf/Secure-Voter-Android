package com.securevote.remote

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * SVR Application entry point.
 *
 * Initializes:
 *   - Hilt dependency injection
 *   - Bouncy Castle PQC provider (for ML-KEM-1024)
 *   - Device key pair in Android Keystore (if not already created)
 *
 * SECURITY NOTE: No sensitive data is initialized here that persists
 * beyond the app's lifecycle. All crypto keys live in hardware.
 */
@HiltAndroidApp
class SVRApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Bouncy Castle PQC provider is initialized in SVRCrypto.init{}
        // Device key pair is lazily created on first use
    }
}
