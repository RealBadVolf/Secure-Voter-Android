plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.plugin.serialization")
    kotlin("kapt")
}

android {
    namespace = "com.securevote.remote"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.securevote.remote"
        minSdk = 28  // Android 9+ required for StrongBox Keystore
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0-beta"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "SVR_GATEWAY_URL", "\"https://remote.securevote.gov/api/v1\"")
        buildConfigField("String", "PROTOCOL_VERSION", "\"1\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug") // Replace with election authority signing config
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += setOf(
                "META-INF/versions/9/OSGI-INF/MANIFEST.MF",
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt",
            )
        }
    }
}

dependencies {
    // ---- Core Android ----
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.activity:activity-compose:1.9.3")

    // ---- Jetpack Compose (BOM) ----
    implementation(platform("androidx.compose:compose-bom:2024.12.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.animation:animation")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // ---- Navigation ----
    implementation("androidx.navigation:navigation-compose:2.8.5")

    // ---- Hilt (Dependency Injection) ----
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-compiler:2.51.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // ---- Networking (sv-remote-gateway API) ----
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("com.squareup.retrofit2:converter-kotlinx-serialization:2.11.0")

    // ---- Certificate Pinning ----
    implementation("com.squareup.okhttp3:okhttp-tls:4.12.0")

    // ---- CameraX (Biometric capture) ----
    implementation("androidx.camera:camera-core:1.4.1")
    implementation("androidx.camera:camera-camera2:1.4.1")
    implementation("androidx.camera:camera-lifecycle:1.4.1")
    implementation("androidx.camera:camera-view:1.4.1")

    // ---- ML Kit (Face detection + liveness) ----
    implementation("com.google.mlkit:face-detection:16.1.7")

    // ---- Biometric API ----
    implementation("androidx.biometric:biometric:1.2.0-alpha05")

    // ---- QR Code Generation (for IPVC fallback) ----
    implementation("com.google.zxing:core:3.5.3")

    // ---- QR Code Scanning (for admin portal) ----
    implementation("com.google.mlkit:barcode-scanning:17.3.0")

    // ---- NFC ----
    // Uses android.nfc (built-in, no external dependency)

    // ---- Security / Crypto ----
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    // Bouncy Castle for ML-KEM (post-quantum) — bundled, not a runtime dependency
    implementation("org.bouncycastle:bcprov-jdk18on:1.79")
    implementation("org.bouncycastle:bcpkix-jdk18on:1.79")

    // ---- Play Integrity (Device Attestation) ----
    implementation("com.google.android.play:integrity:1.4.0")

    // ---- DataStore (Encrypted local preferences) ----
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // ---- Testing ----
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.12.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
}

kapt {
    correctErrorTypes = true
}
