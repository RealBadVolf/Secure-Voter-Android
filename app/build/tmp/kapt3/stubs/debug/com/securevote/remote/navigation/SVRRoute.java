package com.securevote.remote.navigation;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0010\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b6\u0018\u00002\u00020\u0001:\f\b\t\n\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013B\u0011\b\u0004\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007\u0082\u0001\f\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f\u00a8\u0006 "}, d2 = {"Lcom/securevote/remote/navigation/SVRRoute;", "", "route", "", "<init>", "(Ljava/lang/String;)V", "getRoute", "()Ljava/lang/String;", "Splash", "VoterLookup", "NFCScan", "Biometric", "PinEntry", "TokenIssuance", "Ballot", "BallotReview", "Encrypting", "Success", "ThankYou", "Verify", "Lcom/securevote/remote/navigation/SVRRoute$Ballot;", "Lcom/securevote/remote/navigation/SVRRoute$BallotReview;", "Lcom/securevote/remote/navigation/SVRRoute$Biometric;", "Lcom/securevote/remote/navigation/SVRRoute$Encrypting;", "Lcom/securevote/remote/navigation/SVRRoute$NFCScan;", "Lcom/securevote/remote/navigation/SVRRoute$PinEntry;", "Lcom/securevote/remote/navigation/SVRRoute$Splash;", "Lcom/securevote/remote/navigation/SVRRoute$Success;", "Lcom/securevote/remote/navigation/SVRRoute$ThankYou;", "Lcom/securevote/remote/navigation/SVRRoute$TokenIssuance;", "Lcom/securevote/remote/navigation/SVRRoute$Verify;", "Lcom/securevote/remote/navigation/SVRRoute$VoterLookup;", "app_debug"})
public abstract class SVRRoute {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String route = null;
    
