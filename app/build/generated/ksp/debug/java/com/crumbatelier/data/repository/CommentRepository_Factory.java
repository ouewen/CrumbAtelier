package com.crumbatelier.data.repository;

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
public final class CommentRepository_Factory implements Factory<CommentRepository> {
  private final Provider<SupabaseClient> supabaseProvider;

  public CommentRepository_Factory(Provider<SupabaseClient> supabaseProvider) {
    this.supabaseProvider = supabaseProvider;
  }

  @Override
  public CommentRepository get() {
    return newInstance(supabaseProvider.get());
  }

  public static CommentRepository_Factory create(Provider<SupabaseClient> supabaseProvider) {
    return new CommentRepository_Factory(supabaseProvider);
  }

  public static CommentRepository newInstance(SupabaseClient supabase) {
    return new CommentRepository(supabase);
  }
}
