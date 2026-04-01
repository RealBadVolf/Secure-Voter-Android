package com.securevote.remote.ui;

import androidx.compose.animation.*;
import androidx.compose.runtime.*;
import androidx.navigation.NavType;
import com.securevote.remote.navigation.SVRRoute;
import com.securevote.remote.ui.screens.*;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000\b\n\u0000\n\u0002\u0010\u0002\n\u0000\u001a\b\u0010\u0000\u001a\u00020\u0001H\u0007\u00a8\u0006\u0002"}, d2 = {"SVRApp", "", "app_debug"})
public final class SVRAppKt {
    
    /**
     * Root composable for the SecureVote Remote app.
     * Manages navigation between all screens in the voting flow.
     *
     * Flow:
     *  Splash → NFC Scan → Biometric → (IPVC QR fallback if bio fails)
     *  → PIN Entry → Token Issuance → Ballot (per race) → Review
     *  → Encryption → Success → Thank You
     */
    @androidx.compose.runtime.Composable()
    public static final void SVRApp() {
    }
}