package com.crumbatelier.data.repository

import com.crumbatelier.data.model.PaginatedResult
import com.crumbatelier.data.model.Recipe
import com.crumbatelier.data.remote.dto.*
import com.crumbatelier.util.ApplicationScope
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.storage.storage
import io.ktor.http.ContentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecipeRepository @Inject constructor(
    private val supabase: SupabaseClient,
    @ApplicationScope private val appScope: CoroutineScope
) {
    private val _recipes     = MutableStateFlow<List<RecipeDto>>(emptyList())
    private val _favoriteIds = MutableStateFlow<Set<Long>>(emptySet())

    init {
        appScope.launch { fetchAll() }
        appScope.launch { subscribeRealtime() }
    }

    fun observeRecipes(userId: String): Flow<List<Recipe>> =
        _recipes.combine(_favoriteIds) { dtos, favIds ->
            dtos.map { it.toDomain(isFavorited = it.id in favIds) }
        }

    fun observeRecipesByQuery(query: String, userId: String): Flow<List<Recipe>> =
        observeRecipes(userId).map { list ->
            val q = query.lowercase()
            list.filter {
                it.title.lowercase().contains(q) ||
                        it.description.lowercase().contains(q)
            }
        }

    fun observeRecipeById(id: Long, userId: String): Flow<Recipe?> =
        observeRecipes(userId).map { it.firstOrNull { r -> r.id == id } }

    suspend fun getRecipesPaginated(
        userId: String,
        page: Int = 1,
        pageSize: Int = 10,
        query: String = ""
    ): Result<PaginatedResult<Recipe>> = runCatching {
        val params = buildJsonObject {
            put("p_user_id",   userId)
            put("p_page",      page)
            put("p_page_size", pageSize)
            put("p_query",     query)
        }
        val rows = supabase.postgrest.rpc("get_recipes_paginated", params)
            .decodeList<RecipePaginatedRow>()

        val totalCount = rows.firstOrNull()?.totalCount ?: 0L
        val favIds = _favoriteIds.value
        PaginatedResult(
            items       = rows.map { it.toDomain().copy(isFavorited = it.id in favIds) },
            totalCount  = totalCount,
            currentPage = page,
            pageSize    = pageSize
        )
    }

    suspend fun loadFavoriteIds(userId: String) {
        runCatching {
            supabase.from("favorites")
                .select { filter { eq("user_id", userId) } }
                .decodeList<FavoriteDto>()
                .map { it.recipeId }.toSet()
        }.onSuccess { _favoriteIds.value = it }
    }

    fun updateFavoriteCache(recipeId: Long, nowFavorited: Boolean) {
        _favoriteIds.update { if (nowFavorited) it + recipeId else it - recipeId }
    }

    suspend fun insertRecipe(recipe: Recipe, userId: String): Result<Unit> =
        runCatching {
            supabase.from("recipes").insert(
                RecipeInsertDto(
                    title       = recipe.title,
                    description = recipe.description,
                    ingredients = recipe.ingredients,
                    steps       = recipe.steps,
                    imageUrl    = recipe.imageUrl,
                    prepTime    = recipe.prepTime,
                    cookTime    = recipe.cookTime,
                    servings    = recipe.servings,
                    difficulty  = recipe.difficulty?.name,
                    createdBy   = userId
                )
            )
            fetchAll()
        }.map {}

    suspend fun updateRecipe(recipe: Recipe): Result<Unit> =
        runCatching {
            supabase.from("recipes").update(
                RecipeInsertDto(
                    title       = recipe.title,
                    description = recipe.description,
                    ingredients = recipe.ingredients,
                    steps       = recipe.steps,
                    imageUrl    = recipe.imageUrl,
                    prepTime    = recipe.prepTime,
                    cookTime    = recipe.cookTime,
                    servings    = recipe.servings,
                    difficulty  = recipe.difficulty?.name,
                    createdBy   = recipe.createdBy ?: ""
                )
            ) { filter { eq("id", recipe.id) } }
            fetchAll()
        }.map {}

    suspend fun deleteRecipe(id: Long): Result<Unit> =
        runCatching {
            supabase.from("recipes").delete { filter { eq("id", id) } }
            _recipes.update { it.filter { r -> r.id != id } }
        }.map {}

    suspend fun getLatestRecipeIdByUser(userId: String): Long? =
        runCatching {
            supabase.from("recipes")
                .select {
                    filter { eq("created_by", userId) }
                    order("created_at", order = Order.DESCENDING)
                    limit(1)
                }
                .decodeSingle<RecipeDto>()
                .id
        }.getOrNull()

    suspend fun uploadRecipeImage(
        imageBytes: ByteArray,
        fileName: String,
        mimeType: String = "image/jpeg"
    ): Result<String> = runCatching {
        val path = "recipes/$fileName"
        val type = ContentType.parse(mimeType)
        supabase.storage.from("recipe-images").upload(path, imageBytes) {
            upsert = true
            contentType = type
        }
        supabase.storage.from("recipe-images").publicUrl(path)
    }

    suspend fun deleteRecipeImage(imageUrl: String): Result<Unit> = runCatching {
        val path = imageUrl.substringAfter("recipe-images/")
        supabase.storage.from("recipe-images").delete(listOf(path))
    }

    private suspend fun fetchAll() {
        runCatching {
            supabase.from("recipes")
                .select { order("created_at", order = Order.DESCENDING) }
                .decodeList<RecipeDto>()
        }.onSuccess { _recipes.value = it }
    }

    private suspend fun subscribeRealtime() {
        val ch = supabase.channel("public:recipes")
        ch.postgresChangeFlow<PostgresAction>(schema = "public") { table = "recipes" }
            .onEach { fetchAll() }
            .launchIn(appScope)
        ch.subscribe()
    }
}