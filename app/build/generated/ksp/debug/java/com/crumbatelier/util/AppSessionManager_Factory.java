package com.crumbatelier.util;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.github.jan.supabase.SupabaseClient;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import kotlinx.coroutines.CoroutineScope;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("com.crumbatelier.util.ApplicationScope")
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
public final class AppSessionManager_Factory implements Factory<AppSessionManager> {
  private final Provider<SupabaseClient> supabaseProvider;

  private final Provider<CoroutineScope> appScopeProvider;

  public AppSessionManager_Factory(Provider<SupabaseClient> supabaseProvider,
      Provider<CoroutineScope> appScopeProvider) {
    this.supabaseProvider = supabaseProvider;
    this.appScopeProvider = appScopeProvider;
  }

  @Override
  public AppSessionManager get() {
    return newInstance(supabaseProvider.get(), appScopeProvider.get());
  }

  public static AppSessionManager_Factory create(Provider<SupabaseClient> supabaseProvider,
      Provider<CoroutineScope> appScopeProvider) {
    return new AppSessionManager_Factory(supabaseProvider, appScopeProvider);
  }

  public static AppSessionManager newInstance(SupabaseClient supabase, CoroutineScope appScope) {
    return new AppSessionManager(supabase, appScope);
  }
}
