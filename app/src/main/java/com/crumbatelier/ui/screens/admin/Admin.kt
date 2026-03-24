package com.crumbatelier.ui.screens.admin

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import com.crumbatelier.data.model.Category
import com.crumbatelier.data.model.Recipe
import com.crumbatelier.data.model.UserRole
import com.crumbatelier.data.repository.AuthRepository
import com.crumbatelier.data.repository.CategoryRepository
import com.crumbatelier.data.repository.RecipeRepository
import com.crumbatelier.ui.components.*
import com.crumbatelier.ui.state.AdminRecipeUiState
import com.crumbatelier.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AdminRecipeViewModel @Inject constructor(
    private val recipeRepo: RecipeRepository,
    private val authRepo: AuthRepository,
    private val categoryRepo: CategoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminRecipeUiState())
    val state = _state.asStateFlow()

    private val _isUploadingImage = MutableStateFlow(false)
    val isUploadingImage: StateFlow<Boolean> = _isUploadingImage.asStateFlow()

    private val session = authRepo.sessionFlow.stateIn(viewModelScope, SharingStarted.Eagerly, null)
    private val isAdmin: Boolean get() = session.value?.role == UserRole.ADMIN

    val allCategories: StateFlow<List<Category>> = categoryRepo
        .observeCategories()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _selectedCategoryIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedCategoryIds: StateFlow<Set<Long>> = _selectedCategoryIds.asStateFlow()

    fun toggleCategory(categoryId: Long) {
        _selectedCategoryIds.update { current ->
            if (categoryId in current) current - categoryId else current + categoryId
        }
    }

    fun loadForEdit(id: Long) {
        if (!isAdmin) return
        viewModelScope.launch {
            val s = session.filterNotNull().first()
            recipeRepo.observeRecipeById(id, s.userId).filterNotNull().first().let { r ->
                _state.update {
                    it.copy(
                        title       = r.title,
                        description = r.description,
                        ingredients = r.ingredients,
                        steps       = r.steps,
                        imageUrl    = r.imageUrl ?: "",
                        isEditMode  = true
                    )
                }
            }
            categoryRepo.getRecipeCategoryIds(id)
                .onSuccess { ids -> _selectedCategoryIds.value = ids.toSet() }
        }
    }

    fun uploadImage(imageBytes: ByteArray, mimeType: String) {
        viewModelScope.launch {
            _isUploadingImage.value = true
            _state.update { it.copy(errorMessage = null) }
            val ext = when {
                mimeType.contains("png")  -> "png"
                mimeType.contains("webp") -> "webp"
                else                      -> "jpg"
            }
            val fileName = "${UUID.randomUUID()}.$ext"
            recipeRepo.uploadRecipeImage(imageBytes, fileName, mimeType)
                .onSuccess { url -> _state.update { it.copy(imageUrl = url) } }
                .onFailure { e  -> _state.update { it.copy(errorMessage = "Upload failed: ${e.message}") } }
            _isUploadingImage.value = false
        }
    }

    fun clearImage() = _state.update { it.copy(imageUrl = "") }

    fun onTitleChange(v: String)       = _state.update { it.copy(title       = v, errorMessage = null) }
    fun onDescriptionChange(v: String) = _state.update { it.copy(description = v, errorMessage = null) }
    fun onIngredientsChange(v: String) = _state.update { it.copy(ingredients = v, errorMessage = null) }
    fun onStepsChange(v: String)       = _state.update { it.copy(steps       = v, errorMessage = null) }

    fun save(recipeId: Long?, onSaved: () -> Unit) {
        if (!isAdmin) { _state.update { it.copy(errorMessage = "Unauthorized.") }; return }
        val s = _state.value
        when {
            s.title.isBlank()       -> { _state.update { it.copy(errorMessage = "Title is required.")        }; return }
            s.ingredients.isBlank() -> { _state.update { it.copy(errorMessage = "Ingredients are required.") }; return }
            s.steps.isBlank()       -> { _state.update { it.copy(errorMessage = "Steps are required.")       }; return }
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val userId = session.value?.userId ?: return@launch
            val recipe = Recipe(
                id          = recipeId ?: 0L,
                title       = s.title.trim(),
                description = s.description.trim(),
                ingredients = s.ingredients.trim(),
                steps       = s.steps.trim(),
                imageUrl    = s.imageUrl.takeIf { it.isNotBlank() },
                createdBy   = userId
            )
            val result = if (recipeId != null) recipeRepo.updateRecipe(recipe)
            else recipeRepo.insertRecipe(recipe, userId)
            result
                .onSuccess {
                    val targetId = recipeId ?: recipeRepo.getLatestRecipeIdByUser(userId)
                    if (targetId != null) {
                        categoryRepo.setRecipeCategories(targetId, _selectedCategoryIds.value.toList())
                    }
                    _state.update { it.copy(isLoading = false, isSaved = true) }
                    onSaved()
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, errorMessage = e.message ?: "Failed to save.") }
                }
        }
    }
}

