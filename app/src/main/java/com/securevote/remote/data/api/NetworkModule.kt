package com.securevote.remote.data.api

import com.securevote.remote.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.CertificatePinner
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Hilt module providing the sv-remote-gateway API client.
 *
 * SECURITY:
 *   - Certificate pinning against the election authority's TLS cert
 *   - TLS 1.3 only (configured via network_security_config.xml)
 *   - No cleartext traffic permitted
 *   - Request timeouts prevent slow-loris attacks
 *   - Logging disabled in release builds
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        isLenient = false
        prettyPrint = false
    }

    @Provides
    @Singleton
    fun provideCertificatePinner(): CertificatePinner {
        return CertificatePinner.Builder()
            // Pin the sv-remote-gateway TLS certificate
            // Replace with actual SHA-256 pins before deployment
            .add(
                "remote.securevote.gov",
                "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA="  // Primary
            )
            .add(
                "remote.securevote.gov",
                "sha256/BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB="  // Backup
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(pinner: CertificatePinner): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .certificatePinner(pinner)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)

        // Only log in debug builds — NEVER log request/response bodies in production
        // (they could contain PINs, tokens, or encrypted ballot data)
        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.HEADERS  // Headers only, never body
            }
            builder.addInterceptor(logging)
        }

        // Add device signature to every request
        builder.addInterceptor { chain ->
            val original = chain.request()
            val signed = original.newBuilder()
                .addHeader("X-SVR-Protocol-Version", BuildConfig.PROTOCOL_VERSION)
                .addHeader("X-SVR-App-Version", BuildConfig.VERSION_NAME)
                .addHeader("X-SVR-Device-Id", getDeviceIdHeader())
                .build()
            chain.proceed(signed)
        }

        return builder.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.SVR_GATEWAY_URL + "/")
            .client(client)
            .addConverterFactory(
                json.asConverterFactory("application/json; charset=UTF-8".toMediaType())
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideSVRGatewayApi(retrofit: Retrofit): SVRGatewayApi {
        return retrofit.create(SVRGatewayApi::class.java)
    }

    /**
     * Device ID header — a hash of the hardware-backed public key.
     * Used for request routing and rate limiting, NOT for identification.
     */
    private fun getDeviceIdHeader(): String {
        return try {
            com.securevote.remote.crypto.SVRCrypto.getDevicePublicKeyHash().take(16)
        } catch (e: Exception) {
            "unregistered"
        }
    }
}
