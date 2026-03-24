package com.crumbatelier.util

import com.crumbatelier.data.model.UserRole
import com.crumbatelier.data.remote.dto.ProfileDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppSessionManager @Inject constructor(
    private val supabase: SupabaseClient,
    @ApplicationScope private val appScope: CoroutineScope
) {
    private val _session = MutableStateFlow<AppSession?>(null)
    val session: StateFlow<AppSession?> = _session.asStateFlow()

    init { observeAuthState() }

    private fun observeAuthState() {
        appScope.launch {
            // Collect auth state changes without importing SessionStatus
            supabase.auth.sessionStatus.collect { status ->
                val className = status::class.simpleName ?: ""
                when {
                    className == "Authenticated" -> {
                        val uid = supabase.auth.currentUserOrNull()?.id ?: return@collect
                        fetchAndSetProfile(uid)
                    }
                    className == "NotAuthenticated" || className == "RefreshFailure" -> {
                        _session.value = null
                    }
                    else -> { /* Initializing — wait */ }
                }
            }
        }
    }

    private suspend fun fetchAndSetProfile(userId: String) {
        runCatching {
            supabase.from("profiles")
                .select { filter { eq("id", userId) } }
                .decodeSingle<ProfileDto>()
        }.onSuccess { profile ->
            // Block login for deactivated or soft-deleted accounts
            if (!profile.isActive || profile.deletedAt != null) {
                appScope.launch {
                    runCatching { supabase.auth.signOut() }
                }
                _session.value = null
                return
            }
            _session.value = AppSession(
                userId   = profile.id,
                userName = profile.name,
                email    = profile.email,
                role     = runCatching { UserRole.valueOf(profile.role) }
                    .getOrDefault(UserRole.VIEWER)
            )
        }.onFailure {
            val user = supabase.auth.currentUserOrNull()
            _session.value = AppSession(
                userId   = userId,
                userName = user?.userMetadata?.get("name")
                    ?.toString()?.trim('"') ?: "User",
                email    = user?.email ?: "",
                role     = UserRole.VIEWER
            )
        }
    }

    suspend fun refreshProfile() {
        supabase.auth.currentUserOrNull()?.id?.let { fetchAndSetProfile(it) }
    }
}