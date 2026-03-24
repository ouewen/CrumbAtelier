package com.crumbatelier.data.remote.dto

import com.crumbatelier.data.model.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ── Profile ──────────────────────────────────────────────────
@Serializable
data class ProfileDto(
    val id: String,
    val name: String,
    val email: String,
    val role: String = "VIEWER",
    @SerialName("avatar_url")  val avatarUrl: String? = null,
    val bio: String? = null,
    @SerialName("is_active")   val isActive: Boolean = true,
    @SerialName("deleted_at")  val deletedAt: String? = null,
    @SerialName("delete_type") val deleteType: String? = null,
    @SerialName("created_at")  val createdAt: String? = null
) {
    fun toDomain(): User = User(
        id = id, name = name, email = email,
        role = runCatching { UserRole.valueOf(role) }.getOrDefault(UserRole.VIEWER),
        avatarUrl = avatarUrl, bio = bio, isActive = isActive
    )
}

// ── Recipe ────────────────────────────────────────────────────
@Serializable
data class RecipeDto(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val ingredients: String,
    val steps: String,
    @SerialName("image_url")  val imageUrl: String? = null,
    @SerialName("prep_time")  val prepTime: Int? = null,
    @SerialName("cook_time")  val cookTime: Int? = null,
    val servings: Int? = null,
    val difficulty: String? = null,
    @SerialName("created_by") val createdBy: String? = null,
    @SerialName("created_at") val createdAt: String? = null
) {
    fun toDomain(isFavorited: Boolean = false) = Recipe(
        id = id, title = title, description = description,
        ingredients = ingredients, steps = steps, imageUrl = imageUrl,
        prepTime = prepTime, cookTime = cookTime, servings = servings,
        difficulty = difficulty?.let { runCatching { Difficulty.valueOf(it) }.getOrNull() },
        createdBy = createdBy, createdAt = createdAt, isFavorited = isFavorited
    )
}

@Serializable
data class RecipePaginatedRow(
    val id: Long, val title: String, val description: String = "",
    val ingredients: String, val steps: String,
    @SerialName("image_url")    val imageUrl: String? = null,
    @SerialName("prep_time")    val prepTime: Int? = null,
    @SerialName("cook_time")    val cookTime: Int? = null,
    val servings: Int? = null, val difficulty: String? = null,
    @SerialName("created_by")   val createdBy: String? = null,
    @SerialName("created_at")   val createdAt: String? = null,
    @SerialName("is_favorited") val isFavorited: Boolean = false,
    @SerialName("total_count")  val totalCount: Long = 0
) {
    fun toDomain() = Recipe(
        id = id, title = title, description = description,
        ingredients = ingredients, steps = steps, imageUrl = imageUrl,
        prepTime = prepTime, cookTime = cookTime, servings = servings,
        difficulty = difficulty?.let { runCatching { Difficulty.valueOf(it) }.getOrNull() },
        createdBy = createdBy, createdAt = createdAt, isFavorited = isFavorited
    )
}

@Serializable
data class RecipeInsertDto(
    val title: String, val description: String, val ingredients: String, val steps: String,
    @SerialName("image_url")  val imageUrl: String? = null,
    @SerialName("prep_time")  val prepTime: Int? = null,
    @SerialName("cook_time")  val cookTime: Int? = null,
    val servings: Int? = null, val difficulty: String? = null,
    @SerialName("created_by") val createdBy: String
)

// ── Favorite ──────────────────────────────────────────────────
@Serializable
data class FavoriteDto(
    val id: Long = 0,
    @SerialName("user_id")   val userId: String,
    @SerialName("recipe_id") val recipeId: Long
)

@Serializable
data class FavoriteInsertDto(
    @SerialName("user_id")   val userId: String,
    @SerialName("recipe_id") val recipeId: Long
)

// ── Category ──────────────────────────────────────────────────
@Serializable
data class CategoryDto(
    val id: Long = 0, val name: String,
    val description: String? = null, val icon: String? = null,
    @SerialName("created_at") val createdAt: String? = null
) {
    fun toDomain() = Category(id = id, name = name, description = description, icon = icon)
}

@Serializable
data class CategoryPaginatedRow(
    val id: Long, val name: String,
    val description: String? = null, val icon: String? = null,
    @SerialName("created_at")   val createdAt: String? = null,
    @SerialName("recipe_count") val recipeCount: Long = 0,
    @SerialName("total_count")  val totalCount: Long = 0
) {
    fun toDomain() = Category(id = id, name = name, description = description, icon = icon, recipeCount = recipeCount)
}

@Serializable
data class CategoryInsertDto(
    val name: String, val description: String? = null, val icon: String? = null,
    @SerialName("created_by") val createdBy: String? = null
)

// ── Comment ───────────────────────────────────────────────────
@Serializable
data class CommentDto(
    val id: Long = 0,
    @SerialName("recipe_id")  val recipeId: Long,
    @SerialName("user_id")    val userId: String,
    val content: String,
    val rating: Int? = null,
    @SerialName("parent_id")  val parentId: Long? = null,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class CommentPaginatedRow(
    val id: Long,
    val content: String,
    val rating: Int? = null,
    @SerialName("created_at")  val createdAt: String? = null,
    @SerialName("user_id")     val userId: String,
    @SerialName("user_name")   val userName: String,
    @SerialName("user_avatar") val userAvatar: String? = null,
    @SerialName("parent_id")   val parentId: Long? = null,
    @SerialName("total_count") val totalCount: Long = 0
) {
    fun toDomain() = Comment(
        id = id, content = content, rating = rating,
        createdAt = createdAt, userId = userId,
        userName = userName, userAvatar = userAvatar,
        parentId = parentId
    )
}

@Serializable
data class CommentInsertDto(
    @SerialName("recipe_id") val recipeId: Long,
    @SerialName("user_id")   val userId: String,
    val content: String,
    val rating: Int? = null,
    @SerialName("parent_id") val parentId: Long? = null
)

// ── RPC params ────────────────────────────────────────────────
@Serializable
data class DeleteAccountParams(
    @SerialName("p_user_id")     val userId: String,
    @SerialName("p_delete_type") val deleteType: String
)

@Serializable
data class ToggleFavoriteParams(
    @SerialName("p_user_id")   val userId: String,
    @SerialName("p_recipe_id") val recipeId: Long
)

@Serializable
data class GetRecipesPaginatedParams(
    @SerialName("p_user_id")   val userId: String,
    @SerialName("p_page")      val page: Int = 1,
    @SerialName("p_page_size") val pageSize: Int = 10,
    @SerialName("p_query")     val query: String = ""
)

@Serializable
data class GetCategoriesPaginatedParams(
    @SerialName("p_page")      val page: Int = 1,
    @SerialName("p_page_size") val pageSize: Int = 10,
    @SerialName("p_query")     val query: String = ""
)

@Serializable
data class GetRecipeCommentsParams(
    @SerialName("p_recipe_id") val recipeId: Long,
    @SerialName("p_page")      val page: Int = 1,
    @SerialName("p_page_size") val pageSize: Int = 20
)