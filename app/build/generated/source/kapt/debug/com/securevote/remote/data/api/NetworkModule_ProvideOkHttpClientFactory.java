package com.securevote.remote.data.api;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class NetworkModule_ProvideOkHttpClientFactory implements Factory<OkHttpClient> {
  private final Provider<CertificatePinner> pinnerProvider;

  public NetworkModule_ProvideOkHttpClientFactory(Provider<CertificatePinner> pinnerProvider) {
    this.pinnerProvider = pinnerProvider;
  }

  @Override
  public OkHttpClient get() {
    return provideOkHttpClient(pinnerProvider.get());
  }

  public static NetworkModule_ProvideOkHttpClientFactory create(
      Provider<CertificatePinner> pinnerProvider) {
    return new NetworkModule_ProvideOkHttpClientFactory(pinnerProvider);
  }

  public static OkHttpClient provideOkHttpClient(CertificatePinner pinner) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideOkHttpClient(pinner));
  }
}
