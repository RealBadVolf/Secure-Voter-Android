package com.securevote.remote.data.repository;

import com.securevote.remote.data.api.SVRGatewayApi;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class SVRRepository_Factory implements Factory<SVRRepository> {
  private final Provider<SVRGatewayApi> apiProvider;

  public SVRRepository_Factory(Provider<SVRGatewayApi> apiProvider) {
    this.apiProvider = apiProvider;
  }

  @Override
  public SVRRepository get() {
    return newInstance(apiProvider.get());
  }

  public static SVRRepository_Factory create(Provider<SVRGatewayApi> apiProvider) {
    return new SVRRepository_Factory(apiProvider);
  }

  public static SVRRepository newInstance(SVRGatewayApi api) {
    return new SVRRepository(api);
  }
}
