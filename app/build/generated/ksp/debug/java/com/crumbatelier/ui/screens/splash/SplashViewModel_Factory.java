package com.crumbatelier.ui.screens.splash;

import com.crumbatelier.util.AppSessionManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.github.jan.supabase.SupabaseClient;
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
public final class SplashViewModel_Factory implements Factory<SplashViewModel> {
  private final Provider<SupabaseClient> supabaseProvider;

  private final Provider<AppSessionManager> sessionManagerProvider;

  public SplashViewModel_Factory(Provider<SupabaseClient> supabaseProvider,
      Provider<AppSessionManager> sessionManagerProvider) {
    this.supabaseProvider = supabaseProvider;
    this.sessionManagerProvider = sessionManagerProvider;
  }

  @Override
  public SplashViewModel get() {
    return newInstance(supabaseProvider.get(), sessionManagerProvider.get());
  }

  public static SplashViewModel_Factory create(Provider<SupabaseClient> supabaseProvider,
      Provider<AppSessionManager> sessionManagerProvider) {
    return new SplashViewModel_Factory(supabaseProvider, sessionManagerProvider);
  }

  public static SplashViewModel newInstance(SupabaseClient supabase,
      AppSessionManager sessionManager) {
    return new SplashViewModel(supabase, sessionManager);
  }
}
