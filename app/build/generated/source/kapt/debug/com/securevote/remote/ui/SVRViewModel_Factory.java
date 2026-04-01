package com.securevote.remote.ui;

import com.securevote.remote.data.repository.SVRRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class SVRViewModel_Factory implements Factory<SVRViewModel> {
  private final Provider<SVRRepository> repositoryProvider;

  public SVRViewModel_Factory(Provider<SVRRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public SVRViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static SVRViewModel_Factory create(Provider<SVRRepository> repositoryProvider) {
    return new SVRViewModel_Factory(repositoryProvider);
  }

  public static SVRViewModel newInstance(SVRRepository repository) {
    return new SVRViewModel(repository);
  }
}
