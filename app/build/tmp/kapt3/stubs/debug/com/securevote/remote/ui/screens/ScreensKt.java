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

@kotlin.Metadata(mv = {2, 2, 0}, k = 2, xi = 48, d1 = {"\u0000`\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\u0010$\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u000b\u001a \u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00032\u0006\u0010\u0005\u001a\u00020\u0006H\u0007\u001a$\u0010\u0007\u001a\u00020\u00012\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00010\t2\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00010\tH\u0007\u001a*\u0010\u000b\u001a\u00020\u00012\u0012\u0010\f\u001a\u000e\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020\u00010\r2\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00010\tH\u0007\u001a\u0018\u0010\u0010\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0011\u001a\u00020\u0006H\u0003\u001a.\u0010\u0012\u001a\u00020\u00012\b\b\u0002\u0010\u0013\u001a\u00020\u00062\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00010\t2\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00010\tH\u0007\u001a \u0010\u0016\u001a\u00020\u00012\b\b\u0002\u0010\u0013\u001a\u00020\u00062\f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00010\tH\u0007\u001aB\u0010\u0018\u001a\u00020\u00012\u0006\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u00062\u0006\u0010\u0013\u001a\u00020\u00062\u0012\u0010\u001c\u001a\u000e\u0012\u0004\u0012\u00020\u001d\u0012\u0004\u0012\u00020\u00010\r2\f\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u00010\tH\u0007\u001a\u0016\u0010\u001f\u001a\u00020\u00012\f\u0010 \u001a\b\u0012\u0004\u0012\u00020\u00010\tH\u0007\u001ax\u0010!\u001a\u00020\u00012\u0006\u0010\"\u001a\u00020\u00062\u0006\u0010#\u001a\u00020\u00062\u0006\u0010$\u001a\u00020\u00032\f\u0010%\u001a\b\u0012\u0004\u0012\u00020\'0&2\u0006\u0010(\u001a\u00020\u00032\u0006\u0010)\u001a\u00020\u00032\b\u0010*\u001a\u0004\u0018\u00010\u00062\u0012\u0010+\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00010\r2\f\u0010,\u001a\b\u0012\u0004\u0012\u00020\u00010\t2\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00010\tH\u0007\u001ah\u0010-\u001a\u00020\u00012\f\u0010.\u001a\b\u0012\u0004\u0012\u00020/0&2\u0018\u0010%\u001a\u0014\u0012\u0004\u0012\u00020\u0006\u0012\n\u0012\b\u0012\u0004\u0012\u00020\'0&002\u0014\u00101\u001a\u0010\u0012\u0004\u0012\u00020\u0006\u0012\u0006\u0012\u0004\u0018\u00010\u0006002\f\u00102\u001a\b\u0012\u0004\u0012\u00020\u00010\t2\u0012\u00103\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00010\rH\u0007\u001an\u00104\u001a\u00020\u00012\u0014\u00101\u001a\u0010\u0012\u0004\u0012\u00020\u0006\u0012\u0006\u0012\u0004\u0018\u00010\u0006002\u0006\u0010\u001b\u001a\u00020\u00062\u0006\u00105\u001a\u00020\u00062\u0006\u00106\u001a\u00020\u000626\u00107\u001a2\u0012\u0013\u0012\u00110\u0006\u00a2\u0006\f\b9\u0012\b\b:\u0012\u0004\b\b(;\u0012\u0013\u0012\u00110\u0006\u00a2\u0006\f\b9\u0012\b\b:\u0012\u0004\b\b(<\u0012\u0004\u0012\u00020\u000108H\u0007\u001a:\u0010=\u001a\u00020\u00012\u0006\u0010;\u001a\u00020\u00062\u0006\u0010<\u001a\u00020\u00062\b\b\u0002\u0010>\u001a\u00020\u00032\b\b\u0002\u0010?\u001a\u00020\u00032\f\u0010@\u001a\b\u0012\u0004\u0012\u00020\u00010\tH\u0007\u001a\u0016\u0010A\u001a\u00020\u00012\f\u0010B\u001a\b\u0012\u0004\u0012\u00020\u00010\tH\u0007\u001a\u0016\u0010C\u001a\u00020\u00012\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00010\tH\u0007\u00a8\u0006D"}, d2 = {"StepIndicator", "", "step", "", "total", "label", "", "SplashScreen", "onBeginVoting", "Lkotlin/Function0;", "onVerify", "VoterLookupScreen", "onVoterFound", "Lkotlin/Function1;", "Lcom/securevote/remote/ui/VoterInfo;", "onBack", "VoterDetailRow", "value", "NFCScanScreen", "voterName", "onScanComplete", "onNoNFC", "BiometricScreen", "onVerified", "PINEntryScreen", "voterId", "", "electionId", "onPinVerified", "Lcom/securevote/remote/ui/PINResult;", "onLocked", "TokenIssuanceScreen", "onTokenIssued", "BallotRaceScreen", "raceTitle", "votingRule", "maxSelections", "candidates", "", "Lcom/securevote/remote/ui/LiveCandidate;", "raceIndex", "totalRaces", "currentSelection", "onSelect", "onNext", "BallotReviewScreen", "races", "Lcom/securevote/remote/ui/LiveRace;", "", "selections", "onConfirm", "onChangeRace", "EncryptionScreen", "precinctId", "voterToken", "onComplete", "Lkotlin/Function2;", "Lkotlin/ParameterName;", "name", "voteRecordId", "confirmationHash", "SuccessScreen", "submissionSequence", "remainingSubmissions", "onDone", "ThankYouScreen", "onReturn", "VerificationScreen", "app_debug"})
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
    kotlin.jvm.functions.Function1<? super com.securevote.remote.ui.PINResult, kotlin.Unit> onPinVerified, @org.jetbrains.annotations.NotNull()
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
    java.lang.String confirmationHash, int submissionSequence, int remainingSubmissions, @org.jetbrains.annotations.NotNull()
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