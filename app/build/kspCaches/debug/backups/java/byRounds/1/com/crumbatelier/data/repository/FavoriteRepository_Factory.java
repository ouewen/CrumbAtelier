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
public final class FavoriteRepository_Factory implements Factory<FavoriteRepository> {
  private final Provider<SupabaseClient> supabaseProvider;

  private final Provider<RecipeRepository> recipeRepositoryProvider;

  private final Provider<CoroutineScope> appScopeProvider;

  public FavoriteRepository_Factory(Provider<SupabaseClient> supabaseProvider,
      Provider<RecipeRepository> recipeRepositoryProvider,
      Provider<CoroutineScope> appScopeProvider) {
    this.supabaseProvider = supabaseProvider;
    this.recipeRepositoryProvider = recipeRepositoryProvider;
    this.appScopeProvider = appScopeProvider;
  }

  @Override
  public FavoriteRepository get() {
    return newInstance(supabaseProvider.get(), recipeRepositoryProvider.get(), appScopeProvider.get());
  }

  public static FavoriteRepository_Factory create(Provider<SupabaseClient> supabaseProvider,
      Provider<RecipeRepository> recipeRepositoryProvider,
      Provider<CoroutineScope> appScopeProvider) {
    return new FavoriteRepository_Factory(supabaseProvider, recipeRepositoryProvider, appScopeProvider);
  }

  public static FavoriteRepository newInstance(SupabaseClient supabase,
      RecipeRepository recipeRepository, CoroutineScope appScope) {
    return new FavoriteRepository(supabase, recipeRepository, appScope);
  }
}
