package com.securevote.remote;

import android.app.Application;
import dagger.hilt.android.HiltAndroidApp;

/**
 * SVR Application entry point.
 *
 * Initializes:
 *  - Hilt dependency injection
 *  - Bouncy Castle PQC provider (for ML-KEM-1024)
 *  - Device key pair in Android Keystore (if not already created)
 *
 * SECURITY NOTE: No sensitive data is initialized here that persists
 * beyond the app's lifecycle. All crypto keys live in hardware.
 */
@dagger.hilt.android.HiltAndroidApp()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H\u0016\u00a8\u0006\u0005"}, d2 = {"Lcom/securevote/remote/SVRApplication;", "Landroid/app/Application;", "()V", "onCreate", "", "app_debug"})
public final class SVRApplication extends android.app.Application {
    
    public SVRApplication() {
        super();
    }
    
    @java.lang.Override()
    public void onCreate() {
    }
}