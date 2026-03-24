package com.crumbatelier.ui.screens.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import com.crumbatelier.data.model.Comment
import com.crumbatelier.data.model.Recipe
import com.crumbatelier.data.model.UserRole
import com.crumbatelier.data.repository.AuthRepository
import com.crumbatelier.data.repository.CommentRepository
import com.crumbatelier.data.repository.FavoriteRepository
import com.crumbatelier.data.repository.RecipeRepository
import com.crumbatelier.ui.components.CrumbTopBar
import com.crumbatelier.ui.components.ErrorContent
import com.crumbatelier.ui.components.LoadingContent
import com.crumbatelier.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// ── Models ─────────────────────────────────────────────────────────────

data class CommentWithReplies(
    val comment: Comment,
    val replies: List<Comment> = emptyList(),
    val replyPage: Int = 1,
    val replyTotalCount: Long = 0,
    val hasMoreReplies: Boolean = false,
    val isLoadingReplies: Boolean = false,
    val isExpanded: Boolean = false,
    val replyInput: String = "",
    val isSubmittingReply: Boolean = false,
    val showReplyBox: Boolean = false
)

// ── State ──────────────────────────────────────────────────────────────

data class RecipeDetailUiState(
    val recipe: Recipe? = null,
    val comments: List<CommentWithReplies> = emptyList(),
    val isLoading: Boolean = true,
    val isLoadingComments: Boolean = false,
    val isSubmittingComment: Boolean = false,
    val commentInput: String = "",
    val ratingInput: Int = 0,
    val editingComment: Comment? = null,
    val errorMessage: String? = null,
    val commentError: String? = null
)

// ── ViewModel ──────────────────────────────────────────────────────────

