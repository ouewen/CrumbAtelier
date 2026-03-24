package com.crumbatelier.data.repository

import com.crumbatelier.data.model.Recipe
import com.crumbatelier.data.remote.dto.FavoriteDto
import com.crumbatelier.data.remote.dto.FavoriteInsertDto
import com.crumbatelier.data.remote.dto.RecipeDto
import com.crumbatelier.util.ApplicationScope
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteRepository @Inject constructor(
    private val supabase: SupabaseClient,
    private val recipeRepository: RecipeRepository,
    @ApplicationScope private val appScope: CoroutineScope
) {
    private val _favoriteRecipes = MutableStateFlow<List<RecipeDto>>(emptyList())

    fun observeFavoriteRecipes(userId: String): Flow<List<Recipe>> =
        _favoriteRecipes.combine(recipeRepository.observeRecipes(userId)) { favDtos, allRecipes ->
            val ids = favDtos.map { it.id }.toSet()
            allRecipes.filter { it.id in ids }.map { it.copy(isFavorited = true) }
        }

    suspend fun loadFavorites(userId: String) {
        recipeRepository.loadFavoriteIds(userId)
        fetchFavoriteRecipes(userId)
        subscribeFavoriteRealtime(userId)
    }

    suspend fun toggleFavorite(
        userId: String,
        recipeId: Long,
        currentlyFavorited: Boolean
    ): Result<Unit> {
        recipeRepository.updateFavoriteCache(recipeId, !currentlyFavorited)
        val result = if (currentlyFavorited)
            removeFavorite(userId, recipeId)
        else
            addFavorite(userId, recipeId)
        if (result.isFailure) recipeRepository.updateFavoriteCache(recipeId, currentlyFavorited)
        return result
    }

    private suspend fun addFavorite(userId: String, recipeId: Long): Result<Unit> =
        runCatching {
            supabase.from("favorites").insert(FavoriteInsertDto(userId = userId, recipeId = recipeId))
            fetchFavoriteRecipes(userId)
        }.map {}

    private suspend fun removeFavorite(userId: String, recipeId: Long): Result<Unit> =
        runCatching {
            supabase.from("favorites").delete {
                filter { eq("user_id", userId); eq("recipe_id", recipeId) }
            }
            _favoriteRecipes.update { it.filter { r -> r.id != recipeId } }
        }.map {}

    private suspend fun fetchFavoriteRecipes(userId: String) {
        runCatching {
            val ids = supabase.from("favorites")
                .select { filter { eq("user_id", userId) } }
                .decodeList<FavoriteDto>().map { it.recipeId }
            if (ids.isEmpty()) { _favoriteRecipes.value = emptyList(); return }
            supabase.from("recipes")
                .select { filter { isIn("id", ids) } }
                .decodeList<RecipeDto>()
        }.onSuccess { _favoriteRecipes.value = it }
    }

    private fun subscribeFavoriteRealtime(userId: String) {
        appScope.launch {
            val ch = supabase.channel("public:favorites:$userId")
            ch.postgresChangeFlow<PostgresAction>(schema = "public") { table = "favorites" }
                .onEach { fetchFavoriteRecipes(userId) }
                .launchIn(appScope)
            ch.subscribe()
        }
    }
}