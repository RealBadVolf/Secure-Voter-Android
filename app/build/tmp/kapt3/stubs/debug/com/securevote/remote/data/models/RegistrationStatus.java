package com.securevote.remote.data.models;

import kotlinx.serialization.Serializable;

@kotlinx.serialization.Serializable()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0007\b\u0087\u0081\u0002\u0018\u0000 \u00072\b\u0012\u0004\u0012\u00020\u00000\u0001:\u0001\u0007B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006\u00a8\u0006\b"}, d2 = {"Lcom/securevote/remote/data/models/RegistrationStatus;", "", "(Ljava/lang/String;I)V", "ACTIVE", "REVOKED", "PIN_LOCKED", "COOLING", "Companion", "app_debug"})
public enum RegistrationStatus {
    /*public static final*/ ACTIVE /* = new ACTIVE() */,
    /*public static final*/ REVOKED /* = new REVOKED() */,
    /*public static final*/ PIN_LOCKED /* = new PIN_LOCKED() */,
    /*public static final*/ COOLING /* = new COOLING() */;
    @org.jetbrains.annotations.NotNull()
    public static final com.securevote.remote.data.models.RegistrationStatus.Companion Companion = null;
    
    RegistrationStatus() {
    }
    
    @org.jetbrains.annotations.NotNull()
    public static kotlin.enums.EnumEntries<com.securevote.remote.data.models.RegistrationStatus> getEntries() {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000f\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004H\u00c6\u0001\u00a8\u0006\u0006"}, d2 = {"Lcom/securevote/remote/data/models/RegistrationStatus$Companion;", "", "()V", "serializer", "Lkotlinx/serialization/KSerializer;", "Lcom/securevote/remote/data/models/RegistrationStatus;", "app_debug"})
    public static final class Companion {
        
        @org.jetbrains.annotations.NotNull()
        public final kotlinx.serialization.KSerializer<com.securevote.remote.data.models.RegistrationStatus> serializer() {
            return null;
        }
        
        private Companion() {
            super();
        }
    }
}