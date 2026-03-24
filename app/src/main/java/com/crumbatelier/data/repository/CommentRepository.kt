package com.crumbatelier.data.repository

import com.crumbatelier.data.model.Comment
import com.crumbatelier.data.model.PaginatedResult
import com.crumbatelier.data.remote.dto.CommentDto
import com.crumbatelier.data.remote.dto.CommentInsertDto
import com.crumbatelier.data.remote.dto.CommentPaginatedRow
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommentRepository @Inject constructor(
    private val supabase: SupabaseClient
) {
    // Get top-level comments for a recipe (parent_id IS NULL)
    suspend fun getCommentsPaginated(
        recipeId: Long,
        page: Int = 1,
        pageSize: Int = 20
    ): Result<PaginatedResult<Comment>> = runCatching {
        val params = buildJsonObject {
            put("p_recipe_id", recipeId)
            put("p_page",      page)
            put("p_page_size", pageSize)
        }
        val rows = supabase.postgrest.rpc("get_recipe_comments", params)
            .decodeList<CommentPaginatedRow>()
        val totalCount = rows.firstOrNull()?.totalCount ?: 0L
        PaginatedResult(items = rows.map { it.toDomain() }, totalCount = totalCount, currentPage = page, pageSize = pageSize)
    }

    // Get replies for a specific comment
    suspend fun getReplies(
        parentId: Long,
        page: Int = 1,
        pageSize: Int = 5
    ): Result<PaginatedResult<Comment>> = runCatching {
        val params = buildJsonObject {
            put("p_parent_id", parentId)
            put("p_page",      page)
            put("p_page_size", pageSize)
        }
        val rows = supabase.postgrest.rpc("get_comment_replies", params)
            .decodeList<CommentPaginatedRow>()
        val totalCount = rows.firstOrNull()?.totalCount ?: 0L
        PaginatedResult(items = rows.map { it.toDomain() }, totalCount = totalCount, currentPage = page, pageSize = pageSize)
    }

    suspend fun addComment(recipeId: Long, userId: String, content: String, rating: Int? = null, parentId: Long? = null): Result<Unit> =
        runCatching {
            supabase.from("comments").insert(
                CommentInsertDto(recipeId = recipeId, userId = userId, content = content, rating = rating, parentId = parentId)
            )
        }.map {}

    suspend fun updateComment(commentId: Long, content: String, rating: Int?): Result<Unit> =
        runCatching {
            supabase.from("comments").update(
                mapOf("content" to content, "rating" to rating)
            ) { filter { eq("id", commentId) } }
        }.map {}

    suspend fun deleteComment(commentId: Long): Result<Unit> =
        runCatching {
            supabase.from("comments").delete { filter { eq("id", commentId) } }
        }.map {}

    // Count replies for a comment
    suspend fun countReplies(parentId: Long): Long =
        runCatching {
            supabase.from("comments")
                .select { filter { eq("parent_id", parentId) } }
                .decodeList<CommentDto>()
                .size.toLong()
        }.getOrDefault(0L)
}