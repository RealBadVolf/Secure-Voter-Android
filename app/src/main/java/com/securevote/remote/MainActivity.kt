package com.securevote.remote

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.securevote.remote.ui.SVRApp
import com.securevote.remote.ui.theme.SecureVoteTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main Activity for SecureVote Remote.
 *
 * CRITICAL SECURITY: FLAG_SECURE is set on the window at creation time.
 * This prevents:
 *   - Screenshots (system screenshot API returns black)
 *   - Screen recording (MediaProjection captures black frames)
 *   - Screen casting (Chromecast, AirPlay equivalent shows black)
 *   - Screen sharing (any share API gets black frames)
 *   - Recent apps thumbnail (shows black in app switcher)
 *   - Accessibility services reading screen content (partially — allowlisted services like TalkBack still work)
 *
 * This is set UNCONDITIONALLY on the Activity — there is no user toggle,
 * no debug override, no way to disable it. The ballot is never capturable.
 *
 * Combined with the 10-second rotating IPVC QR code, this means:
 *   1. You cannot screenshot the QR to send to a remote corrupt admin
 *   2. You cannot screen-record your ballot selections for a coercer
 *   3. You cannot cast your screen to prove how you voted to a vote buyer
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ================================================================
        // FLAG_SECURE — THE MOST IMPORTANT LINE IN THIS FILE
        // Set BEFORE setContent so the window is secure from the first frame.
        // ================================================================
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        enableEdgeToEdge()

        // Prevent the activity from appearing in recent apps with a readable thumbnail
        // (FLAG_SECURE already blacks it out, but this is belt-and-suspenders)
        setTaskDescription(
            android.app.ActivityManager.TaskDescription.Builder()
                .setLabel("SecureVote Remote")
                .build()
        )

        setContent {
            SecureVoteTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SVRApp()
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        // Additional protection: when the app goes to background,
        // any in-progress ballot selections are in memory only
        // and will be cleared if the process is killed.
        // This is by design — no ballot data persists to disk.
    }

    override fun onResume() {
        super.onResume()
        // Re-assert FLAG_SECURE every time we resume
        // (defense against any framework bug that might clear it)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
    }
}
