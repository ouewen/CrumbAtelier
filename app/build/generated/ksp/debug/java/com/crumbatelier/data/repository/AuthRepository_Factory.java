package com.crumbatelier.data.repository;

import com.crumbatelier.util.AppSessionManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.github.jan.supabase.SupabaseClient;
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
    "cast",
    "deprecation"
})
public final class AuthRepository_Factory implements Factory<AuthRepository> {
  private final Provider<SupabaseClient> supabaseProvider;

  private final Provider<AppSessionManager> sessionManagerProvider;

  public AuthRepository_Factory(Provider<SupabaseClient> supabaseProvider,
      Provider<AppSessionManager> sessionManagerProvider) {
    this.supabaseProvider = supabaseProvider;
    this.sessionManagerProvider = sessionManagerProvider;
  }

  @Override
  public AuthRepository get() {
    return newInstance(supabaseProvider.get(), sessionManagerProvider.get());
  }

  public static AuthRepository_Factory create(Provider<SupabaseClient> supabaseProvider,
      Provider<AppSessionManager> sessionManagerProvider) {
    return new AuthRepository_Factory(supabaseProvider, sessionManagerProvider);
  }

  public static AuthRepository newInstance(SupabaseClient supabase,
      AppSessionManager sessionManager) {
    return new AuthRepository(supabase, sessionManager);
  }
}
