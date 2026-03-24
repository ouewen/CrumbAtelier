package com.crumbatelier.ui.screens.categories

import androidx.compose.foundation.clickable
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
import com.crumbatelier.data.model.Category
import com.crumbatelier.data.model.UserRole
import com.crumbatelier.data.repository.AuthRepository
import com.crumbatelier.data.repository.CategoryRepository
import com.crumbatelier.ui.components.*
import com.crumbatelier.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CategoriesUiState(
    val categories: List<Category> = emptyList(),
    val searchQuery: String = "",
    val currentPage: Int = 1,
    val totalPages: Int = 1,
    val totalCount: Long = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showEditDialog: Boolean = false,
    val editingCategory: Category? = null,
    val dialogName: String = "",
    val dialogDescription: String = "",
    val dialogIcon: String = "",
    val dialogError: String? = null,
    val isSaving: Boolean = false
)

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val categoryRepo: CategoryRepository,
    private val authRepo: AuthRepository
) : ViewModel() {
    private val _state = MutableStateFlow(CategoriesUiState(isLoading = true))
    val state = _state.asStateFlow()
    val session = authRepo.sessionFlow.stateIn(viewModelScope, SharingStarted.Eagerly, null)
    val isAdmin: Boolean get() = session.value?.role == UserRole.ADMIN

    private val _query = MutableStateFlow("")

    init {
        @OptIn(FlowPreview::class)
        _query.debounce(300L)
            .onEach { _state.update { it.copy(currentPage = 1) } }
            .launchIn(viewModelScope)
        loadPage(1)
    }

    fun onSearchChange(q: String) {
        _query.value = q
        _state.update { it.copy(searchQuery = q) }
        loadPage(1, q)
    }

    fun loadPage(page: Int, query: String? = null) {
        val q = query ?: _state.value.searchQuery
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            categoryRepo.getCategoriesPaginated(page = page, query = q)
                .onSuccess { result ->
                    _state.update { it.copy(
                        categories   = result.items,
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

    fun openAddDialog() = _state.update { it.copy(
        showEditDialog = true, editingCategory = null,
        dialogName = "", dialogDescription = "", dialogIcon = "", dialogError = null
    )}

    fun openEditDialog(cat: Category) = _state.update { it.copy(
        showEditDialog = true, editingCategory = cat,
        dialogName = cat.name, dialogDescription = cat.description ?: "",
        dialogIcon = cat.icon ?: "", dialogError = null
    )}

    fun closeDialog() = _state.update { it.copy(showEditDialog = false, dialogError = null) }
    fun onDialogNameChange(v: String)        = _state.update { it.copy(dialogName = v, dialogError = null) }
    fun onDialogDescriptionChange(v: String) = _state.update { it.copy(dialogDescription = v) }
    fun onDialogIconChange(v: String)        = _state.update { it.copy(dialogIcon = v) }

    fun saveCategory() {
        val s = _state.value
        if (s.dialogName.isBlank()) { _state.update { it.copy(dialogError = "Name is required.") }; return }
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            val userId = session.value?.userId ?: return@launch
            val category = Category(
                id          = s.editingCategory?.id ?: 0,
                name        = s.dialogName.trim(),
                description = s.dialogDescription.trim().takeIf { it.isNotBlank() },
                icon        = s.dialogIcon.trim().takeIf { it.isNotBlank() }
            )
            val result = if (s.editingCategory != null)
                categoryRepo.updateCategory(category)
            else
                categoryRepo.insertCategory(category, userId)

            result
                .onSuccess { _state.update { it.copy(isSaving = false, showEditDialog = false) }; loadPage(s.currentPage) }
                .onFailure { e -> _state.update { it.copy(isSaving = false, dialogError = e.message) } }
        }
    }

    fun deleteCategory(id: Long) {
        viewModelScope.launch {
            categoryRepo.deleteCategory(id)
                .onSuccess { loadPage(_state.value.currentPage) }
                .onFailure { e -> _state.update { it.copy(errorMessage = e.message) } }
        }
    }
}

@Composable
fun CategoriesScreen(
    onBack: () -> Unit,
    onCategoryClick: (String) -> Unit,
    viewModel: CategoriesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var deleteTarget by remember { mutableStateOf<Category?>(null) }

    deleteTarget?.let { cat ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("Delete Category", style = MaterialTheme.typography.titleMedium) },
            text  = { Text("Delete \"${cat.name}\"? Recipes won't be deleted.", style = MaterialTheme.typography.bodyMedium) },
            confirmButton = {
                TextButton({ deleteTarget = null; viewModel.deleteCategory(cat.id) }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton({ deleteTarget = null }) { Text("Cancel", color = CaramelBrown) } },
            containerColor = CardSurface
        )
    }

    if (state.showEditDialog) {
        AlertDialog(
            onDismissRequest = viewModel::closeDialog,
            title = {
                Text(
                    if (state.editingCategory != null) "Edit Category" else "New Category",
                    style = MaterialTheme.typography.titleMedium, color = ChocolateBrown
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    CrumbTextField(state.dialogName, viewModel::onDialogNameChange, "Name *")
                    CrumbTextField(state.dialogDescription, viewModel::onDialogDescriptionChange, "Description (optional)")
                    CrumbTextField(state.dialogIcon, viewModel::onDialogIconChange, "Icon emoji (optional)")
                    if (state.dialogError != null)
                        Text(state.dialogError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            },
            confirmButton = {
                CrumbButton(
                    text = if (state.editingCategory != null) "Update" else "Create",
                    onClick = viewModel::saveCategory,
                    isLoading = state.isSaving,
                    modifier = Modifier.width(120.dp)
                )
            },
            dismissButton = { TextButton(viewModel::closeDialog) { Text("Cancel", color = MutedText) } },
            containerColor = CardSurface
        )
    }

    Scaffold(
        topBar = {
            CrumbTopBar(
                title = "Categories",
                onBack = onBack,
                actions = {
                    if (viewModel.isAdmin)
                        IconButton(viewModel::openAddDialog) {
                            Icon(Icons.Default.Add, "Add", tint = LightTextOnDark)
                        }
                }
            )
        },
        containerColor = CreamBackground
    ) { pad ->
        Column(Modifier.fillMaxSize().padding(pad)) {
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = viewModel::onSearchChange,
                placeholder = { Text("Search categories…", color = MutedText) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = MutedText) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = CaramelBrown, unfocusedBorderColor   = DividerColor,
                    focusedContainerColor = CardSurface, unfocusedContainerColor = CardSurface,
                    cursorColor = CaramelBrown, focusedTextColor = BrownText, unfocusedTextColor = BrownText
                )
            )

            when {
                state.isLoading -> LoadingContent()
                state.errorMessage != null -> ErrorContent(state.errorMessage!!)
                state.categories.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("🗂", style = MaterialTheme.typography.displayMedium)
                        Text("No categories found", style = MaterialTheme.typography.titleMedium, color = MutedText)
                    }
                }
                else -> LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            "${state.totalCount} categor${if (state.totalCount != 1L) "ies" else "y"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MutedText,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    items(state.categories, key = { it.id }) { cat ->
                        CategoryRow(
                            category        = cat,
                            isAdmin         = viewModel.isAdmin,
                            onClick         = { onCategoryClick(cat.name) },
                            onEdit          = { viewModel.openEditDialog(cat) },
                            onDelete        = { deleteTarget = cat }
                        )
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

                    item { Spacer(Modifier.height(24.dp)) }
                }
            }
        }
    }
}

