package com.securevote.remote.ui.screens;

import androidx.compose.animation.core.*;
import androidx.compose.foundation.*;
import androidx.compose.foundation.layout.*;
import androidx.compose.material.icons.Icons;
import androidx.compose.material.icons.filled.*;
import androidx.compose.material3.*;
import androidx.compose.runtime.*;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.Brush;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.style.TextAlign;
import com.securevote.remote.crypto.SVRCrypto;
import com.securevote.remote.ui.theme.*;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000@\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010$\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\u000b\n\u0002\b\u000f\u001aR\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00032\b\u0010\u0005\u001a\u0004\u0018\u00010\u00062\u0012\u0010\u0007\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00010\b2\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00010\n2\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00010\nH\u0007\u001a@\u0010\f\u001a\u00020\u00012\u0014\u0010\r\u001a\u0010\u0012\u0004\u0012\u00020\u0006\u0012\u0006\u0012\u0004\u0018\u00010\u00060\u000e2\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00010\n2\u0012\u0010\u0010\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00010\bH\u0007\u001a2\u0010\u0011\u001a\u00020\u00012\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00010\n2\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00010\n2\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00010\nH\u0007\u001a@\u0010\u0015\u001a\u00020\u000126\u0010\u0016\u001a2\u0012\u0013\u0012\u00110\u0006\u00a2\u0006\f\b\u0018\u0012\b\b\u0019\u0012\u0004\b\b(\u001a\u0012\u0013\u0012\u00110\u0006\u00a2\u0006\f\b\u0018\u0012\b\b\u0019\u0012\u0004\b\b(\u001b\u0012\u0004\u0012\u00020\u00010\u0017H\u0007\u001a$\u0010\u001c\u001a\u00020\u00012\f\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u00010\n2\f\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u00010\nH\u0007\u001a+\u0010\u001f\u001a\u00020\u00012!\u0010 \u001a\u001d\u0012\u0013\u0012\u00110!\u00a2\u0006\f\b\u0018\u0012\b\b\u0019\u0012\u0004\b\b(\"\u0012\u0004\u0012\u00020\u00010\bH\u0007\u001a&\u0010#\u001a\u00020\u00012\f\u0010$\u001a\b\u0012\u0004\u0012\u00020\u00010\n2\u000e\b\u0002\u0010%\u001a\b\u0012\u0004\u0012\u00020\u00010\nH\u0007\u001a \u0010&\u001a\u00020\u00012\u0006\u0010\'\u001a\u00020\u00032\u0006\u0010(\u001a\u00020\u00032\u0006\u0010)\u001a\u00020\u0006H\u0007\u001a&\u0010*\u001a\u00020\u00012\u0006\u0010\u001a\u001a\u00020\u00062\u0006\u0010\u001b\u001a\u00020\u00062\f\u0010+\u001a\b\u0012\u0004\u0012\u00020\u00010\nH\u0007\u001a\u0016\u0010,\u001a\u00020\u00012\f\u0010-\u001a\b\u0012\u0004\u0012\u00020\u00010\nH\u0007\u001a\u0016\u0010.\u001a\u00020\u00012\f\u0010/\u001a\b\u0012\u0004\u0012\u00020\u00010\nH\u0007\u00a8\u00060"}, d2 = {"BallotRaceScreen", "", "raceIndex", "", "totalRaces", "currentSelection", "", "onSelect", "Lkotlin/Function1;", "onNext", "Lkotlin/Function0;", "onBack", "BallotReviewScreen", "selections", "", "onConfirm", "onChangeRace", "BiometricScreen", "onVerified", "onUncertain", "onRejected", "EncryptionScreen", "onComplete", "Lkotlin/Function2;", "Lkotlin/ParameterName;", "name", "voteRecordId", "confirmationHash", "NFCScanScreen", "onScanComplete", "onNoNFC", "PINEntryScreen", "onPinVerified", "", "isDuress", "SplashScreen", "onBeginVoting", "onVerify", "StepIndicator", "step", "total", "label", "SuccessScreen", "onDone", "ThankYouScreen", "onReturn", "TokenIssuanceScreen", "onTokenIssued", "app_debug"})
public final class ScreensKt {
    
    @androidx.compose.runtime.Composable()
    public static final void SplashScreen(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onBeginVoting, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onVerify) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void NFCScanScreen(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onScanComplete, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onNoNFC) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void BiometricScreen(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onVerified, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onUncertain, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onRejected) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void PINEntryScreen(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.Boolean, kotlin.Unit> onPinVerified) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void TokenIssuanceScreen(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onTokenIssued) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void BallotRaceScreen(int raceIndex, int totalRaces, @org.jetbrains.annotations.Nullable()
    java.lang.String currentSelection, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onSelect, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onNext, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onBack) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void BallotReviewScreen(@org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, java.lang.String> selections, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onConfirm, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.Integer, kotlin.Unit> onChangeRace) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void EncryptionScreen(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function2<? super java.lang.String, ? super java.lang.String, kotlin.Unit> onComplete) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void SuccessScreen(@org.jetbrains.annotations.NotNull()
    java.lang.String voteRecordId, @org.jetbrains.annotations.NotNull()
    java.lang.String confirmationHash, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDone) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void ThankYouScreen(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onReturn) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void StepIndicator(int step, int total, @org.jetbrains.annotations.NotNull()
    java.lang.String label) {
    }
}