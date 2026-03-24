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
public final class ResetPasswordViewModel_Factory implements Factory<ResetPasswordViewModel> {
  private final Provider<AuthRepository> authProvider;

  public ResetPasswordViewModel_Factory(Provider<AuthRepository> authProvider) {
    this.authProvider = authProvider;
  }

  @Override
  public ResetPasswordViewModel get() {
    return newInstance(authProvider.get());
  }

  public static ResetPasswordViewModel_Factory create(Provider<AuthRepository> authProvider) {
    return new ResetPasswordViewModel_Factory(authProvider);
  }

  public static ResetPasswordViewModel newInstance(AuthRepository auth) {
    return new ResetPasswordViewModel(auth);
  }
}
