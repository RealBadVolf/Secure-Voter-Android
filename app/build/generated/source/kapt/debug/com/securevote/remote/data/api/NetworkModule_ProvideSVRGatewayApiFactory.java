package com.securevote.remote.data.api;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import retrofit2.Retrofit;

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
public final class NetworkModule_ProvideSVRGatewayApiFactory implements Factory<SVRGatewayApi> {
  private final Provider<Retrofit> retrofitProvider;

  public NetworkModule_ProvideSVRGatewayApiFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public SVRGatewayApi get() {
    return provideSVRGatewayApi(retrofitProvider.get());
  }

  public static NetworkModule_ProvideSVRGatewayApiFactory create(
      Provider<Retrofit> retrofitProvider) {
    return new NetworkModule_ProvideSVRGatewayApiFactory(retrofitProvider);
  }

  public static SVRGatewayApi provideSVRGatewayApi(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideSVRGatewayApi(retrofit));
  }
}
