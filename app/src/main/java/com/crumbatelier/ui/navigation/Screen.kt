package com.crumbatelier.ui.navigation

sealed class Screen(val route: String) {
    object Splash          : Screen("splash")
    object Login           : Screen("login")
    object Register        : Screen("register")
    object Home            : Screen("home")
    object Favorites       : Screen("favorites")
    object RecipeDetail    : Screen("recipe_detail/{recipeId}") {
        fun createRoute(id: Long) = "recipe_detail/$id"
    }
    object AdminAddRecipe  : Screen("admin_add_recipe")
    object AdminEditRecipe : Screen("admin_edit_recipe/{recipeId}") {
        fun createRoute(id: Long) = "admin_edit_recipe/$id"
    }

    object ForgotPassword : Screen("forgot_password")
    object Categories     : Screen("categories")
    object Profile        : Screen("profile")

    object ResetPassword : Screen("reset_password")

}