@HiltViewModel
class RecipeDetailViewModel @Inject constructor(
    private val recipeRepo: RecipeRepository,
    private val commentRepo: CommentRepository,
    private val favoriteRepo: FavoriteRepository,
    private val authRepo: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RecipeDetailUiState())
    val state = _state.asStateFlow()
    val session = authRepo.sessionFlow.stateIn(viewModelScope, SharingStarted.Eagerly, null)
    val isAdmin: Boolean get() = session.value?.role == UserRole.ADMIN
    private var loadedId = -1L

    fun loadRecipe(id: Long) {
        if (loadedId == id) return
        loadedId = id
        viewModelScope.launch {
            val s = session.filterNotNull().first()
            recipeRepo.observeRecipeById(id, s.userId)
                .catch { e -> _state.update { it.copy(isLoading = false, errorMessage = e.message) } }
                .collect { r -> _state.update { it.copy(recipe = r, isLoading = false) } }
        }
        loadComments(id)
    }

    fun loadComments(recipeId: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingComments = true) }
            commentRepo.getCommentsPaginated(recipeId, page = 1, pageSize = 100)
                .onSuccess { result ->
                    // Load reply counts for all comments in parallel
                    val commentsWithCounts = result.items.map { comment ->
                        async {
                            val replyCount = commentRepo.countReplies(comment.id)
                            android.util.Log.d("COMMENTS", "comment ${comment.id} replyCount=$replyCount")
                            CommentWithReplies(
                                comment        = comment,
                                replyTotalCount = replyCount,
                                hasMoreReplies  = replyCount > 0
                            )
                        }
                    }.awaitAll()
                    _state.update { it.copy(comments = commentsWithCounts, isLoadingComments = false) }
                }
                .onFailure { e ->
                    android.util.Log.e("COMMENTS", "loadComments failed: ${e.message}")
                    _state.update { it.copy(isLoadingComments = false) }
                }
        }
    }

    // ── Replies ───────────────────────────────────────────────────────

    fun toggleReplies(commentId: Long) {
        val existing = _state.value.comments.find { it.comment.id == commentId } ?: return
        if (existing.isExpanded) {
            updateComment(commentId) { it.copy(isExpanded = false) }
        } else {
            updateComment(commentId) { it.copy(isExpanded = true) }
            if (existing.replies.isEmpty()) loadReplies(commentId, 1)
        }
    }

    fun loadReplies(commentId: Long, page: Int) {
        viewModelScope.launch {
            updateComment(commentId) { it.copy(isLoadingReplies = true) }
            commentRepo.getReplies(parentId = commentId, page = page, pageSize = 5)
                .onSuccess { result ->
                    updateComment(commentId) { cwr ->
                        val merged = if (page == 1) result.items else cwr.replies + result.items
                        cwr.copy(
                            replies          = merged,
                            replyPage        = result.currentPage,
                            replyTotalCount  = result.totalCount,
                            hasMoreReplies   = result.currentPage < result.totalPages,
                            isLoadingReplies = false
                        )
                    }
                }
                .onFailure { e ->
                    android.util.Log.e("COMMENTS", "loadReplies failed: ${e.message}")
                    updateComment(commentId) { it.copy(isLoadingReplies = false) }
                }
        }
    }

    fun toggleReplyBox(commentId: Long) =
        updateComment(commentId) { it.copy(showReplyBox = !it.showReplyBox, replyInput = "") }

    fun onReplyInput(commentId: Long, v: String) =
        updateComment(commentId) { it.copy(replyInput = v) }

    fun submitReply(commentId: Long) {
        val cwr      = _state.value.comments.find { it.comment.id == commentId } ?: return
        val userId   = session.value?.userId ?: return
        val recipeId = _state.value.recipe?.id ?: return
        if (cwr.replyInput.isBlank()) return
        viewModelScope.launch {
            updateComment(commentId) { it.copy(isSubmittingReply = true) }
            commentRepo.addComment(
                recipeId = recipeId,
                userId   = userId,
                content  = cwr.replyInput.trim(),
                rating   = null,
                parentId = commentId
            ).onSuccess {
                updateComment(commentId) { it.copy(isSubmittingReply = false, replyInput = "", showReplyBox = false) }
                // Refresh replies and update count
                loadReplies(commentId, 1)
                updateComment(commentId) { it.copy(replyTotalCount = it.replyTotalCount + 1, hasMoreReplies = true, isExpanded = true) }
            }.onFailure { e ->
                android.util.Log.e("COMMENTS", "submitReply failed: ${e.message}")
                updateComment(commentId) { it.copy(isSubmittingReply = false) }
            }
        }
    }

    // ── Top-level comments ────────────────────────────────────────────

    fun onCommentInput(v: String) = _state.update { it.copy(commentInput = v, commentError = null) }
    fun onRatingInput(v: Int)     = _state.update { it.copy(ratingInput = v) }

    fun startEditComment(comment: Comment) = _state.update {
        it.copy(editingComment = comment, commentInput = comment.content, ratingInput = comment.rating ?: 0)
    }

    fun cancelEdit() = _state.update {
        it.copy(editingComment = null, commentInput = "", ratingInput = 0, commentError = null)
    }

    fun submitComment() {
        val s = _state.value
        if (s.commentInput.isBlank()) {
            _state.update { it.copy(commentError = "Comment cannot be empty.") }
            return
        }
        val userId   = session.value?.userId ?: return
        val recipeId = s.recipe?.id ?: return
        viewModelScope.launch {
            _state.update { it.copy(isSubmittingComment = true) }
            val result = if (s.editingComment != null)
                commentRepo.updateComment(s.editingComment.id, s.commentInput.trim(), s.ratingInput.takeIf { it > 0 })
            else
                commentRepo.addComment(recipeId, userId, s.commentInput.trim(), s.ratingInput.takeIf { it > 0 })
            result
                .onSuccess {
                    _state.update { it.copy(isSubmittingComment = false, commentInput = "", ratingInput = 0, editingComment = null, commentError = null) }
                    loadComments(recipeId)
                }
                .onFailure { e -> _state.update { it.copy(isSubmittingComment = false, commentError = e.message) } }
        }
    }

    fun deleteComment(commentId: Long) {
        val recipeId = _state.value.recipe?.id ?: return
        viewModelScope.launch {
            commentRepo.deleteComment(commentId).onSuccess { loadComments(recipeId) }
        }
    }

    fun toggleFavorite() {
        val r = _state.value.recipe ?: return
        val s = session.value ?: return
        viewModelScope.launch { favoriteRepo.toggleFavorite(s.userId, r.id, r.isFavorited) }
    }

    fun deleteRecipe(onDeleted: () -> Unit) {
        if (!isAdmin) return
        val id = _state.value.recipe?.id ?: return
        viewModelScope.launch {
            recipeRepo.deleteRecipe(id)
                .onSuccess { onDeleted() }
                .onFailure { e -> _state.update { it.copy(errorMessage = e.message) } }
        }
    }

    private fun updateComment(id: Long, transform: (CommentWithReplies) -> CommentWithReplies) {
        _state.update { s -> s.copy(comments = s.comments.map { if (it.comment.id == id) transform(it) else it }) }
    }
}

