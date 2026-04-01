package com.securevote.remote.ui.screens;

import android.graphics.Bitmap;
import androidx.compose.animation.core.*;
import androidx.compose.foundation.layout.*;
import androidx.compose.material3.*;
import androidx.compose.runtime.*;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.style.TextAlign;
import com.securevote.remote.crypto.IPVCQRGenerator;
import com.securevote.remote.ui.theme.*;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000\u001e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0012\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a<\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00032\u0006\u0010\u0005\u001a\u00020\u00062\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00010\b2\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00010\bH\u0007\u00a8\u0006\n"}, d2 = {"IPVCQRCodeScreen", "", "voterDocumentHash", "", "electionId", "deviceKeyMaterial", "", "onVerificationComplete", "Lkotlin/Function0;", "onCancel", "app_debug"})
public final class IPVCQRCodeScreenKt {
    
    /**
     * IPVC QR Code Verification Screen.
     *
     * Displayed when biometric verification fails on-device.
     * Shows a rotating QR code that an IPVC admin must scan while
     * physically present with the voter.
     *
     * SECURITY:
     *  - QR regenerates every 10 seconds with fresh nonce + HMAC
     *  - Countdown timer shows remaining validity
     *  - FLAG_SECURE prevents screenshots (set on Activity window)
     *  - Admin must be physically present to scan before expiry
     *  - Even a photo of the QR expires in ≤10 seconds
     */
    @androidx.compose.runtime.Composable()
    public static final void IPVCQRCodeScreen(@org.jetbrains.annotations.NotNull()
    java.lang.String voterDocumentHash, @org.jetbrains.annotations.NotNull()
    java.lang.String electionId, @org.jetbrains.annotations.NotNull()
    byte[] deviceKeyMaterial, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onVerificationComplete, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onCancel) {
    }
}