package com.crumbatelier.data.repository

import com.crumbatelier.data.model.DeleteType
import com.crumbatelier.util.AppSession
import com.crumbatelier.util.AppSessionManager
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val supabase: SupabaseClient,
    private val sessionManager: AppSessionManager
) {
    val sessionFlow: StateFlow<AppSession?> = sessionManager.session

    suspend fun login(email: String, password: String): Result<Unit> =
        runCatching {
            supabase.auth.signInWith(Email) {
                this.email    = email.trim()
                this.password = password
            }
        }.map { }.friendlyError()

    suspend fun register(name: String, email: String, password: String): Result<Unit> =
        runCatching {
            supabase.auth.signUpWith(Email) {
                this.email    = email.trim()
                this.password = password
                data = buildJsonObject {
                    put("name", name.trim())
                    put("role", "VIEWER")
                }
            }
        }.map { }.friendlyError()

    suspend fun logout(): Result<Unit> =
        runCatching { supabase.auth.signOut() }.friendlyError()

    suspend fun sendPasswordReset(email: String): Result<Unit> =
        runCatching {
            supabase.auth.resetPasswordForEmail(
                email       = email.trim(),
                redirectUrl = "crumbatelier://reset-password"
            )
        }.map { }.friendlyError()

    suspend fun updatePassword(newPassword: String): Result<Unit> =
        runCatching {
            supabase.auth.updateUser { password = newPassword }
        }.map { }.friendlyError()

    suspend fun updateProfile(name: String, bio: String): Result<Unit> =
        runCatching {
            val userId = supabase.auth.currentUserOrNull()?.id ?: error("Not logged in")
            supabase.from("profiles").update(
                mapOf("name" to name, "bio" to bio)
            ) { filter { eq("id", userId) } }
            sessionManager.refreshProfile()
        }.map { }.friendlyError()

    suspend fun deleteAccount(userId: String, deleteType: DeleteType): Result<Unit> =
        runCatching {
            if (deleteType == DeleteType.HARD) {
                // Hard delete via security definer RPC — can delete from auth.users
                val params = buildJsonObject { put("p_user_id", userId) }
                supabase.postgrest.rpc("hard_delete_account", params)
            } else {
                // Soft / deactivate — handled by existing RPC
                val params = buildJsonObject {
                    put("p_user_id",     userId)
                    put("p_delete_type", deleteType.key)
                }
                supabase.postgrest.rpc("delete_account", params)
            }
            supabase.auth.signOut()
        }.map { }.friendlyError()
}

private fun <T> Result<T>.friendlyError(): Result<T> = mapFailure { t ->
    val msg = t.message ?: ""
    Exception(when {
        "Invalid login credentials" in msg -> "Incorrect email or password."
        "User already registered"   in msg -> "An account with this email already exists."
        "Email not confirmed"       in msg -> "Please verify your email before signing in."
        "Password should be"        in msg -> "Password must be at least 6 characters."
        "Unable to connect" in msg || "Network" in msg ->
            "Network error. Please check your connection."
        else -> msg.ifBlank { "An unexpected error occurred." }
    })
}

private fun <T> Result<T>.mapFailure(f: (Throwable) -> Throwable): Result<T> =
    if (isFailure) Result.failure(f(exceptionOrNull()!!)) else this