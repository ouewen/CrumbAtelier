package com.crumbatelier.ui.screens.detail;

import com.crumbatelier.data.repository.AuthRepository;
import com.crumbatelier.data.repository.CommentRepository;
import com.crumbatelier.data.repository.FavoriteRepository;
import com.crumbatelier.data.repository.RecipeRepository;
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
public final class RecipeDetailViewModel_Factory implements Factory<RecipeDetailViewModel> {
  private final Provider<RecipeRepository> recipeRepoProvider;

  private final Provider<CommentRepository> commentRepoProvider;

  private final Provider<FavoriteRepository> favoriteRepoProvider;

  private final Provider<AuthRepository> authRepoProvider;

  public RecipeDetailViewModel_Factory(Provider<RecipeRepository> recipeRepoProvider,
      Provider<CommentRepository> commentRepoProvider,
      Provider<FavoriteRepository> favoriteRepoProvider,
      Provider<AuthRepository> authRepoProvider) {
    this.recipeRepoProvider = recipeRepoProvider;
    this.commentRepoProvider = commentRepoProvider;
    this.favoriteRepoProvider = favoriteRepoProvider;
    this.authRepoProvider = authRepoProvider;
  }

  @Override
  public RecipeDetailViewModel get() {
    return newInstance(recipeRepoProvider.get(), commentRepoProvider.get(), favoriteRepoProvider.get(), authRepoProvider.get());
  }

  public static RecipeDetailViewModel_Factory create(Provider<RecipeRepository> recipeRepoProvider,
      Provider<CommentRepository> commentRepoProvider,
      Provider<FavoriteRepository> favoriteRepoProvider,
      Provider<AuthRepository> authRepoProvider) {
    return new RecipeDetailViewModel_Factory(recipeRepoProvider, commentRepoProvider, favoriteRepoProvider, authRepoProvider);
  }

  public static RecipeDetailViewModel newInstance(RecipeRepository recipeRepo,
      CommentRepository commentRepo, FavoriteRepository favoriteRepo, AuthRepository authRepo) {
    return new RecipeDetailViewModel(recipeRepo, commentRepo, favoriteRepo, authRepo);
  }
}
