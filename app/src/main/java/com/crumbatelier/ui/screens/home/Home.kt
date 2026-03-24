package com.crumbatelier.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.crumbatelier.data.model.Recipe
import com.crumbatelier.data.model.UserRole
import com.crumbatelier.data.repository.AuthRepository
import com.crumbatelier.data.repository.FavoriteRepository
import com.crumbatelier.data.repository.RecipeRepository
import com.crumbatelier.ui.components.*
import com.crumbatelier.ui.screens.categories.PaginationControls
import com.crumbatelier.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val recipes: List<Recipe> = emptyList(),
    val searchQuery: String = "",
    val categoryFilter: String? = null,
    val currentPage: Int = 1,
    val totalPages: Int = 1,
    val totalCount: Long = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val recipeRepo: RecipeRepository,
    private val favoriteRepo: FavoriteRepository,
    private val authRepo: AuthRepository
) : ViewModel() {
    private val _state = MutableStateFlow(HomeUiState(isLoading = true))
    val state = _state.asStateFlow()
    val session = authRepo.sessionFlow.stateIn(viewModelScope, SharingStarted.Eagerly, null)
    val isAdmin: Boolean get() = session.value?.role == UserRole.ADMIN

    private val _query = MutableStateFlow("")

    init {
        @OptIn(FlowPreview::class)
        viewModelScope.launch {
            val s = session.filterNotNull().first()
            favoriteRepo.loadFavorites(s.userId)
            _query.debounce(300L).collect { q ->
                loadPage(1, q)
            }
        }
    }

    fun initWithCategory(categoryName: String?) {
        if (categoryName != null && _state.value.categoryFilter != categoryName) {
            _state.update { it.copy(categoryFilter = categoryName) }
            loadPage(1)
        }
    }

    fun onSearchQueryChange(q: String) {
        _query.value = q
        _state.update { it.copy(searchQuery = q) }
    }

    fun clearCategoryFilter() {
        _state.update { it.copy(categoryFilter = null) }
        loadPage(1)
    }

    fun loadPage(page: Int, query: String? = null) {
        val q = query ?: _state.value.searchQuery
        val categoryFilter = _state.value.categoryFilter
        val userId = session.value?.userId ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            // If category filter active, append to query
            val effectiveQuery = if (categoryFilter != null) categoryFilter else q
            recipeRepo.getRecipesPaginated(userId = userId, page = page, pageSize = 5, query = effectiveQuery)
                .onSuccess { result ->
                    _state.update { it.copy(
                        recipes      = result.items,
                        currentPage  = result.currentPage,
                        totalPages   = result.totalPages,
                        totalCount   = result.totalCount,
                        isLoading    = false,
                        errorMessage = null
                    )}
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
        }
    }

    fun logout() { viewModelScope.launch { authRepo.logout() } }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    initialCategoryFilter: String? = null,
    onRecipeClick: (Long) -> Unit,
    onFavoritesClick: () -> Unit,
    onAddRecipeClick: () -> Unit,
    onCategoriesClick: () -> Unit,
    onProfileClick: () -> Unit,
    onLogout: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state   by viewModel.state.collectAsStateWithLifecycle()
    val session by viewModel.session.collectAsStateWithLifecycle()

    // Apply initial category filter from nav argument
    LaunchedEffect(initialCategoryFilter) {
        viewModel.initWithCategory(initialCategoryFilter)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Crumb Atelier", style = MaterialTheme.typography.titleLarge, color = LightTextOnDark)
                        Text(session?.userName?.let { "Hello, $it" } ?: "", style = MaterialTheme.typography.bodySmall, color = SubtleText)
                    }
                },
                actions = {
                    IconButton(onFavoritesClick)  { Icon(Icons.Default.Favorite,  "Favourites", tint = CaramelLight) }
                    IconButton(onCategoriesClick) { Icon(Icons.Default.Category,  "Categories", tint = LightTextOnDark) }
                    IconButton(onProfileClick)    { Icon(Icons.Default.Person,    "Profile",    tint = LightTextOnDark) }
                    IconButton({ viewModel.logout(); onLogout() }) { Icon(Icons.Default.Logout, "Logout", tint = LightTextOnDark) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ChocolateBrown)
            )
        },
        floatingActionButton = {
            if (viewModel.isAdmin)
                FloatingActionButton(
                    onClick = onAddRecipeClick,
                    containerColor = CaramelBrown,
                    contentColor = LightTextOnDark,
                    shape = RoundedCornerShape(16.dp)
                ) { Icon(Icons.Default.Add, "Add recipe") }
        },
        containerColor = CreamBackground
    ) { pad ->
        Column(Modifier.fillMaxSize().padding(pad)) {

            // Search bar
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                placeholder = { Text("Search recipes…", color = MutedText) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = MutedText) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor    = CaramelBrown, unfocusedBorderColor    = DividerColor,
                    focusedContainerColor = CardSurface,  unfocusedContainerColor = CardSurface,
                    cursorColor = CaramelBrown, focusedTextColor = BrownText, unfocusedTextColor = BrownText
                )
            )

            // Active category filter chip
            if (state.categoryFilter != null) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Filtered by:", style = MaterialTheme.typography.bodySmall, color = MutedText)
                    InputChip(
                        selected = true,
                        onClick  = { viewModel.clearCategoryFilter() },
                        label    = { Text(state.categoryFilter!!, color = LightTextOnDark) },
                        trailingIcon = { Icon(Icons.Default.Close, "Clear filter", tint = LightTextOnDark, modifier = Modifier.size(16.dp)) },
                        colors = InputChipDefaults.inputChipColors(
                            selectedContainerColor = CaramelBrown
                        )
                    )
                }
            }

            when {
                state.isLoading -> LoadingContent()
                state.errorMessage != null -> ErrorContent(state.errorMessage!!)
                state.recipes.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("🫙", style = MaterialTheme.typography.displayMedium)
                        Text(
                            if (state.categoryFilter != null) "No recipes in \"${state.categoryFilter}\""
                            else if (state.searchQuery.isBlank()) "The archive is empty"
                            else "No results for \"${state.searchQuery}\"",
                            style = MaterialTheme.typography.titleMedium,
                            color = MutedText
                        )
                    }
                }
                else -> LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            if (state.categoryFilter != null) "${state.categoryFilter} — ${state.totalCount} recipe${if (state.totalCount != 1L) "s" else ""}"
                            else if (state.searchQuery.isBlank()) "All Recipes (${state.totalCount})"
                            else "\"${state.searchQuery}\" — ${state.totalCount} result${if (state.totalCount != 1L) "s" else ""}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MutedText
                        )
                    }

                    items(state.recipes, key = { it.id }) { recipe ->
                        RecipeCard(recipe, onClick = { onRecipeClick(recipe.id) })
                    }

                    if (state.totalPages > 1) {
                        item {
                            PaginationControls(
                                currentPage = state.currentPage,
                                totalPages  = state.totalPages,
                                onPrev      = { viewModel.loadPage(state.currentPage - 1) },
                                onNext      = { viewModel.loadPage(state.currentPage + 1) }
                            )
                        }
                    }

                    item { Spacer(Modifier.height(72.dp)) }
                }
            }
        }
    }
}