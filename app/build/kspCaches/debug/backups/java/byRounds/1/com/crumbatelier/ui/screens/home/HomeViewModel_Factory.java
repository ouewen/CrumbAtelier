package com.crumbatelier.ui.screens.home;

import com.crumbatelier.data.repository.AuthRepository;
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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<RecipeRepository> recipeRepoProvider;

  private final Provider<FavoriteRepository> favoriteRepoProvider;

  private final Provider<AuthRepository> authRepoProvider;

  public HomeViewModel_Factory(Provider<RecipeRepository> recipeRepoProvider,
      Provider<FavoriteRepository> favoriteRepoProvider,
      Provider<AuthRepository> authRepoProvider) {
    this.recipeRepoProvider = recipeRepoProvider;
    this.favoriteRepoProvider = favoriteRepoProvider;
    this.authRepoProvider = authRepoProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(recipeRepoProvider.get(), favoriteRepoProvider.get(), authRepoProvider.get());
  }

  public static HomeViewModel_Factory create(Provider<RecipeRepository> recipeRepoProvider,
      Provider<FavoriteRepository> favoriteRepoProvider,
      Provider<AuthRepository> authRepoProvider) {
    return new HomeViewModel_Factory(recipeRepoProvider, favoriteRepoProvider, authRepoProvider);
  }

  public static HomeViewModel newInstance(RecipeRepository recipeRepo,
      FavoriteRepository favoriteRepo, AuthRepository authRepo) {
    return new HomeViewModel(recipeRepo, favoriteRepo, authRepo);
  }
}
