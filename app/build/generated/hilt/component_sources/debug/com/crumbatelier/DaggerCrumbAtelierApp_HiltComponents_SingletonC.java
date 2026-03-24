package com.crumbatelier;

import android.app.Activity;
import android.app.Service;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import com.crumbatelier.data.repository.AuthRepository;
import com.crumbatelier.data.repository.CategoryRepository;
import com.crumbatelier.data.repository.CommentRepository;
import com.crumbatelier.data.repository.FavoriteRepository;
import com.crumbatelier.data.repository.RecipeRepository;
import com.crumbatelier.di.AppModule_ProvideApplicationScopeFactory;
import com.crumbatelier.di.AppModule_ProvideSupabaseClientFactory;
import com.crumbatelier.ui.screens.admin.AdminRecipeViewModel;
import com.crumbatelier.ui.screens.admin.AdminRecipeViewModel_HiltModules;
import com.crumbatelier.ui.screens.auth.ForgotPasswordViewModel;
import com.crumbatelier.ui.screens.auth.ForgotPasswordViewModel_HiltModules;
import com.crumbatelier.ui.screens.auth.LoginViewModel;
import com.crumbatelier.ui.screens.auth.LoginViewModel_HiltModules;
import com.crumbatelier.ui.screens.auth.RegisterViewModel;
import com.crumbatelier.ui.screens.auth.RegisterViewModel_HiltModules;
import com.crumbatelier.ui.screens.auth.ResetPasswordViewModel;
import com.crumbatelier.ui.screens.auth.ResetPasswordViewModel_HiltModules;
import com.crumbatelier.ui.screens.categories.CategoriesViewModel;
import com.crumbatelier.ui.screens.categories.CategoriesViewModel_HiltModules;
import com.crumbatelier.ui.screens.detail.RecipeDetailViewModel;
import com.crumbatelier.ui.screens.detail.RecipeDetailViewModel_HiltModules;
import com.crumbatelier.ui.screens.favorites.FavoritesViewModel;
import com.crumbatelier.ui.screens.favorites.FavoritesViewModel_HiltModules;
import com.crumbatelier.ui.screens.home.HomeViewModel;
import com.crumbatelier.ui.screens.home.HomeViewModel_HiltModules;
import com.crumbatelier.ui.screens.profile.ProfileViewModel;
import com.crumbatelier.ui.screens.profile.ProfileViewModel_HiltModules;
import com.crumbatelier.ui.screens.splash.SplashViewModel;
import com.crumbatelier.ui.screens.splash.SplashViewModel_HiltModules;
import com.crumbatelier.util.AppSessionManager;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.IdentifierNameString;
import dagger.internal.KeepFieldType;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.MapBuilder;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import io.github.jan.supabase.SupabaseClient;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;
import kotlinx.coroutines.CoroutineScope;

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
public final class DaggerCrumbAtelierApp_HiltComponents_SingletonC {
  private DaggerCrumbAtelierApp_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static CrumbAtelierApp_HiltComponents.SingletonC create() {
    return new Builder().build();
  }

  public static final class Builder {
    private Builder() {
    }

    /**
     * @deprecated This module is declared, but an instance is not used in the component. This method is a no-op. For more, see https://dagger.dev/unused-modules.
     */
    @Deprecated
    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public CrumbAtelierApp_HiltComponents.SingletonC build() {
      return new SingletonCImpl();
    }
  }

