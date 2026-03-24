package com.crumbatelier.ui.screens.auth;

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
public final class RegisterViewModel_Factory implements Factory<RegisterViewModel> {
  private final Provider<AuthRepository> authProvider;

  public RegisterViewModel_Factory(Provider<AuthRepository> authProvider) {
    this.authProvider = authProvider;
  }

  @Override
  public RegisterViewModel get() {
    return newInstance(authProvider.get());
  }

  public static RegisterViewModel_Factory create(Provider<AuthRepository> authProvider) {
    return new RegisterViewModel_Factory(authProvider);
  }

  public static RegisterViewModel newInstance(AuthRepository auth) {
    return new RegisterViewModel(auth);
  }
}
