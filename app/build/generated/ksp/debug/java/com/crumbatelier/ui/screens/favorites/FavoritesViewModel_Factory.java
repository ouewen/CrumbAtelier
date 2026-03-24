package com.crumbatelier.ui.screens.favorites;

import com.crumbatelier.data.repository.AuthRepository;
import com.crumbatelier.data.repository.FavoriteRepository;
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
public final class FavoritesViewModel_Factory implements Factory<FavoritesViewModel> {
  private final Provider<FavoriteRepository> favRepoProvider;

  private final Provider<AuthRepository> authRepoProvider;

  public FavoritesViewModel_Factory(Provider<FavoriteRepository> favRepoProvider,
      Provider<AuthRepository> authRepoProvider) {
    this.favRepoProvider = favRepoProvider;
    this.authRepoProvider = authRepoProvider;
  }

  @Override
  public FavoritesViewModel get() {
    return newInstance(favRepoProvider.get(), authRepoProvider.get());
  }

  public static FavoritesViewModel_Factory create(Provider<FavoriteRepository> favRepoProvider,
      Provider<AuthRepository> authRepoProvider) {
    return new FavoritesViewModel_Factory(favRepoProvider, authRepoProvider);
  }

  public static FavoritesViewModel newInstance(FavoriteRepository favRepo,
      AuthRepository authRepo) {
    return new FavoritesViewModel(favRepo, authRepo);
  }
}