@Composable
private fun CategoryRow(
    category: Category,
    isAdmin: Boolean,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(category.icon ?: "🍴", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.size(40.dp))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(category.name, style = MaterialTheme.typography.titleSmall, color = BrownText)
                if (!category.description.isNullOrBlank())
                    Text(category.description, style = MaterialTheme.typography.bodySmall, color = MutedText)
                Text(
                    "${category.recipeCount} recipe${if (category.recipeCount != 1L) "s" else ""}",
                    style = MaterialTheme.typography.labelSmall,
                    color = CaramelBrown
                )
            }
            if (isAdmin) {
                IconButton(onEdit,   modifier = Modifier.size(36.dp)) { Icon(Icons.Default.Edit,   "Edit",   tint = MutedText,               modifier = Modifier.size(18.dp)) }
                IconButton(onDelete, modifier = Modifier.size(36.dp)) { Icon(Icons.Default.Delete, "Delete", tint = ErrorRed.copy(alpha=0.7f), modifier = Modifier.size(18.dp)) }
            } else {
                // Arrow hint for non-admin
                Icon(Icons.Default.ArrowForward, null, tint = MutedText, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
fun PaginationControls(
    currentPage: Int,
    totalPages: Int,
    onPrev: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPrev, enabled = currentPage > 1) {
            Icon(Icons.Default.ArrowBack, "Previous", tint = if (currentPage > 1) CaramelBrown else MutedText)
        }
        Spacer(Modifier.width(8.dp))
        Text("Page $currentPage of $totalPages", style = MaterialTheme.typography.bodyMedium, color = BrownText)
        Spacer(Modifier.width(8.dp))
        IconButton(onClick = onNext, enabled = currentPage < totalPages) {
            Icon(Icons.Default.ArrowForward, "Next", tint = if (currentPage < totalPages) CaramelBrown else MutedText)
        }
    }
}