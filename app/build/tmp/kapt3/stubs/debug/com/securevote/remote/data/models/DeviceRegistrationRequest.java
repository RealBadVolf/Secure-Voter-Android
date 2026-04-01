package com.securevote.remote.data.models;

import kotlinx.serialization.Serializable;

/**
 * Device registration request sent to sv-remote-auth.
 */
@kotlinx.serialization.Serializable()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u001e\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0087\b\u0018\u0000 92\u00020\u0001:\u000289Bq\b\u0011\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u0012\b\u0010\b\u001a\u0004\u0018\u00010\u0005\u0012\b\u0010\t\u001a\u0004\u0018\u00010\u0005\u0012\u0006\u0010\n\u001a\u00020\u000b\u0012\b\u0010\f\u001a\u0004\u0018\u00010\u0005\u0012\b\u0010\r\u001a\u0004\u0018\u00010\u000e\u0012\b\u0010\u000f\u001a\u0004\u0018\u00010\u0005\u0012\b\u0010\u0010\u001a\u0004\u0018\u00010\u0005\u0012\b\u0010\u0011\u001a\u0004\u0018\u00010\u0012\u00a2\u0006\u0002\u0010\u0013BM\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\u0005\u0012\u0006\u0010\t\u001a\u00020\u0005\u0012\u0006\u0010\n\u001a\u00020\u000b\u0012\u0006\u0010\f\u001a\u00020\u0005\u0012\u0006\u0010\r\u001a\u00020\u000e\u0012\u0006\u0010\u000f\u001a\u00020\u0005\u0012\u0006\u0010\u0010\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0014J\t\u0010\"\u001a\u00020\u0005H\u00c6\u0003J\t\u0010#\u001a\u00020\u0007H\u00c6\u0003J\t\u0010$\u001a\u00020\u0005H\u00c6\u0003J\t\u0010%\u001a\u00020\u0005H\u00c6\u0003J\t\u0010&\u001a\u00020\u000bH\u00c6\u0003J\t\u0010\'\u001a\u00020\u0005H\u00c6\u0003J\t\u0010(\u001a\u00020\u000eH\u00c6\u0003J\t\u0010)\u001a\u00020\u0005H\u00c6\u0003J\t\u0010*\u001a\u00020\u0005H\u00c6\u0003Jc\u0010+\u001a\u00020\u00002\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u00052\b\b\u0002\u0010\t\u001a\u00020\u00052\b\b\u0002\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\f\u001a\u00020\u00052\b\b\u0002\u0010\r\u001a\u00020\u000e2\b\b\u0002\u0010\u000f\u001a\u00020\u00052\b\b\u0002\u0010\u0010\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010,\u001a\u00020\u000b2\b\u0010-\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010.\u001a\u00020\u0003H\u00d6\u0001J\t\u0010/\u001a\u00020\u0005H\u00d6\u0001J&\u00100\u001a\u0002012\u0006\u00102\u001a\u00020\u00002\u0006\u00103\u001a\u0002042\u0006\u00105\u001a\u000206H\u00c1\u0001\u00a2\u0006\u0002\b7R\u0011\u0010\b\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u0011\u0010\f\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0016R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0016R\u0011\u0010\u000f\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0016R\u0011\u0010\u0010\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u0016R\u0011\u0010\t\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u0016R\u0011\u0010\n\u001a\u00020\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001fR\u0011\u0010\r\u001a\u00020\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010!\u00a8\u0006:"}, d2 = {"Lcom/securevote/remote/data/models/DeviceRegistrationRequest;", "", "seen1", "", "devicePublicKeyHash", "", "attestationType", "Lcom/securevote/remote/data/models/AttestationType;", "attestationPayload", "nfcChipData", "nfcVerification", "", "biometricHash", "registrationPhotos", "Lcom/securevote/remote/data/models/RegistrationPhotos;", "duressPinHash", "electionId", "serializationConstructorMarker", "Lkotlinx/serialization/internal/SerializationConstructorMarker;", "(ILjava/lang/String;Lcom/securevote/remote/data/models/AttestationType;Ljava/lang/String;Ljava/lang/String;ZLjava/lang/String;Lcom/securevote/remote/data/models/RegistrationPhotos;Ljava/lang/String;Ljava/lang/String;Lkotlinx/serialization/internal/SerializationConstructorMarker;)V", "(Ljava/lang/String;Lcom/securevote/remote/data/models/AttestationType;Ljava/lang/String;Ljava/lang/String;ZLjava/lang/String;Lcom/securevote/remote/data/models/RegistrationPhotos;Ljava/lang/String;Ljava/lang/String;)V", "getAttestationPayload", "()Ljava/lang/String;", "getAttestationType", "()Lcom/securevote/remote/data/models/AttestationType;", "getBiometricHash", "getDevicePublicKeyHash", "getDuressPinHash", "getElectionId", "getNfcChipData", "getNfcVerification", "()Z", "getRegistrationPhotos", "()Lcom/securevote/remote/data/models/RegistrationPhotos;", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "other", "hashCode", "toString", "write$Self", "", "self", "output", "Lkotlinx/serialization/encoding/CompositeEncoder;", "serialDesc", "Lkotlinx/serialization/descriptors/SerialDescriptor;", "write$Self$app_debug", "$serializer", "Companion", "app_debug"})
public final class DeviceRegistrationRequest {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String devicePublicKeyHash = null;
    @org.jetbrains.annotations.NotNull()
    private final com.securevote.remote.data.models.AttestationType attestationType = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String attestationPayload = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String nfcChipData = null;
    private final boolean nfcVerification = false;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String biometricHash = null;
    @org.jetbrains.annotations.NotNull()
    private final com.securevote.remote.data.models.RegistrationPhotos registrationPhotos = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String duressPinHash = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String electionId = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.securevote.remote.data.models.DeviceRegistrationRequest.Companion Companion = null;
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.securevote.remote.data.models.AttestationType component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component4() {
        return null;
    }
    
