package com.crumbatelier.ui.screens.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.crumbatelier.ui.theme.*
import com.crumbatelier.util.AppSessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SplashDestination {
    object None  : SplashDestination()
    object Home  : SplashDestination()
    object Login : SplashDestination()
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val supabase: SupabaseClient,
    private val sessionManager: AppSessionManager
) : ViewModel() {
    private val _dest = MutableStateFlow<SplashDestination>(SplashDestination.None)
    val destination = _dest.asStateFlow()

    init {
        viewModelScope.launch {
            delay(1_400)
            // Check current session directly instead of collecting SessionStatus
            val currentSession = supabase.auth.currentSessionOrNull()
            if (currentSession != null) {
                _dest.value = SplashDestination.Home
            } else {
                _dest.value = SplashDestination.Login
            }
        }
    }
}

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val dest by viewModel.destination.collectAsStateWithLifecycle()
    LaunchedEffect(dest) {
        when (dest) {
            is SplashDestination.Home  -> onNavigateToHome()
            is SplashDestination.Login -> onNavigateToLogin()
            else -> Unit
        }
    }
    Box(
        Modifier.fillMaxSize().background(ChocolateBrown),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("✦", style = MaterialTheme.typography.displayLarge, color = CaramelLight)
            Spacer(Modifier.height(8.dp))
            Text("Crumb Atelier", style = MaterialTheme.typography.displayMedium, color = LightTextOnDark)
            Text("An Artisan Baking Archive", style = MaterialTheme.typography.bodyMedium, color = SubtleText, textAlign = TextAlign.Center)
        }
    }
}