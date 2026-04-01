package com.securevote.remote.ui.screens;

import androidx.compose.foundation.*;
import androidx.compose.foundation.layout.*;
import androidx.compose.material.icons.Icons;
import androidx.compose.material.icons.filled.*;
import androidx.compose.material3.*;
import androidx.compose.runtime.*;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.style.TextAlign;
import com.securevote.remote.ui.theme.*;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000(\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0005\u001a9\u0010\u0000\u001a\u00020\u00012!\u0010\u0002\u001a\u001d\u0012\u0013\u0012\u00110\u0004\u00a2\u0006\f\b\u0005\u0012\b\b\u0006\u0012\u0004\b\b(\u0007\u0012\u0004\u0012\u00020\u00010\u00032\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00010\tH\u0007\u001a\u001c\u0010\n\u001a\u00020\u00012\u0012\u0010\u000b\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00010\u0003H\u0007\u001a\u0018\u0010\f\u001a\u00020\u00012\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u000eH\u0007\u001a$\u0010\u0010\u001a\u00020\u00012\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00010\t2\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00010\tH\u0007\u00a8\u0006\u0013"}, d2 = {"DuressPinSetupScreen", "", "onPinSet", "Lkotlin/Function1;", "", "Lkotlin/ParameterName;", "name", "pin", "onSkipInfo", "Lkotlin/Function0;", "NumericKeypad", "onKey", "PinDots", "pinLength", "", "total", "RegistrationPhotosScreen", "onPhotosComplete", "onBack", "app_debug"})
public final class RegistrationScreensKt {
    
    /**
     * Duress PIN Setup — part of the registration flow.
     *
     * The voter creates a secondary PIN that, if entered during voting,
     * makes the system APPEAR to accept the ballot normally while
     * internally flagging it as cast under duress.
     *
     * DESIGN DECISION (from ABSENTEE.md):
     *  The duress PIN is set at registration time, NOT later.
     *  If it could be set at any time, a coercer could demand the voter
     *  set it in front of them to prove it's not already set.
     *  By setting it during registration (before any coercion scenario),
     *  the voter can truthfully say "I don't remember setting anything
     *  like that" if confronted.
     */
    @androidx.compose.runtime.Composable()
    public static final void DuressPinSetupScreen(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onPinSet, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onSkipInfo) {
    }
    
    /**
     * Registration Photos — captures the three binding photos:
     *  1. Selfie (with liveness check)
     *  2. Front of ID
     *  3. Voter holding ID next to face (the critical binding photo)
     *
     * From ABSENTEE.md:
     *  "A stolen ID and a stolen selfie cannot be combined to fake
     *  the holding photo, because the composite scene must be spatially
     *  coherent (correct lighting, perspective, hand position, face
     *  angle relative to card)."
     */
    @androidx.compose.runtime.Composable()
    public static final void RegistrationPhotosScreen(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onPhotosComplete, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onBack) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void PinDots(int pinLength, int total) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void NumericKeypad(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onKey) {
    }
}