  private static final class ActivityRetainedCBuilder implements CrumbAtelierApp_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public CrumbAtelierApp_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements CrumbAtelierApp_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public CrumbAtelierApp_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements CrumbAtelierApp_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public CrumbAtelierApp_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements CrumbAtelierApp_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public CrumbAtelierApp_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements CrumbAtelierApp_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public CrumbAtelierApp_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements CrumbAtelierApp_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public CrumbAtelierApp_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements CrumbAtelierApp_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public CrumbAtelierApp_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends CrumbAtelierApp_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends CrumbAtelierApp_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends CrumbAtelierApp_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends CrumbAtelierApp_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity arg0) {
      injectMainActivity2(arg0);
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(MapBuilder.<String, Boolean>newMapBuilder(11).put(LazyClassKeyProvider.com_crumbatelier_ui_screens_admin_AdminRecipeViewModel, AdminRecipeViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_crumbatelier_ui_screens_categories_CategoriesViewModel, CategoriesViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_crumbatelier_ui_screens_favorites_FavoritesViewModel, FavoritesViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_crumbatelier_ui_screens_auth_ForgotPasswordViewModel, ForgotPasswordViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_crumbatelier_ui_screens_home_HomeViewModel, HomeViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_crumbatelier_ui_screens_auth_LoginViewModel, LoginViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_crumbatelier_ui_screens_profile_ProfileViewModel, ProfileViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_crumbatelier_ui_screens_detail_RecipeDetailViewModel, RecipeDetailViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_crumbatelier_ui_screens_auth_RegisterViewModel, RegisterViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_crumbatelier_ui_screens_auth_ResetPasswordViewModel, ResetPasswordViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_crumbatelier_ui_screens_splash_SplashViewModel, SplashViewModel_HiltModules.KeyModule.provide()).build());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    private MainActivity injectMainActivity2(MainActivity instance) {
      MainActivity_MembersInjector.injectSessionManager(instance, singletonCImpl.appSessionManagerProvider.get());
      MainActivity_MembersInjector.injectSupabase(instance, singletonCImpl.provideSupabaseClientProvider.get());
      return instance;
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_crumbatelier_ui_screens_admin_AdminRecipeViewModel = "com.crumbatelier.ui.screens.admin.AdminRecipeViewModel";

      static String com_crumbatelier_ui_screens_auth_LoginViewModel = "com.crumbatelier.ui.screens.auth.LoginViewModel";

      static String com_crumbatelier_ui_screens_categories_CategoriesViewModel = "com.crumbatelier.ui.screens.categories.CategoriesViewModel";

      static String com_crumbatelier_ui_screens_detail_RecipeDetailViewModel = "com.crumbatelier.ui.screens.detail.RecipeDetailViewModel";

      static String com_crumbatelier_ui_screens_auth_ResetPasswordViewModel = "com.crumbatelier.ui.screens.auth.ResetPasswordViewModel";

      static String com_crumbatelier_ui_screens_home_HomeViewModel = "com.crumbatelier.ui.screens.home.HomeViewModel";

      static String com_crumbatelier_ui_screens_auth_ForgotPasswordViewModel = "com.crumbatelier.ui.screens.auth.ForgotPasswordViewModel";

      static String com_crumbatelier_ui_screens_profile_ProfileViewModel = "com.crumbatelier.ui.screens.profile.ProfileViewModel";

      static String com_crumbatelier_ui_screens_favorites_FavoritesViewModel = "com.crumbatelier.ui.screens.favorites.FavoritesViewModel";

      static String com_crumbatelier_ui_screens_splash_SplashViewModel = "com.crumbatelier.ui.screens.splash.SplashViewModel";

      static String com_crumbatelier_ui_screens_auth_RegisterViewModel = "com.crumbatelier.ui.screens.auth.RegisterViewModel";

      @KeepFieldType
      AdminRecipeViewModel com_crumbatelier_ui_screens_admin_AdminRecipeViewModel2;

      @KeepFieldType
      LoginViewModel com_crumbatelier_ui_screens_auth_LoginViewModel2;

      @KeepFieldType
      CategoriesViewModel com_crumbatelier_ui_screens_categories_CategoriesViewModel2;

      @KeepFieldType
      RecipeDetailViewModel com_crumbatelier_ui_screens_detail_RecipeDetailViewModel2;

      @KeepFieldType
      ResetPasswordViewModel com_crumbatelier_ui_screens_auth_ResetPasswordViewModel2;

      @KeepFieldType
      HomeViewModel com_crumbatelier_ui_screens_home_HomeViewModel2;

      @KeepFieldType
      ForgotPasswordViewModel com_crumbatelier_ui_screens_auth_ForgotPasswordViewModel2;

      @KeepFieldType
      ProfileViewModel com_crumbatelier_ui_screens_profile_ProfileViewModel2;

      @KeepFieldType
      FavoritesViewModel com_crumbatelier_ui_screens_favorites_FavoritesViewModel2;

      @KeepFieldType
      SplashViewModel com_crumbatelier_ui_screens_splash_SplashViewModel2;

      @KeepFieldType
      RegisterViewModel com_crumbatelier_ui_screens_auth_RegisterViewModel2;
    }
  }

  private static final class ViewModelCImpl extends CrumbAtelierApp_HiltComponents.ViewModelC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<AdminRecipeViewModel> adminRecipeViewModelProvider;

    private Provider<CategoriesViewModel> categoriesViewModelProvider;

    private Provider<FavoritesViewModel> favoritesViewModelProvider;

    private Provider<ForgotPasswordViewModel> forgotPasswordViewModelProvider;

    private Provider<HomeViewModel> homeViewModelProvider;

    private Provider<LoginViewModel> loginViewModelProvider;

    private Provider<ProfileViewModel> profileViewModelProvider;

    private Provider<RecipeDetailViewModel> recipeDetailViewModelProvider;

    private Provider<RegisterViewModel> registerViewModelProvider;

    private Provider<ResetPasswordViewModel> resetPasswordViewModelProvider;

    private Provider<SplashViewModel> splashViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;

      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.adminRecipeViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.categoriesViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.favoritesViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.forgotPasswordViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.homeViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.loginViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
      this.profileViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 6);
      this.recipeDetailViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 7);
      this.registerViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 8);
      this.resetPasswordViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 9);
      this.splashViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 10);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(MapBuilder.<String, javax.inject.Provider<ViewModel>>newMapBuilder(11).put(LazyClassKeyProvider.com_crumbatelier_ui_screens_admin_AdminRecipeViewModel, ((Provider) adminRecipeViewModelProvider)).put(LazyClassKeyProvider.com_crumbatelier_ui_screens_categories_CategoriesViewModel, ((Provider) categoriesViewModelProvider)).put(LazyClassKeyProvider.com_crumbatelier_ui_screens_favorites_FavoritesViewModel, ((Provider) favoritesViewModelProvider)).put(LazyClassKeyProvider.com_crumbatelier_ui_screens_auth_ForgotPasswordViewModel, ((Provider) forgotPasswordViewModelProvider)).put(LazyClassKeyProvider.com_crumbatelier_ui_screens_home_HomeViewModel, ((Provider) homeViewModelProvider)).put(LazyClassKeyProvider.com_crumbatelier_ui_screens_auth_LoginViewModel, ((Provider) loginViewModelProvider)).put(LazyClassKeyProvider.com_crumbatelier_ui_screens_profile_ProfileViewModel, ((Provider) profileViewModelProvider)).put(LazyClassKeyProvider.com_crumbatelier_ui_screens_detail_RecipeDetailViewModel, ((Provider) recipeDetailViewModelProvider)).put(LazyClassKeyProvider.com_crumbatelier_ui_screens_auth_RegisterViewModel, ((Provider) registerViewModelProvider)).put(LazyClassKeyProvider.com_crumbatelier_ui_screens_auth_ResetPasswordViewModel, ((Provider) resetPasswordViewModelProvider)).put(LazyClassKeyProvider.com_crumbatelier_ui_screens_splash_SplashViewModel, ((Provider) splashViewModelProvider)).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return Collections.<Class<?>, Object>emptyMap();
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_crumbatelier_ui_screens_detail_RecipeDetailViewModel = "com.crumbatelier.ui.screens.detail.RecipeDetailViewModel";

      static String com_crumbatelier_ui_screens_auth_ForgotPasswordViewModel = "com.crumbatelier.ui.screens.auth.ForgotPasswordViewModel";

      static String com_crumbatelier_ui_screens_admin_AdminRecipeViewModel = "com.crumbatelier.ui.screens.admin.AdminRecipeViewModel";

      static String com_crumbatelier_ui_screens_profile_ProfileViewModel = "com.crumbatelier.ui.screens.profile.ProfileViewModel";

      static String com_crumbatelier_ui_screens_splash_SplashViewModel = "com.crumbatelier.ui.screens.splash.SplashViewModel";

      static String com_crumbatelier_ui_screens_auth_LoginViewModel = "com.crumbatelier.ui.screens.auth.LoginViewModel";

      static String com_crumbatelier_ui_screens_auth_RegisterViewModel = "com.crumbatelier.ui.screens.auth.RegisterViewModel";

      static String com_crumbatelier_ui_screens_auth_ResetPasswordViewModel = "com.crumbatelier.ui.screens.auth.ResetPasswordViewModel";

      static String com_crumbatelier_ui_screens_categories_CategoriesViewModel = "com.crumbatelier.ui.screens.categories.CategoriesViewModel";

      static String com_crumbatelier_ui_screens_home_HomeViewModel = "com.crumbatelier.ui.screens.home.HomeViewModel";

      static String com_crumbatelier_ui_screens_favorites_FavoritesViewModel = "com.crumbatelier.ui.screens.favorites.FavoritesViewModel";

      @KeepFieldType
      RecipeDetailViewModel com_crumbatelier_ui_screens_detail_RecipeDetailViewModel2;

      @KeepFieldType
      ForgotPasswordViewModel com_crumbatelier_ui_screens_auth_ForgotPasswordViewModel2;

      @KeepFieldType
      AdminRecipeViewModel com_crumbatelier_ui_screens_admin_AdminRecipeViewModel2;

      @KeepFieldType
      ProfileViewModel com_crumbatelier_ui_screens_profile_ProfileViewModel2;

      @KeepFieldType
      SplashViewModel com_crumbatelier_ui_screens_splash_SplashViewModel2;

      @KeepFieldType
      LoginViewModel com_crumbatelier_ui_screens_auth_LoginViewModel2;

      @KeepFieldType
      RegisterViewModel com_crumbatelier_ui_screens_auth_RegisterViewModel2;

      @KeepFieldType
      ResetPasswordViewModel com_crumbatelier_ui_screens_auth_ResetPasswordViewModel2;

      @KeepFieldType
      CategoriesViewModel com_crumbatelier_ui_screens_categories_CategoriesViewModel2;

      @KeepFieldType
      HomeViewModel com_crumbatelier_ui_screens_home_HomeViewModel2;

      @KeepFieldType
      FavoritesViewModel com_crumbatelier_ui_screens_favorites_FavoritesViewModel2;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.crumbatelier.ui.screens.admin.AdminRecipeViewModel 
          return (T) new AdminRecipeViewModel(singletonCImpl.recipeRepositoryProvider.get(), singletonCImpl.authRepositoryProvider.get(), singletonCImpl.categoryRepositoryProvider.get());

          case 1: // com.crumbatelier.ui.screens.categories.CategoriesViewModel 
          return (T) new CategoriesViewModel(singletonCImpl.categoryRepositoryProvider.get(), singletonCImpl.authRepositoryProvider.get());

          case 2: // com.crumbatelier.ui.screens.favorites.FavoritesViewModel 
          return (T) new FavoritesViewModel(singletonCImpl.favoriteRepositoryProvider.get(), singletonCImpl.authRepositoryProvider.get());

          case 3: // com.crumbatelier.ui.screens.auth.ForgotPasswordViewModel 
          return (T) new ForgotPasswordViewModel(singletonCImpl.authRepositoryProvider.get());

          case 4: // com.crumbatelier.ui.screens.home.HomeViewModel 
          return (T) new HomeViewModel(singletonCImpl.recipeRepositoryProvider.get(), singletonCImpl.favoriteRepositoryProvider.get(), singletonCImpl.authRepositoryProvider.get());

          case 5: // com.crumbatelier.ui.screens.auth.LoginViewModel 
          return (T) new LoginViewModel(singletonCImpl.authRepositoryProvider.get());

          case 6: // com.crumbatelier.ui.screens.profile.ProfileViewModel 
          return (T) new ProfileViewModel(singletonCImpl.authRepositoryProvider.get());

          case 7: // com.crumbatelier.ui.screens.detail.RecipeDetailViewModel 
          return (T) new RecipeDetailViewModel(singletonCImpl.recipeRepositoryProvider.get(), singletonCImpl.commentRepositoryProvider.get(), singletonCImpl.favoriteRepositoryProvider.get(), singletonCImpl.authRepositoryProvider.get());

          case 8: // com.crumbatelier.ui.screens.auth.RegisterViewModel 
          return (T) new RegisterViewModel(singletonCImpl.authRepositoryProvider.get());

          case 9: // com.crumbatelier.ui.screens.auth.ResetPasswordViewModel 
          return (T) new ResetPasswordViewModel(singletonCImpl.authRepositoryProvider.get());

          case 10: // com.crumbatelier.ui.screens.splash.SplashViewModel 
          return (T) new SplashViewModel(singletonCImpl.provideSupabaseClientProvider.get(), singletonCImpl.appSessionManagerProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends CrumbAtelierApp_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends CrumbAtelierApp_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }
  }

  private static final class SingletonCImpl extends CrumbAtelierApp_HiltComponents.SingletonC {
    private final SingletonCImpl singletonCImpl = this;

    private Provider<SupabaseClient> provideSupabaseClientProvider;

    private Provider<CoroutineScope> provideApplicationScopeProvider;

    private Provider<AppSessionManager> appSessionManagerProvider;

    private Provider<RecipeRepository> recipeRepositoryProvider;

    private Provider<AuthRepository> authRepositoryProvider;

    private Provider<CategoryRepository> categoryRepositoryProvider;

    private Provider<FavoriteRepository> favoriteRepositoryProvider;

    private Provider<CommentRepository> commentRepositoryProvider;

    private SingletonCImpl() {

      initialize();

    }

    @SuppressWarnings("unchecked")
    private void initialize() {
      this.provideSupabaseClientProvider = DoubleCheck.provider(new SwitchingProvider<SupabaseClient>(singletonCImpl, 1));
      this.provideApplicationScopeProvider = DoubleCheck.provider(new SwitchingProvider<CoroutineScope>(singletonCImpl, 2));
      this.appSessionManagerProvider = DoubleCheck.provider(new SwitchingProvider<AppSessionManager>(singletonCImpl, 0));
      this.recipeRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<RecipeRepository>(singletonCImpl, 3));
      this.authRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<AuthRepository>(singletonCImpl, 4));
      this.categoryRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<CategoryRepository>(singletonCImpl, 5));
      this.favoriteRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<FavoriteRepository>(singletonCImpl, 6));
      this.commentRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<CommentRepository>(singletonCImpl, 7));
    }

    @Override
    public void injectCrumbAtelierApp(CrumbAtelierApp arg0) {
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return Collections.<Boolean>emptySet();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.crumbatelier.util.AppSessionManager 
          return (T) new AppSessionManager(singletonCImpl.provideSupabaseClientProvider.get(), singletonCImpl.provideApplicationScopeProvider.get());

          case 1: // io.github.jan.supabase.SupabaseClient 
          return (T) AppModule_ProvideSupabaseClientFactory.provideSupabaseClient();

          case 2: // @com.crumbatelier.util.ApplicationScope kotlinx.coroutines.CoroutineScope 
          return (T) AppModule_ProvideApplicationScopeFactory.provideApplicationScope();

          case 3: // com.crumbatelier.data.repository.RecipeRepository 
          return (T) new RecipeRepository(singletonCImpl.provideSupabaseClientProvider.get(), singletonCImpl.provideApplicationScopeProvider.get());

          case 4: // com.crumbatelier.data.repository.AuthRepository 
          return (T) new AuthRepository(singletonCImpl.provideSupabaseClientProvider.get(), singletonCImpl.appSessionManagerProvider.get());

          case 5: // com.crumbatelier.data.repository.CategoryRepository 
          return (T) new CategoryRepository(singletonCImpl.provideSupabaseClientProvider.get(), singletonCImpl.provideApplicationScopeProvider.get());

          case 6: // com.crumbatelier.data.repository.FavoriteRepository 
          return (T) new FavoriteRepository(singletonCImpl.provideSupabaseClientProvider.get(), singletonCImpl.recipeRepositoryProvider.get(), singletonCImpl.provideApplicationScopeProvider.get());

          case 7: // com.crumbatelier.data.repository.CommentRepository 
          return (T) new CommentRepository(singletonCImpl.provideSupabaseClientProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
