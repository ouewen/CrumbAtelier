package com.crumbatelier.data.repository

import com.crumbatelier.data.model.Category
import com.crumbatelier.data.model.PaginatedResult
import com.crumbatelier.data.remote.dto.*
import com.crumbatelier.util.ApplicationScope
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
data class RecipeCategoryRow(
    @SerialName("category_id") val categoryId: Long
)

@Singleton
class CategoryRepository @Inject constructor(
    private val supabase: SupabaseClient,
    @ApplicationScope private val appScope: CoroutineScope
) {
    private val _categories = MutableStateFlow<List<CategoryDto>>(emptyList())

    init {
        appScope.launch { fetchAll() }
        appScope.launch { subscribeRealtime() }
    }

    fun observeCategories(): Flow<List<Category>> =
        _categories.map { it.map { dto -> dto.toDomain() } }

    suspend fun getCategoriesPaginated(
        page: Int = 1,
        pageSize: Int = 10,
        query: String = ""
    ): Result<PaginatedResult<Category>> = runCatching {
        val params = buildJsonObject {
            put("p_page",      page)
            put("p_page_size", pageSize)
            put("p_query",     query)
        }
        val rows = supabase.postgrest.rpc("get_categories_paginated", params)
            .decodeList<CategoryPaginatedRow>()
        val totalCount = rows.firstOrNull()?.totalCount ?: 0L
        PaginatedResult(
            items       = rows.map { it.toDomain() },
            totalCount  = totalCount,
            currentPage = page,
            pageSize    = pageSize
        )
    }

    suspend fun getRecipeCategoryIds(recipeId: Long): Result<List<Long>> =
        runCatching {
            supabase.from("recipe_categories")
                .select { filter { eq("recipe_id", recipeId) } }
                .decodeList<RecipeCategoryRow>()
                .map { it.categoryId }
        }

    suspend fun insertCategory(category: Category, userId: String): Result<Unit> =
        runCatching {
            supabase.from("categories").insert(
                CategoryInsertDto(
                    name        = category.name,
                    description = category.description,
                    icon        = category.icon,
                    createdBy   = userId
                )
            )
            fetchAll()
        }.map {}

    suspend fun updateCategory(category: Category): Result<Unit> =
        runCatching {
            supabase.from("categories").update(
                CategoryInsertDto(name = category.name, description = category.description, icon = category.icon)
            ) { filter { eq("id", category.id) } }
            fetchAll()
        }.map {}

    suspend fun deleteCategory(id: Long): Result<Unit> =
        runCatching {
            supabase.from("categories").delete { filter { eq("id", id) } }
            _categories.update { it.filter { c -> c.id != id } }
        }.map {}

    suspend fun setRecipeCategories(recipeId: Long, categoryIds: List<Long>): Result<Unit> =
        runCatching {
            supabase.from("recipe_categories").delete { filter { eq("recipe_id", recipeId) } }
            if (categoryIds.isNotEmpty()) {
                supabase.from("recipe_categories").insert(
                    categoryIds.map { mapOf("recipe_id" to recipeId, "category_id" to it) }
                )
            }
        }.map {}

    private suspend fun fetchAll() {
        runCatching {
            supabase.from("categories").select().decodeList<CategoryDto>()
        }.onSuccess { _categories.value = it }
    }

    private suspend fun subscribeRealtime() {
        val ch = supabase.channel("public:categories")
        ch.postgresChangeFlow<PostgresAction>(schema = "public") { table = "categories" }
            .onEach { fetchAll() }
            .launchIn(appScope)
        ch.subscribe()
    }
}