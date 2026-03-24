package com.crumbatelier.ui.screens.favorites

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.crumbatelier.data.repository.AuthRepository
import com.crumbatelier.data.repository.FavoriteRepository
import com.crumbatelier.ui.components.*
import com.crumbatelier.ui.state.FavoritesUiState
import com.crumbatelier.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(private val favRepo: FavoriteRepository, private val authRepo: AuthRepository) : ViewModel() {
    private val _state = MutableStateFlow(FavoritesUiState(isLoading = true))
    val state = _state.asStateFlow()
    init {
        @OptIn(ExperimentalCoroutinesApi::class)
        authRepo.sessionFlow.filterNotNull()
            .flatMapLatest { s -> favRepo.observeFavoriteRecipes(s.userId) }
            .catch { e -> _state.update { it.copy(isLoading = false, errorMessage = e.message) } }
            .onEach { list -> _state.update { it.copy(recipes = list, isLoading = false) } }
            .launchIn(viewModelScope)
    }
}

@Composable
fun FavoritesScreen(onRecipeClick: (Long) -> Unit, onBack: () -> Unit, viewModel: FavoritesViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    Scaffold(topBar = { CrumbTopBar("My Favourites", onBack = onBack) }, containerColor = CreamBackground) { pad ->
        when {
            state.isLoading -> LoadingContent(Modifier.padding(pad))
            state.errorMessage != null -> ErrorContent(state.errorMessage!!, Modifier.padding(pad))
            state.recipes.isEmpty() -> Box(Modifier.fillMaxSize().padding(pad), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("🤍", style = MaterialTheme.typography.displayMedium)
                    Text("No favourites yet", style = MaterialTheme.typography.titleMedium, color = MutedText)
                    Text("Tap the heart on any recipe to save it here", style = MaterialTheme.typography.bodyMedium, color = SubtleText)
                }
            }
            else -> LazyColumn(Modifier.padding(pad), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                item { Text("${state.recipes.size} saved recipe${if (state.recipes.size != 1) "s" else ""}", style = MaterialTheme.typography.bodyMedium, color = MutedText) }
                items(state.recipes, key = { it.id }) { RecipeCard(it, onClick = { onRecipeClick(it.id) }) }
            }
        }
    }
}
