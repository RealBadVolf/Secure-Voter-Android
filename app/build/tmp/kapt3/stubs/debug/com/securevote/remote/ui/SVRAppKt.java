package com.securevote.remote.ui;

import androidx.compose.animation.*;
import androidx.compose.runtime.*;
import androidx.navigation.NavType;
import com.securevote.remote.BuildConfig;
import com.securevote.remote.navigation.SVRRoute;
import com.securevote.remote.ui.screens.*;
import kotlinx.coroutines.Dispatchers;
import kotlinx.serialization.json.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000:\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010$\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0003\u001a\b\u0010\u0002\u001a\u00020\u0003H\u0007\u001a\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u0005H\u0086@\u00a2\u0006\u0002\u0010\u0006\u001a\u0018\u0010\u0007\u001a\u0004\u0018\u00010\b2\u0006\u0010\t\u001a\u00020\u0001H\u0086@\u00a2\u0006\u0002\u0010\n\u001aP\u0010\u000b\u001a\u0016\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020\u0001\u0018\u00010\f2\u0014\u0010\r\u001a\u0010\u0012\u0004\u0012\u00020\u0001\u0012\u0006\u0012\u0004\u0018\u00010\u00010\u000e2\u0006\u0010\u000f\u001a\u00020\u00012\u0006\u0010\u0010\u001a\u00020\u00012\u0006\u0010\u0011\u001a\u00020\u0001H\u0086@\u00a2\u0006\u0002\u0010\u0012\u001a&\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u000f\u001a\u00020\u00012\u0006\u0010\u0017\u001a\u00020\u0001H\u0086@\u00a2\u0006\u0002\u0010\u0018\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082D\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0019"}, d2 = {"BASE", "", "SVRApp", "", "fetchBDF", "Lcom/securevote/remote/ui/LiveBDF;", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "lookupVoter", "Lcom/securevote/remote/ui/VoterInfo;", "regNumber", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "submitVote", "Lkotlin/Triple;", "selections", "", "electionId", "precinctId", "voterToken", "(Ljava/util/Map;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "verifyPIN", "Lcom/securevote/remote/ui/PINResult;", "voterId", "", "pin", "(JLjava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public final class SVRAppKt {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String BASE = "https://api.badvolf.com/api/v1";
    
    @org.jetbrains.annotations.Nullable()
    public static final java.lang.Object fetchBDF(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.securevote.remote.ui.LiveBDF> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public static final java.lang.Object lookupVoter(@org.jetbrains.annotations.NotNull()
    java.lang.String regNumber, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.securevote.remote.ui.VoterInfo> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public static final java.lang.Object verifyPIN(long voterId, @org.jetbrains.annotations.NotNull()
    java.lang.String electionId, @org.jetbrains.annotations.NotNull()
    java.lang.String pin, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.securevote.remote.ui.PINResult> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public static final java.lang.Object submitVote(@org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, java.lang.String> selections, @org.jetbrains.annotations.NotNull()
    java.lang.String electionId, @org.jetbrains.annotations.NotNull()
    java.lang.String precinctId, @org.jetbrains.annotations.NotNull()
    java.lang.String voterToken, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Triple<java.lang.String, java.lang.String, java.lang.String>> $completion) {
        return null;
    }
    
    @androidx.compose.runtime.Composable()
    public static final void SVRApp() {
    }
}