// ── Screen ─────────────────────────────────────────────────────────────

@Composable
fun RecipeDetailScreen(
    recipeId: Long,
    onBack: () -> Unit,
    onEditClick: () -> Unit,
    viewModel: RecipeDetailViewModel = hiltViewModel()
) {
    val state   by viewModel.state.collectAsStateWithLifecycle()
    val session by viewModel.session.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(recipeId) { viewModel.loadRecipe(recipeId) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Recipe", style = MaterialTheme.typography.titleMedium) },
            text  = { Text("This cannot be undone.", style = MaterialTheme.typography.bodyMedium) },
            confirmButton = {
                TextButton({ showDeleteDialog = false; viewModel.deleteRecipe(onBack) }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton({ showDeleteDialog = false }) { Text("Cancel", color = CaramelBrown) } },
            containerColor = CardSurface
        )
    }

    Scaffold(
        topBar = {
            CrumbTopBar(title = state.recipe?.title ?: "Recipe", onBack = onBack, actions = {
                if (viewModel.isAdmin) {
                    IconButton(onEditClick)                { Icon(Icons.Default.Edit,   "Edit",   tint = LightTextOnDark) }
                    IconButton({ showDeleteDialog = true }) { Icon(Icons.Default.Delete, "Delete", tint = LightTextOnDark) }
                }
                IconButton(viewModel::toggleFavorite) {
                    Icon(
                        if (state.recipe?.isFavorited == true) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        "Fav",
                        tint = if (state.recipe?.isFavorited == true) FavoriteRed else LightTextOnDark
                    )
                }
            })
        },
        containerColor = CreamBackground
    ) { pad ->
        when {
            state.isLoading      -> LoadingContent(Modifier.padding(pad))
            state.recipe == null -> ErrorContent("Recipe not found", Modifier.padding(pad))
            else -> {
                val r = state.recipe!!
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(pad),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {

                    // ── Hero image ───────────────────────────────────────
                    item {
                        Box(Modifier.fillMaxWidth().height(280.dp)) {
                            if (!r.imageUrl.isNullOrBlank()) {
                                AsyncImage(model = r.imageUrl, contentDescription = r.title, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                            } else {
                                Box(
                                    Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(CaramelBrown, ChocolateBrown))),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(r.title.first().uppercase(), style = MaterialTheme.typography.displayLarge, color = LightTextOnDark.copy(alpha = 0.5f))
                                }
                            }
                            Box(Modifier.fillMaxWidth().height(100.dp).align(Alignment.BottomCenter).background(Brush.verticalGradient(listOf(Color.Transparent, CreamBackground))))
                        }
                    }

                    // ── Title + meta ─────────────────────────────────────
                    item {
                        Column(Modifier.padding(horizontal = 24.dp)) {
                            Text(r.title, style = MaterialTheme.typography.headlineLarge, color = ChocolateBrown)
                            Spacer(Modifier.height(8.dp))
                            Text(r.description, style = MaterialTheme.typography.bodyLarge, color = MutedText)
                            if (r.prepTime != null || r.cookTime != null || r.servings != null || r.difficulty != null) {
                                Spacer(Modifier.height(16.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    r.prepTime?.let   { MetaChip("⏱ Prep ${it}m") }
                                    r.cookTime?.let   { MetaChip("🔥 Cook ${it}m") }
                                    r.servings?.let   { MetaChip("🍽 $it serves") }
                                    r.difficulty?.let { MetaChip("📊 ${it.name}") }
                                }
                            }
                        }
                    }

                    // ── Ingredients ──────────────────────────────────────
                    item {
                        Column(Modifier.padding(horizontal = 24.dp)) {
                            Spacer(Modifier.height(32.dp)); HorizontalDivider(color = DividerColor); Spacer(Modifier.height(24.dp))
                            SectionTitle("✦  Ingredients"); Spacer(Modifier.height(16.dp))
                            Card(
                                Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = SoftBeige),
                                shape = RoundedCornerShape(14.dp),
                                elevation = CardDefaults.cardElevation(0.dp)
                            ) {
                                Column(Modifier.padding(20.dp)) {
                                    r.ingredients.split("\n").filter { it.isNotBlank() }.forEach { line ->
                                        Row(verticalAlignment = Alignment.Top, modifier = Modifier.padding(vertical = 4.dp)) {
                                            Text("·  ", color = CaramelBrown, style = MaterialTheme.typography.bodyLarge)
                                            Text(line.trim(), style = MaterialTheme.typography.bodyLarge, color = BrownText)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // ── Steps ────────────────────────────────────────────
                    item {
                        Column(Modifier.padding(horizontal = 24.dp)) {
                            Spacer(Modifier.height(32.dp)); HorizontalDivider(color = DividerColor); Spacer(Modifier.height(24.dp))
                            SectionTitle("✦  Method"); Spacer(Modifier.height(16.dp))
                            val steps = r.steps.split("\n").filter { it.isNotBlank() }
                            steps.forEachIndexed { i, step ->
                                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                                    Box(
                                        Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(ChocolateBrown),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("${i + 1}", style = MaterialTheme.typography.labelLarge, color = LightTextOnDark)
                                    }
                                    Spacer(Modifier.width(12.dp))
                                    Text(
                                        step.trim().replaceFirst(Regex("^\\d+\\.\\s*"), ""),
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = BrownText,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                if (i < steps.lastIndex) Spacer(Modifier.height(12.dp))
                            }
                        }
                    }

                    // ── Comments header ──────────────────────────────────
                    item {
                        Column(Modifier.padding(horizontal = 24.dp)) {
                            Spacer(Modifier.height(32.dp)); HorizontalDivider(color = DividerColor); Spacer(Modifier.height(24.dp))
                            SectionTitle("✦  Comments (${state.comments.size})")
                            Spacer(Modifier.height(16.dp))
                        }
                    }

                    // ── Comment input ────────────────────────────────────
                    item {
                        CommentInputBox(
                            input        = state.commentInput,
                            rating       = state.ratingInput,
                            isEditing    = state.editingComment != null,
                            isSubmitting = state.isSubmittingComment,
                            error        = state.commentError,
                            onInputChange  = viewModel::onCommentInput,
                            onRatingChange = viewModel::onRatingInput,
                            onSubmit     = viewModel::submitComment,
                            onCancel     = viewModel::cancelEdit
                        )
                        Spacer(Modifier.height(16.dp))
                    }

                    // ── Comments list ────────────────────────────────────
                    if (state.isLoadingComments) {
                        item {
                            Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = CaramelBrown, modifier = Modifier.size(24.dp))
                            }
                        }
                    } else if (state.comments.isEmpty()) {
                        item {
                            Box(Modifier.fillMaxWidth().padding(vertical = 24.dp), contentAlignment = Alignment.Center) {
                                Text("No comments yet. Be the first!", style = MaterialTheme.typography.bodyMedium, color = MutedText)
                            }
                        }
                    } else {
                        items(state.comments, key = { it.comment.id }) { cwr ->
                            CommentItem(
                                cwr               = cwr,
                                currentUserId     = session?.userId,
                                isAdmin           = viewModel.isAdmin,
                                onEdit            = { viewModel.startEditComment(cwr.comment) },
                                onDelete          = { viewModel.deleteComment(cwr.comment.id) },
                                onToggleReplies   = { viewModel.toggleReplies(cwr.comment.id) },
                                onToggleReplyBox  = { viewModel.toggleReplyBox(cwr.comment.id) },
                                onReplyInput      = { viewModel.onReplyInput(cwr.comment.id, it) },
                                onSubmitReply     = { viewModel.submitReply(cwr.comment.id) },
                                onLoadMoreReplies = { viewModel.loadReplies(cwr.comment.id, cwr.replyPage + 1) },
                                onDeleteReply     = { viewModel.deleteComment(it) }
                            )
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

// ── Comment Item ───────────────────────────────────────────────────────

@Composable
private fun CommentItem(
    cwr: CommentWithReplies,
    currentUserId: String?,
    isAdmin: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleReplies: () -> Unit,
    onToggleReplyBox: () -> Unit,
    onReplyInput: (String) -> Unit,
    onSubmitReply: () -> Unit,
    onLoadMoreReplies: () -> Unit,
    onDeleteReply: (Long) -> Unit
) {
    val comment = cwr.comment
    Column(Modifier.padding(horizontal = 24.dp)) {

        // Main comment card
        Card(
            Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            Column(Modifier.padding(16.dp)) {

                // Author row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Avatar(comment.userName)
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(comment.userName, style = MaterialTheme.typography.titleSmall, color = BrownText)
                        comment.createdAt?.let { Text(it.take(10), style = MaterialTheme.typography.bodySmall, color = MutedText) }
                    }
                    if (comment.userId == currentUserId) {
                        IconButton(onEdit, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Edit, "Edit", tint = MutedText, modifier = Modifier.size(16.dp))
                        }
                    }
                    if (comment.userId == currentUserId || isAdmin) {
                        IconButton(onDelete, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Delete, "Delete", tint = ErrorRed.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                        }
                    }
                }

                // Star rating
                if ((comment.rating ?: 0) > 0) {
                    Spacer(Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        (1..5).forEach { star ->
                            Icon(
                                if (star <= (comment.rating ?: 0)) Icons.Default.Star else Icons.Default.StarBorder,
                                null, tint = CaramelBrown, modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
                Text(comment.content, style = MaterialTheme.typography.bodyMedium, color = BrownText)

                // Reply / View replies buttons
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    TextButton(onClick = onToggleReplyBox, contentPadding = PaddingValues(0.dp)) {
                        Icon(Icons.Default.Reply, null, tint = CaramelBrown, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Reply", style = MaterialTheme.typography.labelMedium, color = CaramelBrown)
                    }
                    if (cwr.replyTotalCount > 0 || cwr.replies.isNotEmpty()) {
                        TextButton(onClick = onToggleReplies, contentPadding = PaddingValues(0.dp)) {
                            Icon(
                                if (cwr.isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                null, tint = MutedText, modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                if (cwr.isExpanded) "Hide replies"
                                else "View ${cwr.replyTotalCount} ${if (cwr.replyTotalCount == 1L) "reply" else "replies"}",
                                style = MaterialTheme.typography.labelMedium, color = MutedText
                            )
                        }
                    }
                }
            }
        }

        // Reply input box
        AnimatedVisibility(visible = cwr.showReplyBox) {
            Column(Modifier.padding(start = 24.dp, top = 8.dp)) {
                OutlinedTextField(
                    value = cwr.replyInput,
                    onValueChange = onReplyInput,
                    placeholder = { Text("Write a reply…", color = MutedText) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor      = CaramelBrown,
                        unfocusedBorderColor    = DividerColor,
                        focusedContainerColor   = CardSurface,
                        unfocusedContainerColor = CardSurface,
                        cursorColor             = CaramelBrown,
                        focusedTextColor        = BrownText,
                        unfocusedTextColor      = BrownText
                    ),
                    trailingIcon = {
                        IconButton(onClick = onSubmitReply, enabled = !cwr.isSubmittingReply) {
                            if (cwr.isSubmittingReply)
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = CaramelBrown)
                            else
                                Icon(Icons.Default.Send, "Send", tint = CaramelBrown)
                        }
                    }
                )
                Spacer(Modifier.height(8.dp))
            }
        }

        // Replies dropdown
        AnimatedVisibility(visible = cwr.isExpanded) {
            Column(
                Modifier.padding(start = 24.dp, top = 4.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (cwr.isLoadingReplies && cwr.replies.isEmpty()) {
                    Box(Modifier.fillMaxWidth().padding(8.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = CaramelBrown)
                    }
                }

                cwr.replies.forEach { reply ->
                    ReplyCard(
                        reply         = reply,
                        currentUserId = currentUserId,
                        isAdmin       = isAdmin,
                        onDelete      = { onDeleteReply(reply.id) }
                    )
                }

                // Load more
                if (cwr.hasMoreReplies) {
                    TextButton(
                        onClick  = onLoadMoreReplies,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        if (cwr.isLoadingReplies) {
                            CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp, color = CaramelBrown)
                            Spacer(Modifier.width(6.dp))
                        }
                        Text("Load more replies", style = MaterialTheme.typography.labelMedium, color = CaramelBrown)
                    }
                }

                Spacer(Modifier.height(4.dp))
            }
        }
    }
}

// ── Reply Card ─────────────────────────────────────────────────────────

@Composable
private fun ReplyCard(
    reply: Comment,
    currentUserId: String?,
    isAdmin: Boolean,
    onDelete: () -> Unit
) {
    Card(
        Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SoftBeige),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Avatar(reply.userName, size = 28)
                Spacer(Modifier.width(8.dp))
                Column(Modifier.weight(1f)) {
                    Text(reply.userName, style = MaterialTheme.typography.labelMedium, color = BrownText)
                    reply.createdAt?.let { Text(it.take(10), style = MaterialTheme.typography.bodySmall, color = MutedText) }
                }
                if (reply.userId == currentUserId || isAdmin) {
                    IconButton(onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Delete, "Delete", tint = ErrorRed.copy(alpha = 0.6f), modifier = Modifier.size(14.dp))
                    }
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(reply.content, style = MaterialTheme.typography.bodySmall, color = BrownText)
        }
    }
}

// ── Comment Input Box ──────────────────────────────────────────────────

@Composable
private fun CommentInputBox(
    input: String,
    rating: Int,
    isEditing: Boolean,
    isSubmitting: Boolean,
    error: String?,
    onInputChange: (String) -> Unit,
    onRatingChange: (Int) -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit
) {
    Card(
        Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                if (isEditing) "Edit Comment" else "Leave a Comment",
                style = MaterialTheme.typography.titleSmall, color = ChocolateBrown
            )
            // Star rating
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Rating:", style = MaterialTheme.typography.bodySmall, color = MutedText)
                (1..5).forEach { star ->
                    IconButton(
                        onClick  = { onRatingChange(if (rating == star) 0 else star) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            if (star <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                            "$star stars",
                            tint     = if (star <= rating) CaramelBrown else MutedText,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                if (rating > 0) Text("($rating/5)", style = MaterialTheme.typography.bodySmall, color = CaramelBrown)
            }
            OutlinedTextField(
                value         = input,
                onValueChange = onInputChange,
                placeholder   = { Text("Share your thoughts…", color = MutedText) },
                modifier      = Modifier.fillMaxWidth(),
                minLines      = 3,
                maxLines      = 6,
                shape         = RoundedCornerShape(10.dp),
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor      = CaramelBrown,
                    unfocusedBorderColor    = DividerColor,
                    focusedContainerColor   = CreamBackground,
                    unfocusedContainerColor = CreamBackground,
                    cursorColor             = CaramelBrown,
                    focusedTextColor        = BrownText,
                    unfocusedTextColor      = BrownText
                )
            )
            if (error != null) Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                if (isEditing) {
                    OutlinedButton(
                        onClick  = onCancel,
                        modifier = Modifier.weight(1f),
                        shape    = RoundedCornerShape(10.dp),
                        colors   = ButtonDefaults.outlinedButtonColors(contentColor = MutedText)
                    ) { Text("Cancel") }
                }
                Button(
                    onClick  = onSubmit,
                    modifier = Modifier.weight(1f),
                    enabled  = !isSubmitting,
                    shape    = RoundedCornerShape(10.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = CaramelBrown, contentColor = LightTextOnDark)
                ) {
                    if (isSubmitting)
                        CircularProgressIndicator(color = LightTextOnDark, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    else
                        Text(if (isEditing) "Update" else "Post Comment")
                }
            }
        }
    }
}

// ── Helpers ────────────────────────────────────────────────────────────

@Composable
private fun Avatar(name: String, size: Int = 36) {
    Box(
        Modifier.size(size.dp).clip(CircleShape).background(ChocolateBrown),
        contentAlignment = Alignment.Center
    ) {
        Text(name.firstOrNull()?.uppercase() ?: "?", style = MaterialTheme.typography.labelMedium, color = LightTextOnDark)
    }
}

@Composable
private fun MetaChip(text: String) {
    Surface(shape = RoundedCornerShape(8.dp), color = SoftBeige) {
        Text(text, style = MaterialTheme.typography.bodySmall, color = BrownText, modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold), color = ChocolateBrown)
}