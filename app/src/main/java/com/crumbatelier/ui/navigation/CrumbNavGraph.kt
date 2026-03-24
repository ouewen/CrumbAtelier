package com.crumbatelier.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.crumbatelier.data.model.UserRole
import com.crumbatelier.ui.screens.admin.AdminRecipeScreen
import com.crumbatelier.ui.screens.auth.ForgotPasswordScreen
import com.crumbatelier.ui.screens.auth.LoginScreen
import com.crumbatelier.ui.screens.auth.RegisterScreen
import com.crumbatelier.ui.screens.auth.ResetPasswordScreen
import com.crumbatelier.ui.screens.categories.CategoriesScreen
import com.crumbatelier.ui.screens.detail.RecipeDetailScreen
import com.crumbatelier.ui.screens.favorites.FavoritesScreen
import com.crumbatelier.ui.screens.home.HomeScreen
import com.crumbatelier.ui.screens.profile.ProfileScreen
import com.crumbatelier.ui.screens.splash.SplashScreen
import com.crumbatelier.util.AppSession

@Composable
fun CrumbNavGraph(navController: NavHostController, session: AppSession?) {

    fun toHome(categoryName: String? = null) {
        val route = if (categoryName != null)
            "${Screen.Home.route}?category=${categoryName}"
        else
            Screen.Home.route
        navController.navigate(route) { popUpTo(0) { inclusive = true } }
    }
    fun toLogin() = navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } }

    NavHost(navController = navController, startDestination = Screen.Splash.route) {

        // ── Splash ────────────────────────────────────────────────────
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToHome  = { toHome() },
                onNavigateToLogin = { toLogin() }
            )
        }

        // ── Auth ──────────────────────────────────────────────────────
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess       = { toHome() },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onForgotPassword     = { navController.navigate(Screen.ForgotPassword.route) }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = { toHome() },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.ResetPassword.route) {
            ResetPasswordScreen(
                onSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ── Home (with optional category filter) ──────────────────────
        composable(
            route = "${Screen.Home.route}?category={category}",
            arguments = listOf(navArgument("category") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { bs ->
            val categoryFilter = bs.arguments?.getString("category")
            HomeScreen(
                initialCategoryFilter = categoryFilter,
                onRecipeClick         = { navController.navigate(Screen.RecipeDetail.createRoute(it)) },
                onFavoritesClick      = { navController.navigate(Screen.Favorites.route) },
                onAddRecipeClick      = { navController.navigate(Screen.AdminAddRecipe.route) },
                onCategoriesClick     = { navController.navigate(Screen.Categories.route) },
                onProfileClick        = { navController.navigate(Screen.Profile.route) },
                onLogout              = { toLogin() }
            )
        }

        // ── Recipe detail ─────────────────────────────────────────────
        composable(
            Screen.RecipeDetail.route,
            arguments = listOf(navArgument("recipeId") { type = NavType.LongType })
        ) { bs ->
            val id = bs.arguments?.getLong("recipeId") ?: return@composable
            RecipeDetailScreen(
                recipeId    = id,
                onBack      = { navController.popBackStack() },
                onEditClick = { navController.navigate(Screen.AdminEditRecipe.createRoute(id)) }
            )
        }

        // ── Admin: add recipe ─────────────────────────────────────────
        composable(Screen.AdminAddRecipe.route) {
            if (session?.role != UserRole.ADMIN) {
                LaunchedEffect(Unit) { toHome() }
                return@composable
            }
            AdminRecipeScreen(
                recipeId = null,
                onBack   = { navController.popBackStack() },
                onSaved  = { navController.popBackStack() }
            )
        }

        // ── Admin: edit recipe ────────────────────────────────────────
        composable(
            Screen.AdminEditRecipe.route,
            arguments = listOf(navArgument("recipeId") { type = NavType.LongType })
        ) { bs ->
            if (session?.role != UserRole.ADMIN) {
                LaunchedEffect(Unit) { toHome() }
                return@composable
            }
            val id = bs.arguments?.getLong("recipeId")
            AdminRecipeScreen(
                recipeId = id,
                onBack   = { navController.popBackStack() },
                onSaved  = { navController.popBackStack() }
            )
        }

        // ── Favorites ─────────────────────────────────────────────────
        composable(Screen.Favorites.route) {
            FavoritesScreen(
                onRecipeClick = { navController.navigate(Screen.RecipeDetail.createRoute(it)) },
                onBack        = { navController.popBackStack() }
            )
        }

        // ── Categories ────────────────────────────────────────────────
        composable(Screen.Categories.route) {
            CategoriesScreen(
                onBack           = { navController.popBackStack() },
                onCategoryClick  = { categoryName ->
                    navController.navigate("${Screen.Home.route}?category=${categoryName}") {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Profile ───────────────────────────────────────────────────
        composable(Screen.Profile.route) {
            ProfileScreen(
                onBack           = { navController.popBackStack() },
                onAccountDeleted = { toLogin() }
            )
        }
    }
}