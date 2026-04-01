package com.securevote.remote.util;

import android.app.Activity;
import android.view.WindowManager;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Security utilities for SecureVote Remote.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0012\n\u0002\u0010\u0019\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006J\u000e\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nJ\u0006\u0010\u000b\u001a\u00020\bJ\u0006\u0010\f\u001a\u00020\bJ\u0010\u0010\r\u001a\u00020\u00042\b\u0010\u000e\u001a\u0004\u0018\u00010\u000fJ\u0010\u0010\r\u001a\u00020\u00042\b\u0010\u000e\u001a\u0004\u0018\u00010\u0010J\u0010\u0010\u0011\u001a\u00020\u00042\b\u0010\u000e\u001a\u0004\u0018\u00010\u0012\u00a8\u0006\u0013"}, d2 = {"Lcom/securevote/remote/util/SecurityUtil;", "", "()V", "enforceScreenSecurity", "", "activity", "Landroid/app/Activity;", "hasUnknownAccessibilityService", "", "context", "Landroid/content/Context;", "isDevicePotentiallyRooted", "isEmulator", "secureWipe", "data", "", "", "secureWipeString", "", "app_debug"})
public final class SecurityUtil {
    @org.jetbrains.annotations.NotNull()
    public static final com.securevote.remote.util.SecurityUtil INSTANCE = null;
    
    private SecurityUtil() {
        super();
    }
    
    /**
     * Securely wipe a byte array by overwriting with random data, then zeros.
     * Prevents compiler/JIT from optimizing away the wipe.
     *
     * This mirrors the memwipe() utility in the Go application layer
     * (see APPLICATION.md — "Memory wiping" section).
     */
    public final void secureWipe(@org.jetbrains.annotations.Nullable()
    byte[] data) {
    }
    
    /**
     * Securely wipe a char array (for PIN data).
     */
    public final void secureWipe(@org.jetbrains.annotations.Nullable()
    char[] data) {
    }
    
    /**
     * Securely wipe a string by clearing its backing char array.
     * NOTE: Strings in Java/Kotlin are immutable and pooled — this is best-effort.
     * For truly sensitive data, use CharArray instead of String.
     */
    public final void secureWipeString(@org.jetbrains.annotations.Nullable()
    java.lang.String data) {
    }
    
    /**
     * Assert FLAG_SECURE on an activity's window.
     * Called in onCreate and onResume as defense-in-depth.
     */
    public final void enforceScreenSecurity(@org.jetbrains.annotations.NotNull()
    android.app.Activity activity) {
    }
    
    /**
     * Check if the device is likely rooted.
     * This is a heuristic check — not as reliable as Play Integrity,
     * but provides an additional signal.
     */
    public final boolean isDevicePotentiallyRooted() {
        return false;
    }
    
    /**
     * Check if the app is running in an emulator.
     */
    public final boolean isEmulator() {
        return false;
    }
    
    /**
     * Check if an accessibility service that could read screen content is active.
     * Allowlists known assistive technologies (TalkBack, etc.).
     */
    public final boolean hasUnknownAccessibilityService(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return false;
    }
}