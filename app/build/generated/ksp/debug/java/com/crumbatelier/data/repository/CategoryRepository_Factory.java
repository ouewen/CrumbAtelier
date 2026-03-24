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
public final class CategoryRepository_Factory implements Factory<CategoryRepository> {
  private final Provider<SupabaseClient> supabaseProvider;

  private final Provider<CoroutineScope> appScopeProvider;

  public CategoryRepository_Factory(Provider<SupabaseClient> supabaseProvider,
      Provider<CoroutineScope> appScopeProvider) {
    this.supabaseProvider = supabaseProvider;
    this.appScopeProvider = appScopeProvider;
  }

  @Override
  public CategoryRepository get() {
    return newInstance(supabaseProvider.get(), appScopeProvider.get());
  }

  public static CategoryRepository_Factory create(Provider<SupabaseClient> supabaseProvider,
      Provider<CoroutineScope> appScopeProvider) {
    return new CategoryRepository_Factory(supabaseProvider, appScopeProvider);
  }

  public static CategoryRepository newInstance(SupabaseClient supabase, CoroutineScope appScope) {
    return new CategoryRepository(supabase, appScope);
  }
}
