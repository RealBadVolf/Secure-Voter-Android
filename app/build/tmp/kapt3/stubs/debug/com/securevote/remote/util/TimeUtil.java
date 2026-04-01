package com.securevote.remote.util;

import android.app.Activity;
import android.view.WindowManager;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Timestamp utilities matching the SecureVote protocol.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0006\u0010\u0003\u001a\u00020\u0004J\u0006\u0010\u0005\u001a\u00020\u0006\u00a8\u0006\u0007"}, d2 = {"Lcom/securevote/remote/util/TimeUtil;", "", "()V", "nowEpochSeconds", "", "nowISO", "", "app_debug"})
public final class TimeUtil {
    @org.jetbrains.annotations.NotNull()
    public static final com.securevote.remote.util.TimeUtil INSTANCE = null;
    
    private TimeUtil() {
        super();
    }
    
    /**
     * Current UTC timestamp in ISO 8601 format.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String nowISO() {
        return null;
    }
    
    /**
     * Current Unix epoch seconds.
     */
    public final long nowEpochSeconds() {
        return 0L;
    }
}