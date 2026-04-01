# SecureVote Remote — ProGuard rules

# Keep all crypto classes (critical — obfuscation must not break crypto)
-keep class com.securevote.remote.crypto.** { *; }
-keep class org.bouncycastle.** { *; }

# Keep serialization models
-keep class com.securevote.remote.data.models.** { *; }
-keepclassmembers class com.securevote.remote.data.models.** { *; }

# Keep Retrofit API interface
-keep class com.securevote.remote.data.api.SVRGatewayApi { *; }

# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

# ML Kit
-keep class com.google.mlkit.** { *; }

# ZXing QR
-keep class com.google.zxing.** { *; }
