package com.securevote.remote.data.repository;

import com.securevote.remote.data.api.SVRGatewayApi;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class RepositoryModule_ProvideSVRRepositoryFactory implements Factory<SVRRepository> {
  private final Provider<SVRGatewayApi> apiProvider;

  public RepositoryModule_ProvideSVRRepositoryFactory(Provider<SVRGatewayApi> apiProvider) {
    this.apiProvider = apiProvider;
  }

  @Override
  public SVRRepository get() {
    return provideSVRRepository(apiProvider.get());
  }

  public static RepositoryModule_ProvideSVRRepositoryFactory create(
      Provider<SVRGatewayApi> apiProvider) {
    return new RepositoryModule_ProvideSVRRepositoryFactory(apiProvider);
  }

  public static SVRRepository provideSVRRepository(SVRGatewayApi api) {
    return Preconditions.checkNotNullFromProvides(RepositoryModule.INSTANCE.provideSVRRepository(api));
  }
}