    private SVRRoute(java.lang.String route) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getRoute() {
        return null;
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007\u00a8\u0006\b"}, d2 = {"Lcom/securevote/remote/navigation/SVRRoute$Ballot;", "Lcom/securevote/remote/navigation/SVRRoute;", "<init>", "()V", "createRoute", "", "index", "", "app_debug"})
    public static final class Ballot extends com.securevote.remote.navigation.SVRRoute {
        @org.jetbrains.annotations.NotNull()
        public static final com.securevote.remote.navigation.SVRRoute.Ballot INSTANCE = null;
        
        private Ballot() {
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String createRoute(int index) {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003\u00a8\u0006\u0004"}, d2 = {"Lcom/securevote/remote/navigation/SVRRoute$BallotReview;", "Lcom/securevote/remote/navigation/SVRRoute;", "<init>", "()V", "app_debug"})
    public static final class BallotReview extends com.securevote.remote.navigation.SVRRoute {
        @org.jetbrains.annotations.NotNull()
        public static final com.securevote.remote.navigation.SVRRoute.BallotReview INSTANCE = null;
        
        private BallotReview() {
        }
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003\u00a8\u0006\u0004"}, d2 = {"Lcom/securevote/remote/navigation/SVRRoute$Biometric;", "Lcom/securevote/remote/navigation/SVRRoute;", "<init>", "()V", "app_debug"})
    public static final class Biometric extends com.securevote.remote.navigation.SVRRoute {
        @org.jetbrains.annotations.NotNull()
        public static final com.securevote.remote.navigation.SVRRoute.Biometric INSTANCE = null;
        
        private Biometric() {
        }
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003\u00a8\u0006\u0004"}, d2 = {"Lcom/securevote/remote/navigation/SVRRoute$Encrypting;", "Lcom/securevote/remote/navigation/SVRRoute;", "<init>", "()V", "app_debug"})
    public static final class Encrypting extends com.securevote.remote.navigation.SVRRoute {
        @org.jetbrains.annotations.NotNull()
        public static final com.securevote.remote.navigation.SVRRoute.Encrypting INSTANCE = null;
        
        private Encrypting() {
        }
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003\u00a8\u0006\u0004"}, d2 = {"Lcom/securevote/remote/navigation/SVRRoute$NFCScan;", "Lcom/securevote/remote/navigation/SVRRoute;", "<init>", "()V", "app_debug"})
    public static final class NFCScan extends com.securevote.remote.navigation.SVRRoute {
        @org.jetbrains.annotations.NotNull()
        public static final com.securevote.remote.navigation.SVRRoute.NFCScan INSTANCE = null;
        
        private NFCScan() {
        }
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003\u00a8\u0006\u0004"}, d2 = {"Lcom/securevote/remote/navigation/SVRRoute$PinEntry;", "Lcom/securevote/remote/navigation/SVRRoute;", "<init>", "()V", "app_debug"})
    public static final class PinEntry extends com.securevote.remote.navigation.SVRRoute {
        @org.jetbrains.annotations.NotNull()
        public static final com.securevote.remote.navigation.SVRRoute.PinEntry INSTANCE = null;
        
        private PinEntry() {
        }
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003\u00a8\u0006\u0004"}, d2 = {"Lcom/securevote/remote/navigation/SVRRoute$Splash;", "Lcom/securevote/remote/navigation/SVRRoute;", "<init>", "()V", "app_debug"})
    public static final class Splash extends com.securevote.remote.navigation.SVRRoute {
        @org.jetbrains.annotations.NotNull()
        public static final com.securevote.remote.navigation.SVRRoute.Splash INSTANCE = null;
        
        private Splash() {
        }
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003\u00a8\u0006\u0004"}, d2 = {"Lcom/securevote/remote/navigation/SVRRoute$Success;", "Lcom/securevote/remote/navigation/SVRRoute;", "<init>", "()V", "app_debug"})
    public static final class Success extends com.securevote.remote.navigation.SVRRoute {
        @org.jetbrains.annotations.NotNull()
        public static final com.securevote.remote.navigation.SVRRoute.Success INSTANCE = null;
        
        private Success() {
        }
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003\u00a8\u0006\u0004"}, d2 = {"Lcom/securevote/remote/navigation/SVRRoute$ThankYou;", "Lcom/securevote/remote/navigation/SVRRoute;", "<init>", "()V", "app_debug"})
    public static final class ThankYou extends com.securevote.remote.navigation.SVRRoute {
        @org.jetbrains.annotations.NotNull()
        public static final com.securevote.remote.navigation.SVRRoute.ThankYou INSTANCE = null;
        
        private ThankYou() {
        }
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003\u00a8\u0006\u0004"}, d2 = {"Lcom/securevote/remote/navigation/SVRRoute$TokenIssuance;", "Lcom/securevote/remote/navigation/SVRRoute;", "<init>", "()V", "app_debug"})
    public static final class TokenIssuance extends com.securevote.remote.navigation.SVRRoute {
        @org.jetbrains.annotations.NotNull()
        public static final com.securevote.remote.navigation.SVRRoute.TokenIssuance INSTANCE = null;
        
        private TokenIssuance() {
        }
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003\u00a8\u0006\u0004"}, d2 = {"Lcom/securevote/remote/navigation/SVRRoute$Verify;", "Lcom/securevote/remote/navigation/SVRRoute;", "<init>", "()V", "app_debug"})
    public static final class Verify extends com.securevote.remote.navigation.SVRRoute {
        @org.jetbrains.annotations.NotNull()
        public static final com.securevote.remote.navigation.SVRRoute.Verify INSTANCE = null;
        
        private Verify() {
        }
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003\u00a8\u0006\u0004"}, d2 = {"Lcom/securevote/remote/navigation/SVRRoute$VoterLookup;", "Lcom/securevote/remote/navigation/SVRRoute;", "<init>", "()V", "app_debug"})
    public static final class VoterLookup extends com.securevote.remote.navigation.SVRRoute {
        @org.jetbrains.annotations.NotNull()
        public static final com.securevote.remote.navigation.SVRRoute.VoterLookup INSTANCE = null;
        
        private VoterLookup() {
        }
    }
}