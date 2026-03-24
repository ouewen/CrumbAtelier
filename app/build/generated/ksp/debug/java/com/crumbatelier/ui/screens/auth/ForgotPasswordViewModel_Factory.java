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
public final class ForgotPasswordViewModel_Factory implements Factory<ForgotPasswordViewModel> {
  private final Provider<AuthRepository> authProvider;

  public ForgotPasswordViewModel_Factory(Provider<AuthRepository> authProvider) {
    this.authProvider = authProvider;
  }

  @Override
  public ForgotPasswordViewModel get() {
    return newInstance(authProvider.get());
  }

  public static ForgotPasswordViewModel_Factory create(Provider<AuthRepository> authProvider) {
    return new ForgotPasswordViewModel_Factory(authProvider);
  }

  public static ForgotPasswordViewModel newInstance(AuthRepository auth) {
    return new ForgotPasswordViewModel(auth);
  }
}
