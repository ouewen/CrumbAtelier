package com.crumbatelier.data.repository;

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
public final class RecipeRepository_Factory implements Factory<RecipeRepository> {
  private final Provider<SupabaseClient> supabaseProvider;

  private final Provider<CoroutineScope> appScopeProvider;

  public RecipeRepository_Factory(Provider<SupabaseClient> supabaseProvider,
      Provider<CoroutineScope> appScopeProvider) {
    this.supabaseProvider = supabaseProvider;
    this.appScopeProvider = appScopeProvider;
  }

  @Override
  public RecipeRepository get() {
    return newInstance(supabaseProvider.get(), appScopeProvider.get());
  }

  public static RecipeRepository_Factory create(Provider<SupabaseClient> supabaseProvider,
      Provider<CoroutineScope> appScopeProvider) {
    return new RecipeRepository_Factory(supabaseProvider, appScopeProvider);
  }

  public static RecipeRepository newInstance(SupabaseClient supabase, CoroutineScope appScope) {
    return new RecipeRepository(supabase, appScope);
  }
}
