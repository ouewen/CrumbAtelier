package com.crumbatelier.data.model

enum class UserRole { ADMIN, VIEWER }
enum class Difficulty { Easy, Medium, Hard }

enum class DeleteType(val key: String, val label: String, val description: String) {
    SOFT("soft", "Anonymize Account", "Your profile is anonymized. Recipes you created remain in the archive."),
    DEACTIVATE("deactivate", "Deactivate Account", "Your account is suspended. An admin can reactivate it later."),
    HARD("hard", "Delete Everything", "All your data is permanently removed. This cannot be undone.")
}

data class User(val id: String, val name: String, val email: String, val role: UserRole, val avatarUrl: String? = null, val bio: String? = null, val isActive: Boolean = true)

data class Recipe(
    val id: Long = 0, val title: String, val description: String,
    val ingredients: String, val steps: String, val imageUrl: String? = null,
    val prepTime: Int? = null, val cookTime: Int? = null, val servings: Int? = null,
    val difficulty: Difficulty? = null, val createdBy: String? = null,
    val createdAt: String? = null, val isFavorited: Boolean = false
)

data class Category(val id: Long = 0, val name: String, val description: String? = null, val icon: String? = null, val recipeCount: Long = 0)

data class Comment(
    val id: Long = 0,
    val content: String,
    val rating: Int? = null,
    val createdAt: String? = null,
    val userId: String,
    val userName: String,
    val userAvatar: String? = null,
    val parentId: Long? = null       // null = top-level comment, non-null = reply
)

data class PaginatedResult<T>(
    val items: List<T>, val totalCount: Long, val currentPage: Int, val pageSize: Int
) {
    val totalPages: Int get() = ((totalCount + pageSize - 1) / pageSize).toInt()
    val hasNextPage: Boolean get() = currentPage < totalPages
    val hasPrevPage: Boolean get() = currentPage > 1
}