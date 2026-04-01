package com.securevote.remote;

import android.os.Bundle;
import android.view.WindowManager;
import androidx.activity.ComponentActivity;
import androidx.compose.ui.Modifier;
import dagger.hilt.android.AndroidEntryPoint;

/**
 * Main Activity for SecureVote Remote.
 *
 * CRITICAL SECURITY: FLAG_SECURE is set on the window at creation time.
 * This prevents:
 *  - Screenshots (system screenshot API returns black)
 *  - Screen recording (MediaProjection captures black frames)
 *  - Screen casting (Chromecast, AirPlay equivalent shows black)
 *  - Screen sharing (any share API gets black frames)
 *  - Recent apps thumbnail (shows black in app switcher)
 *  - Accessibility services reading screen content (partially — allowlisted services like TalkBack still work)
 *
 * This is set UNCONDITIONALLY on the Activity — there is no user toggle,
 * no debug override, no way to disable it. The ballot is never capturable.
 *
 * Combined with the 10-second rotating IPVC QR code, this means:
 *  1. You cannot screenshot the QR to send to a remote corrupt admin
 *  2. You cannot screen-record your ballot selections for a coercer
 *  3. You cannot cast your screen to prove how you voted to a vote buyer
 */
@dagger.hilt.android.AndroidEntryPoint()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0012\u0010\u0003\u001a\u00020\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006H\u0014J\b\u0010\u0007\u001a\u00020\u0004H\u0014J\b\u0010\b\u001a\u00020\u0004H\u0014\u00a8\u0006\t"}, d2 = {"Lcom/securevote/remote/MainActivity;", "Landroidx/activity/ComponentActivity;", "()V", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "onPause", "onResume", "app_debug"})
public final class MainActivity extends androidx.activity.ComponentActivity {
    
    public MainActivity() {
        super(0);
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    @java.lang.Override()
    protected void onPause() {
    }
    
    @java.lang.Override()
    protected void onResume() {
    }
}