    public final boolean component5() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component6() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.securevote.remote.data.models.RegistrationPhotos component7() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component8() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component9() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.securevote.remote.data.models.DeviceRegistrationRequest copy(@org.jetbrains.annotations.NotNull()
    java.lang.String devicePublicKeyHash, @org.jetbrains.annotations.NotNull()
    com.securevote.remote.data.models.AttestationType attestationType, @org.jetbrains.annotations.NotNull()
    java.lang.String attestationPayload, @org.jetbrains.annotations.NotNull()
    java.lang.String nfcChipData, boolean nfcVerification, @org.jetbrains.annotations.NotNull()
    java.lang.String biometricHash, @org.jetbrains.annotations.NotNull()
    com.securevote.remote.data.models.RegistrationPhotos registrationPhotos, @org.jetbrains.annotations.NotNull()
    java.lang.String duressPinHash, @org.jetbrains.annotations.NotNull()
    java.lang.String electionId) {
        return null;
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
    
    @kotlin.jvm.JvmStatic()
    public static final void write$Self$app_debug(@org.jetbrains.annotations.NotNull()
    com.securevote.remote.data.models.DeviceRegistrationRequest self, @org.jetbrains.annotations.NotNull()
    kotlinx.serialization.encoding.CompositeEncoder output, @org.jetbrains.annotations.NotNull()
    kotlinx.serialization.descriptors.SerialDescriptor serialDesc) {
    }
    
    public DeviceRegistrationRequest(@org.jetbrains.annotations.NotNull()
    java.lang.String devicePublicKeyHash, @org.jetbrains.annotations.NotNull()
    com.securevote.remote.data.models.AttestationType attestationType, @org.jetbrains.annotations.NotNull()
    java.lang.String attestationPayload, @org.jetbrains.annotations.NotNull()
    java.lang.String nfcChipData, boolean nfcVerification, @org.jetbrains.annotations.NotNull()
    java.lang.String biometricHash, @org.jetbrains.annotations.NotNull()
    com.securevote.remote.data.models.RegistrationPhotos registrationPhotos, @org.jetbrains.annotations.NotNull()
    java.lang.String duressPinHash, @org.jetbrains.annotations.NotNull()
    java.lang.String electionId) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getDevicePublicKeyHash() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.securevote.remote.data.models.AttestationType getAttestationType() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getAttestationPayload() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getNfcChipData() {
        return null;
    }
    
    public final boolean getNfcVerification() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getBiometricHash() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.securevote.remote.data.models.RegistrationPhotos getRegistrationPhotos() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getDuressPinHash() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getElectionId() {
        return null;
    }
    
    /**
     * Device registration request sent to sv-remote-auth.
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c7\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0003J\u0018\u0010\b\u001a\f\u0012\b\u0012\u0006\u0012\u0002\b\u00030\n0\tH\u00d6\u0001\u00a2\u0006\u0002\u0010\u000bJ\u0011\u0010\f\u001a\u00020\u00022\u0006\u0010\r\u001a\u00020\u000eH\u00d6\u0001J\u0019\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0002H\u00d6\u0001R\u0014\u0010\u0004\u001a\u00020\u00058VX\u00d6\u0005\u00a2\u0006\u0006\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\u0014"}, d2 = {"com/securevote/remote/data/models/DeviceRegistrationRequest.$serializer", "Lkotlinx/serialization/internal/GeneratedSerializer;", "Lcom/securevote/remote/data/models/DeviceRegistrationRequest;", "()V", "descriptor", "Lkotlinx/serialization/descriptors/SerialDescriptor;", "getDescriptor", "()Lkotlinx/serialization/descriptors/SerialDescriptor;", "childSerializers", "", "Lkotlinx/serialization/KSerializer;", "()[Lkotlinx/serialization/KSerializer;", "deserialize", "decoder", "Lkotlinx/serialization/encoding/Decoder;", "serialize", "", "encoder", "Lkotlinx/serialization/encoding/Encoder;", "value", "app_debug"})
    @java.lang.Deprecated()
    public static final class $serializer implements kotlinx.serialization.internal.GeneratedSerializer<com.securevote.remote.data.models.DeviceRegistrationRequest> {
        @org.jetbrains.annotations.NotNull()
        public static final com.securevote.remote.data.models.DeviceRegistrationRequest.$serializer INSTANCE = null;
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public kotlinx.serialization.KSerializer<?>[] childSerializers() {
            return null;
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public com.securevote.remote.data.models.DeviceRegistrationRequest deserialize(@org.jetbrains.annotations.NotNull()
        kotlinx.serialization.encoding.Decoder decoder) {
            return null;
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public kotlinx.serialization.descriptors.SerialDescriptor getDescriptor() {
            return null;
        }
        
        @java.lang.Override()
        public void serialize(@org.jetbrains.annotations.NotNull()
        kotlinx.serialization.encoding.Encoder encoder, @org.jetbrains.annotations.NotNull()
        com.securevote.remote.data.models.DeviceRegistrationRequest value) {
        }
        
        private $serializer() {
            super();
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public kotlinx.serialization.KSerializer<?>[] typeParametersSerializers() {
            return null;
        }
    }
    
    /**
     * Device registration request sent to sv-remote-auth.
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000f\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004H\u00c6\u0001\u00a8\u0006\u0006"}, d2 = {"Lcom/securevote/remote/data/models/DeviceRegistrationRequest$Companion;", "", "()V", "serializer", "Lkotlinx/serialization/KSerializer;", "Lcom/securevote/remote/data/models/DeviceRegistrationRequest;", "app_debug"})
    public static final class Companion {
        
        @org.jetbrains.annotations.NotNull()
        public final kotlinx.serialization.KSerializer<com.securevote.remote.data.models.DeviceRegistrationRequest> serializer() {
            return null;
        }
        
        private Companion() {
            super();
        }
    }
}