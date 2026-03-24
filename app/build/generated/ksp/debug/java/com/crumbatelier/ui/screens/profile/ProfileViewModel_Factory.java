package com.crumbatelier.ui.screens.profile;

import com.crumbatelier.data.repository.AuthRepository;
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
    "cast",
    "deprecation"
})
public final class ProfileViewModel_Factory implements Factory<ProfileViewModel> {
  private final Provider<AuthRepository> authRepoProvider;

  public ProfileViewModel_Factory(Provider<AuthRepository> authRepoProvider) {
    this.authRepoProvider = authRepoProvider;
  }

  @Override
  public ProfileViewModel get() {
    return newInstance(authRepoProvider.get());
  }

  public static ProfileViewModel_Factory create(Provider<AuthRepository> authRepoProvider) {
    return new ProfileViewModel_Factory(authRepoProvider);
  }

  public static ProfileViewModel newInstance(AuthRepository authRepo) {
    return new ProfileViewModel(authRepo);
  }
}
