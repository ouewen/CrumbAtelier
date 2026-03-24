package com.crumbatelier;

import com.crumbatelier.util.AppSessionManager;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import io.github.jan.supabase.SupabaseClient;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
  private final Provider<AppSessionManager> sessionManagerProvider;

  private final Provider<SupabaseClient> supabaseProvider;

  public MainActivity_MembersInjector(Provider<AppSessionManager> sessionManagerProvider,
      Provider<SupabaseClient> supabaseProvider) {
    this.sessionManagerProvider = sessionManagerProvider;
    this.supabaseProvider = supabaseProvider;
  }

  public static MembersInjector<MainActivity> create(
      Provider<AppSessionManager> sessionManagerProvider,
      Provider<SupabaseClient> supabaseProvider) {
    return new MainActivity_MembersInjector(sessionManagerProvider, supabaseProvider);
  }

  @Override
  public void injectMembers(MainActivity instance) {
    injectSessionManager(instance, sessionManagerProvider.get());
    injectSupabase(instance, supabaseProvider.get());
  }

  @InjectedFieldSignature("com.crumbatelier.MainActivity.sessionManager")
  public static void injectSessionManager(MainActivity instance, AppSessionManager sessionManager) {
    instance.sessionManager = sessionManager;
  }

  @InjectedFieldSignature("com.crumbatelier.MainActivity.supabase")
  public static void injectSupabase(MainActivity instance, SupabaseClient supabase) {
    instance.supabase = supabase;
  }
}
