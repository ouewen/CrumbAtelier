package com.crumbatelier.ui.screens.categories;

import com.crumbatelier.data.repository.AuthRepository;
import com.crumbatelier.data.repository.CategoryRepository;
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
public final class CategoriesViewModel_Factory implements Factory<CategoriesViewModel> {
  private final Provider<CategoryRepository> categoryRepoProvider;

  private final Provider<AuthRepository> authRepoProvider;

  public CategoriesViewModel_Factory(Provider<CategoryRepository> categoryRepoProvider,
      Provider<AuthRepository> authRepoProvider) {
    this.categoryRepoProvider = categoryRepoProvider;
    this.authRepoProvider = authRepoProvider;
  }

  @Override
  public CategoriesViewModel get() {
    return newInstance(categoryRepoProvider.get(), authRepoProvider.get());
  }

  public static CategoriesViewModel_Factory create(
      Provider<CategoryRepository> categoryRepoProvider,
      Provider<AuthRepository> authRepoProvider) {
    return new CategoriesViewModel_Factory(categoryRepoProvider, authRepoProvider);
  }

  public static CategoriesViewModel newInstance(CategoryRepository categoryRepo,
      AuthRepository authRepo) {
    return new CategoriesViewModel(categoryRepo, authRepo);
  }
}
