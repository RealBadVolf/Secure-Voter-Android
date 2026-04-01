package com.securevote.remote.biometric;

import android.graphics.Bitmap;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.securevote.remote.crypto.SVRCrypto;

/**
 * Result of ML Kit face detection on a single frame.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b7\u0018\u00002\u00020\u0001:\u0003\u0003\u0004\u0005B\u0007\b\u0004\u00a2\u0006\u0002\u0010\u0002\u0082\u0001\u0003\u0006\u0007\b\u00a8\u0006\t"}, d2 = {"Lcom/securevote/remote/biometric/FaceDetectionResult;", "", "()V", "Detected", "MultipleFaces", "NoFace", "Lcom/securevote/remote/biometric/FaceDetectionResult$Detected;", "Lcom/securevote/remote/biometric/FaceDetectionResult$MultipleFaces;", "Lcom/securevote/remote/biometric/FaceDetectionResult$NoFace;", "app_debug"})
public abstract class FaceDetectionResult {
    
    private FaceDetectionResult() {
        super();
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0018\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001BG\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\u0006\u0010\u0007\u001a\u00020\u0003\u0012\u0006\u0010\b\u001a\u00020\u0003\u0012\u0006\u0010\t\u001a\u00020\n\u0012\b\u0010\u000b\u001a\u0004\u0018\u00010\f\u00a2\u0006\u0002\u0010\rJ\t\u0010\u001a\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001b\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001c\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001d\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001e\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010 \u001a\u00020\nH\u00c6\u0003J\u0010\u0010!\u001a\u0004\u0018\u00010\fH\u00c6\u0003\u00a2\u0006\u0002\u0010\u0018J`\u0010\"\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\u00032\b\b\u0002\u0010\t\u001a\u00020\n2\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\fH\u00c6\u0001\u00a2\u0006\u0002\u0010#J\u0013\u0010$\u001a\u00020%2\b\u0010&\u001a\u0004\u0018\u00010\'H\u00d6\u0003J\t\u0010(\u001a\u00020\fH\u00d6\u0001J\t\u0010)\u001a\u00020*H\u00d6\u0001R\u0011\u0010\t\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\u0007\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0011R\u0011\u0010\b\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0011R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0011R\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0011R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0011R\u0015\u0010\u000b\u001a\u0004\u0018\u00010\f\u00a2\u0006\n\n\u0002\u0010\u0019\u001a\u0004\b\u0017\u0010\u0018\u00a8\u0006+"}, d2 = {"Lcom/securevote/remote/biometric/FaceDetectionResult$Detected;", "Lcom/securevote/remote/biometric/FaceDetectionResult;", "smilingProbability", "", "leftEyeOpenProbability", "rightEyeOpenProbability", "headEulerAngleX", "headEulerAngleY", "headEulerAngleZ", "boundingBox", "Landroid/graphics/Rect;", "trackingId", "", "(FFFFFFLandroid/graphics/Rect;Ljava/lang/Integer;)V", "getBoundingBox", "()Landroid/graphics/Rect;", "getHeadEulerAngleX", "()F", "getHeadEulerAngleY", "getHeadEulerAngleZ", "getLeftEyeOpenProbability", "getRightEyeOpenProbability", "getSmilingProbability", "getTrackingId", "()Ljava/lang/Integer;", "Ljava/lang/Integer;", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "copy", "(FFFFFFLandroid/graphics/Rect;Ljava/lang/Integer;)Lcom/securevote/remote/biometric/FaceDetectionResult$Detected;", "equals", "", "other", "", "hashCode", "toString", "", "app_debug"})
    public static final class Detected extends com.securevote.remote.biometric.FaceDetectionResult {
        private final float smilingProbability = 0.0F;
        private final float leftEyeOpenProbability = 0.0F;
        private final float rightEyeOpenProbability = 0.0F;
        private final float headEulerAngleX = 0.0F;
        private final float headEulerAngleY = 0.0F;
        private final float headEulerAngleZ = 0.0F;
        @org.jetbrains.annotations.NotNull()
        private final android.graphics.Rect boundingBox = null;
        @org.jetbrains.annotations.Nullable()
        private final java.lang.Integer trackingId = null;
        
        public final float component1() {
            return 0.0F;
        }
        
        public final float component2() {
            return 0.0F;
        }
        
        public final float component3() {
            return 0.0F;
        }
        
        public final float component4() {
            return 0.0F;
        }
        
        public final float component5() {
            return 0.0F;
        }
        
        public final float component6() {
            return 0.0F;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final android.graphics.Rect component7() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.Integer component8() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.securevote.remote.biometric.FaceDetectionResult.Detected copy(float smilingProbability, float leftEyeOpenProbability, float rightEyeOpenProbability, float headEulerAngleX, float headEulerAngleY, float headEulerAngleZ, @org.jetbrains.annotations.NotNull()
        android.graphics.Rect boundingBox, @org.jetbrains.annotations.Nullable()
        java.lang.Integer trackingId) {
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
        
        public Detected(float smilingProbability, float leftEyeOpenProbability, float rightEyeOpenProbability, float headEulerAngleX, float headEulerAngleY, float headEulerAngleZ, @org.jetbrains.annotations.NotNull()
        android.graphics.Rect boundingBox, @org.jetbrains.annotations.Nullable()
        java.lang.Integer trackingId) {
        }
        
        public final float getSmilingProbability() {
            return 0.0F;
        }
        
        public final float getLeftEyeOpenProbability() {
            return 0.0F;
        }
        
        public final float getRightEyeOpenProbability() {
            return 0.0F;
        }
        
        public final float getHeadEulerAngleX() {
            return 0.0F;
        }
        
        public final float getHeadEulerAngleY() {
            return 0.0F;
        }
        
        public final float getHeadEulerAngleZ() {
            return 0.0F;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final android.graphics.Rect getBoundingBox() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.Integer getTrackingId() {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/securevote/remote/biometric/FaceDetectionResult$MultipleFaces;", "Lcom/securevote/remote/biometric/FaceDetectionResult;", "()V", "app_debug"})
    public static final class MultipleFaces extends com.securevote.remote.biometric.FaceDetectionResult {
        @org.jetbrains.annotations.NotNull()
        public static final com.securevote.remote.biometric.FaceDetectionResult.MultipleFaces INSTANCE = null;
        
        private MultipleFaces() {
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/securevote/remote/biometric/FaceDetectionResult$NoFace;", "Lcom/securevote/remote/biometric/FaceDetectionResult;", "()V", "app_debug"})
    public static final class NoFace extends com.securevote.remote.biometric.FaceDetectionResult {
        @org.jetbrains.annotations.NotNull()
        public static final com.securevote.remote.biometric.FaceDetectionResult.NoFace INSTANCE = null;
        
        private NoFace() {
        }
    }
}