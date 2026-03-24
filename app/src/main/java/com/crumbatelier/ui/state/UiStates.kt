package com.crumbatelier.ui.state

import com.crumbatelier.data.model.Recipe

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class HomeUiState(
    val recipes: List<Recipe> = emptyList(),
    val searchQuery: String = "",
    val currentPage: Int = 1,
    val totalPages: Int = 1,
    val totalCount: Long = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class RecipeDetailUiState(
    val recipe: Recipe? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class AdminRecipeUiState(
    val title: String = "",
    val description: String = "",
    val ingredients: String = "",
    val steps: String = "",
    val imageUrl: String = "",
    val isEditMode: Boolean = false,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val errorMessage: String? = null
)

data class FavoritesUiState(
    val recipes: List<Recipe> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)