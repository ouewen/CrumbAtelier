package com.crumbatelier.ui.screens.admin;

import com.crumbatelier.data.repository.AuthRepository;
import com.crumbatelier.data.repository.CategoryRepository;
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
public final class AdminRecipeViewModel_Factory implements Factory<AdminRecipeViewModel> {
  private final Provider<RecipeRepository> recipeRepoProvider;

  private final Provider<AuthRepository> authRepoProvider;

  private final Provider<CategoryRepository> categoryRepoProvider;

  public AdminRecipeViewModel_Factory(Provider<RecipeRepository> recipeRepoProvider,
      Provider<AuthRepository> authRepoProvider,
      Provider<CategoryRepository> categoryRepoProvider) {
    this.recipeRepoProvider = recipeRepoProvider;
    this.authRepoProvider = authRepoProvider;
    this.categoryRepoProvider = categoryRepoProvider;
  }

  @Override
  public AdminRecipeViewModel get() {
    return newInstance(recipeRepoProvider.get(), authRepoProvider.get(), categoryRepoProvider.get());
  }

  public static AdminRecipeViewModel_Factory create(Provider<RecipeRepository> recipeRepoProvider,
      Provider<AuthRepository> authRepoProvider,
      Provider<CategoryRepository> categoryRepoProvider) {
    return new AdminRecipeViewModel_Factory(recipeRepoProvider, authRepoProvider, categoryRepoProvider);
  }

  public static AdminRecipeViewModel newInstance(RecipeRepository recipeRepo,
      AuthRepository authRepo, CategoryRepository categoryRepo) {
    return new AdminRecipeViewModel(recipeRepo, authRepo, categoryRepo);
  }
}
