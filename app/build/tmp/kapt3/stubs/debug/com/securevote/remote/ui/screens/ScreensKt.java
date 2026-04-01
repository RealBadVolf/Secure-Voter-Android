package com.securevote.remote.ui.screens;

import androidx.compose.foundation.*;
import androidx.compose.foundation.layout.*;
import androidx.compose.foundation.text.KeyboardOptions;
import androidx.compose.material.icons.Icons;
import androidx.compose.material.icons.filled.*;
import androidx.compose.material3.*;
import androidx.compose.runtime.*;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.input.ImeAction;
import androidx.compose.ui.text.input.KeyboardType;
import androidx.compose.ui.text.input.PasswordVisualTransformation;
import androidx.compose.ui.text.style.TextAlign;
import com.securevote.remote.BuildConfig;
import com.securevote.remote.ui.*;
import com.securevote.remote.ui.theme.*;
import kotlinx.serialization.json.*;
import java.net.HttpURLConnection;
import java.net.URL;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000V\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010$\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\t\n\u0002\b\u0015\n\u0002\u0018\u0002\n\u0000\u001ax\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00032\u0006\u0010\u0005\u001a\u00020\u00062\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b2\u0006\u0010\n\u001a\u00020\u00062\u0006\u0010\u000b\u001a\u00020\u00062\b\u0010\f\u001a\u0004\u0018\u00010\u00032\u0012\u0010\r\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00010\u000e2\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00010\u00102\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00010\u0010H\u0007\u001ah\u0010\u0012\u001a\u00020\u00012\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00140\b2\u0018\u0010\u0007\u001a\u0014\u0012\u0004\u0012\u00020\u0003\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\b0\u00152\u0014\u0010\u0016\u001a\u0010\u0012\u0004\u0012\u00020\u0003\u0012\u0006\u0012\u0004\u0018\u00010\u00030\u00152\f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00010\u00102\u0012\u0010\u0018\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00010\u000eH\u0007\u001a \u0010\u0019\u001a\u00020\u00012\b\b\u0002\u0010\u001a\u001a\u00020\u00032\f\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u00010\u0010H\u0007\u001an\u0010\u001c\u001a\u00020\u00012\u0014\u0010\u0016\u001a\u0010\u0012\u0004\u0012\u00020\u0003\u0012\u0006\u0012\u0004\u0018\u00010\u00030\u00152\u0006\u0010\u001d\u001a\u00020\u00032\u0006\u0010\u001e\u001a\u00020\u00032\u0006\u0010\u001f\u001a\u00020\u000326\u0010 \u001a2\u0012\u0013\u0012\u00110\u0003\u00a2\u0006\f\b\"\u0012\b\b#\u0012\u0004\b\b($\u0012\u0013\u0012\u00110\u0003\u00a2\u0006\f\b\"\u0012\b\b#\u0012\u0004\b\b(%\u0012\u0004\u0012\u00020\u00010!H\u0007\u001a.\u0010&\u001a\u00020\u00012\b\b\u0002\u0010\u001a\u001a\u00020\u00032\f\u0010\'\u001a\b\u0012\u0004\u0012\u00020\u00010\u00102\f\u0010(\u001a\b\u0012\u0004\u0012\u00020\u00010\u0010H\u0007\u001aS\u0010)\u001a\u00020\u00012\u0006\u0010*\u001a\u00020+2\u0006\u0010\u001d\u001a\u00020\u00032\u0006\u0010\u001a\u001a\u00020\u00032#\u0010,\u001a\u001f\u0012\u0015\u0012\u0013\u0018\u00010\u0003\u00a2\u0006\f\b\"\u0012\b\b#\u0012\u0004\b\b(-\u0012\u0004\u0012\u00020\u00010\u000e2\f\u0010.\u001a\b\u0012\u0004\u0012\u00020\u00010\u0010H\u0007\u001a$\u0010/\u001a\u00020\u00012\f\u00100\u001a\b\u0012\u0004\u0012\u00020\u00010\u00102\f\u00101\u001a\b\u0012\u0004\u0012\u00020\u00010\u0010H\u0007\u001a \u00102\u001a\u00020\u00012\u0006\u00103\u001a\u00020\u00062\u0006\u00104\u001a\u00020\u00062\u0006\u00105\u001a\u00020\u0003H\u0007\u001a&\u00106\u001a\u00020\u00012\u0006\u0010$\u001a\u00020\u00032\u0006\u0010%\u001a\u00020\u00032\f\u00107\u001a\b\u0012\u0004\u0012\u00020\u00010\u0010H\u0007\u001a\u0016\u00108\u001a\u00020\u00012\f\u00109\u001a\b\u0012\u0004\u0012\u00020\u00010\u0010H\u0007\u001a\u0016\u0010:\u001a\u00020\u00012\f\u0010;\u001a\b\u0012\u0004\u0012\u00020\u00010\u0010H\u0007\u001a\u0016\u0010<\u001a\u00020\u00012\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00010\u0010H\u0007\u001a\u0018\u0010=\u001a\u00020\u00012\u0006\u00105\u001a\u00020\u00032\u0006\u0010>\u001a\u00020\u0003H\u0003\u001a*\u0010?\u001a\u00020\u00012\u0012\u0010@\u001a\u000e\u0012\u0004\u0012\u00020A\u0012\u0004\u0012\u00020\u00010\u000e2\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00010\u0010H\u0007\u00a8\u0006B"}, d2 = {"BallotRaceScreen", "", "raceTitle", "", "votingRule", "maxSelections", "", "candidates", "", "Lcom/securevote/remote/ui/LiveCandidate;", "raceIndex", "totalRaces", "currentSelection", "onSelect", "Lkotlin/Function1;", "onNext", "Lkotlin/Function0;", "onBack", "BallotReviewScreen", "races", "Lcom/securevote/remote/ui/LiveRace;", "", "selections", "onConfirm", "onChangeRace", "BiometricScreen", "voterName", "onVerified", "EncryptionScreen", "electionId", "precinctId", "voterToken", "onComplete", "Lkotlin/Function2;", "Lkotlin/ParameterName;", "name", "voteRecordId", "confirmationHash", "NFCScanScreen", "onScanComplete", "onNoNFC", "PINEntryScreen", "voterId", "", "onPinVerified", "sessionToken", "onLocked", "SplashScreen", "onBeginVoting", "onVerify", "StepIndicator", "step", "total", "label", "SuccessScreen", "onDone", "ThankYouScreen", "onReturn", "TokenIssuanceScreen", "onTokenIssued", "VerificationScreen", "VoterDetailRow", "value", "VoterLookupScreen", "onVoterFound", "Lcom/securevote/remote/ui/VoterInfo;", "app_debug"})
public final class ScreensKt {
    