@Composable
fun AdminRecipeScreen(
    recipeId: Long?,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: AdminRecipeViewModel = hiltViewModel()
) {
    val state              by viewModel.state.collectAsStateWithLifecycle()
    val allCategories      by viewModel.allCategories.collectAsStateWithLifecycle()
    val selectedCategories by viewModel.selectedCategoryIds.collectAsStateWithLifecycle()
    val isUploadingImage   by viewModel.isUploadingImage.collectAsStateWithLifecycle()
    val context            = LocalContext.current

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val mimeType = context.contentResolver.getType(it) ?: "image/jpeg"
            val bytes    = context.contentResolver.openInputStream(it)?.readBytes() ?: return@let
            viewModel.uploadImage(bytes, mimeType)
        }
    }

    LaunchedEffect(recipeId) {
        if (recipeId != null) viewModel.loadForEdit(recipeId)
    }

    Scaffold(
        topBar = { CrumbTopBar(if (recipeId != null) "Edit Recipe" else "New Recipe", onBack = onBack) },
        containerColor = CreamBackground
    ) { pad ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(pad)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                if (recipeId != null) "Update the recipe details below" else "Fill in the recipe details",
                style = MaterialTheme.typography.bodyMedium,
                color = MutedText
            )

            CrumbTextField(state.title,       viewModel::onTitleChange,       "Recipe Title *")
            CrumbTextField(state.description, viewModel::onDescriptionChange, "Short Description",
                singleLine = false, minLines = 2, maxLines = 4)

            // ── Image picker ──────────────────────────────────────────
            Column {
                Text("Recipe Image", style = MaterialTheme.typography.titleSmall, color = ChocolateBrown)
                Spacer(Modifier.height(8.dp))

                if (state.imageUrl.isNotBlank()) {
                    // Preview with remove button
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        AsyncImage(
                            model              = state.imageUrl,
                            contentDescription = "Recipe image",
                            contentScale       = ContentScale.Crop,
                            modifier           = Modifier.fillMaxSize()
                        )
                        IconButton(
                            onClick  = { viewModel.clearImage() },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .background(ChocolateBrown.copy(alpha = 0.7f), RoundedCornerShape(50))
                        ) {
                            Icon(Icons.Default.Close, "Remove image", tint = LightTextOnDark)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(
                        onClick  = { imagePicker.launch("image/*") },
                        modifier = Modifier.fillMaxWidth(),
                        shape    = RoundedCornerShape(12.dp),
                        colors   = ButtonDefaults.outlinedButtonColors(contentColor = CaramelBrown),
                        border   = androidx.compose.foundation.BorderStroke(1.dp, CaramelBrown)
                    ) {
                        Icon(Icons.Default.Image, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Change Image")
                    }
                } else {
                    Button(
                        onClick   = { imagePicker.launch("image/*") },
                        enabled   = !isUploadingImage,
                        modifier  = Modifier.fillMaxWidth().height(120.dp),
                        shape     = RoundedCornerShape(12.dp),
                        colors    = ButtonDefaults.buttonColors(
                            containerColor = SoftBeige,
                            contentColor   = MutedText
                        ),
                        elevation = ButtonDefaults.buttonElevation(0.dp)
                    ) {
                        if (isUploadingImage) {
                            CircularProgressIndicator(
                                modifier    = Modifier.size(24.dp),
                                color       = CaramelBrown,
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(12.dp))
                            Text("Uploading…", style = MaterialTheme.typography.bodyMedium)
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Image, null,
                                    modifier = Modifier.size(36.dp), tint = CaramelBrown)
                                Text("Tap to pick image from gallery",
                                    style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }

            HorizontalDivider(color = DividerColor)

            // ── Category picker ───────────────────────────────────────
            Column {
                Text("Categories", style = MaterialTheme.typography.titleSmall, color = ChocolateBrown)
                Spacer(Modifier.height(4.dp))
                Text("Tap to select one or more categories",
                    style = MaterialTheme.typography.bodySmall, color = SubtleText)
                Spacer(Modifier.height(10.dp))

                if (allCategories.isEmpty()) {
                    Text("No categories available.",
                        style = MaterialTheme.typography.bodySmall, color = MutedText)
                } else {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(allCategories, key = { it.id }) { category ->
                            CategoryChip(
                                category   = category,
                                isSelected = category.id in selectedCategories,
                                onClick    = { viewModel.toggleCategory(category.id) }
                            )
                        }
                    }
                    if (selectedCategories.isNotEmpty()) {
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "${selectedCategories.size} categor${if (selectedCategories.size == 1) "y" else "ies"} selected",
                            style = MaterialTheme.typography.bodySmall,
                            color = CaramelBrown
                        )
                    }
                }
            }

            HorizontalDivider(color = DividerColor)

            // ── Ingredients ───────────────────────────────────────────
            Column {
                Text("Ingredients *", style = MaterialTheme.typography.titleSmall, color = ChocolateBrown)
                Spacer(Modifier.height(4.dp))
                Text("One ingredient per line", style = MaterialTheme.typography.bodySmall, color = SubtleText)
                Spacer(Modifier.height(8.dp))
                CrumbTextField(state.ingredients, viewModel::onIngredientsChange,
                    "e.g. 500g bread flour", singleLine = false, minLines = 5, maxLines = 15)
            }

            HorizontalDivider(color = DividerColor)

            // ── Steps ─────────────────────────────────────────────────
            Column {
                Text("Method / Steps *", style = MaterialTheme.typography.titleSmall, color = ChocolateBrown)
                Spacer(Modifier.height(4.dp))
                Text("One step per line", style = MaterialTheme.typography.bodySmall, color = SubtleText)
                Spacer(Modifier.height(8.dp))
                CrumbTextField(state.steps, viewModel::onStepsChange,
                    "e.g. 1. Mix flour and water…", singleLine = false, minLines = 6, maxLines = 20)
            }

            if (state.errorMessage != null) {
                Text(state.errorMessage!!, color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall)
            }

            Spacer(Modifier.height(8.dp))
            CrumbButton(
                if (recipeId != null) "Update Recipe" else "Save Recipe",
                onClick   = { viewModel.save(recipeId, onSaved) },
                isLoading = state.isLoading
            )
            Spacer(Modifier.height(40.dp))
        }
    }
}

// ── Category Chip ─────────────────────────────────────────────────────

@Composable
private fun CategoryChip(
    category: Category,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor     = if (isSelected) ChocolateBrown else CardSurface
    val textColor   = if (isSelected) LightTextOnDark else BrownText
    val borderColor = if (isSelected) ChocolateBrown else DividerColor

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (category.icon != null) {
            Text(category.icon, style = MaterialTheme.typography.bodySmall)
        }
        Text(category.name, style = MaterialTheme.typography.labelMedium, color = textColor)
        if (isSelected) {
            Icon(Icons.Default.Check, null, tint = LightTextOnDark, modifier = Modifier.size(14.dp))
        }
    }
}