    @androidx.compose.runtime.Composable()
    public static final void StepIndicator(int step, int total, @org.jetbrains.annotations.NotNull()
    java.lang.String label) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void SplashScreen(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onBeginVoting, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onVerify) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void VoterLookupScreen(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.securevote.remote.ui.VoterInfo, kotlin.Unit> onVoterFound, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onBack) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void VoterDetailRow(java.lang.String label, java.lang.String value) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void NFCScanScreen(@org.jetbrains.annotations.NotNull()
    java.lang.String voterName, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onScanComplete, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onNoNFC) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void BiometricScreen(@org.jetbrains.annotations.NotNull()
    java.lang.String voterName, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onVerified) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void PINEntryScreen(long voterId, @org.jetbrains.annotations.NotNull()
    java.lang.String electionId, @org.jetbrains.annotations.NotNull()
    java.lang.String voterName, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onPinVerified, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onLocked) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void TokenIssuanceScreen(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onTokenIssued) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void BallotRaceScreen(@org.jetbrains.annotations.NotNull()
    java.lang.String raceTitle, @org.jetbrains.annotations.NotNull()
    java.lang.String votingRule, int maxSelections, @org.jetbrains.annotations.NotNull()
    java.util.List<com.securevote.remote.ui.LiveCandidate> candidates, int raceIndex, int totalRaces, @org.jetbrains.annotations.Nullable()
    java.lang.String currentSelection, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onSelect, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onNext, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onBack) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void BallotReviewScreen(@org.jetbrains.annotations.NotNull()
    java.util.List<com.securevote.remote.ui.LiveRace> races, @org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, ? extends java.util.List<com.securevote.remote.ui.LiveCandidate>> candidates, @org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, java.lang.String> selections, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onConfirm, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.Integer, kotlin.Unit> onChangeRace) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void EncryptionScreen(@org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, java.lang.String> selections, @org.jetbrains.annotations.NotNull()
    java.lang.String electionId, @org.jetbrains.annotations.NotNull()
    java.lang.String precinctId, @org.jetbrains.annotations.NotNull()
    java.lang.String voterToken, @org.jetbrains.annotations.NotNull()
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
    public static final void VerificationScreen(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onBack) {
    }
}