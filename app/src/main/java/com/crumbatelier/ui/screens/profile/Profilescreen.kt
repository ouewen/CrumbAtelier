package com.crumbatelier.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.crumbatelier.data.model.DeleteType
import com.crumbatelier.data.repository.AuthRepository
import com.crumbatelier.ui.components.*
import com.crumbatelier.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val name: String = "",
    val bio: String = "",
    val email: String = "",
    val role: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepo: AuthRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ProfileUiState())
    val state = _state.asStateFlow()
    val session = authRepo.sessionFlow.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    init {
        viewModelScope.launch {
            authRepo.sessionFlow.filterNotNull().first().let { s ->
                _state.update { it.copy(name = s.userName, email = s.email, role = s.role.name) }
            }
        }
    }

    fun onNameChange(v: String) = _state.update { it.copy(name = v, errorMessage = null) }
    fun onBioChange(v: String)  = _state.update { it.copy(bio  = v) }

    fun saveProfile() {
        if (_state.value.name.isBlank()) {
            _state.update { it.copy(errorMessage = "Name cannot be empty.") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, errorMessage = null) }
            authRepo.updateProfile(_state.value.name, _state.value.bio)
                .onSuccess { _state.update { it.copy(isSaving = false, successMessage = "Profile updated!") } }
                .onFailure { e -> _state.update { it.copy(isSaving = false, errorMessage = e.message) } }
        }
    }

    fun deleteAccount(deleteType: DeleteType, onDeleted: () -> Unit) {
        val userId = session.value?.userId ?: return
        viewModelScope.launch {
            _state.update { it.copy(isDeleting = true) }
            authRepo.deleteAccount(userId, deleteType)
                .onSuccess { onDeleted() }
                .onFailure { e -> _state.update { it.copy(isDeleting = false, errorMessage = e.message) } }
        }
    }
}

@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onAccountDeleted: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state   by viewModel.state.collectAsStateWithLifecycle()
    val session by viewModel.session.collectAsStateWithLifecycle()
    var showDeleteSheet by remember { mutableStateOf(false) }

    // ── Delete account bottom sheet ───────────────────────────────────
    if (showDeleteSheet) {
        DeleteAccountSheet(
            isDeleting = state.isDeleting,
            onDismiss  = { showDeleteSheet = false },
            onConfirm  = { deleteType ->
                viewModel.deleteAccount(deleteType) {
                    showDeleteSheet = false
                    onAccountDeleted()
                }
            }
        )
    }

    Scaffold(
        topBar = { CrumbTopBar("My Profile", onBack = onBack) },
        containerColor = CreamBackground
    ) { pad ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(pad)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // ── Avatar + role badge ───────────────────────────────────
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(CaramelBrown),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        session?.userName?.firstOrNull()?.uppercase() ?: "?",
                        style = MaterialTheme.typography.headlineLarge,
                        color = LightTextOnDark
                    )
                }
                Spacer(Modifier.height(8.dp))
                Surface(
                    color = if (state.role == "ADMIN") ChocolateBrown else SoftBeige,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        state.role,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (state.role == "ADMIN") LightTextOnDark else BrownText
                    )
                }
            }

            HorizontalDivider(color = DividerColor)

            // ── Edit profile ──────────────────────────────────────────
            SectionTitle("Edit Profile")
            CrumbTextField(state.name, viewModel::onNameChange, "Display Name")
            CrumbTextField(
                state.bio, viewModel::onBioChange, "Bio (optional)",
                singleLine = false, minLines = 2, maxLines = 4
            )
            CrumbTextField(
                value = state.email,
                onValueChange = {},
                label = "Email (read-only)",
                modifier = Modifier
            )

            if (state.errorMessage != null)
                Text(state.errorMessage!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            if (state.successMessage != null)
                Text(state.successMessage!!, color = CaramelBrown, style = MaterialTheme.typography.bodySmall)

            CrumbButton("Save Changes", onClick = viewModel::saveProfile, isLoading = state.isSaving)

            HorizontalDivider(color = DividerColor)

            // ── Danger zone ───────────────────────────────────────────
            SectionTitle("Account")
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ErrorRed.copy(alpha = 0.05f)),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ErrorRed.copy(alpha = 0.2f))
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Danger Zone", style = MaterialTheme.typography.titleSmall, color = ErrorRed)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Deleting or deactivating your account is irreversible. Choose carefully.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedText
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = { showDeleteSheet = true },
                        colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Warning, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Manage Account Deletion")
                    }
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

// ── Delete Account Bottom Sheet ───────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeleteAccountSheet(
    isDeleting: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (DeleteType) -> Unit
) {
    var selectedType by remember { mutableStateOf<DeleteType?>(null) }
    var showConfirm  by remember { mutableStateOf(false) }

    if (showConfirm && selectedType != null) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title = { Text("Are you sure?", style = MaterialTheme.typography.titleMedium) },
            text = {
                Text(
                    selectedType!!.description + "\n\nThis action cannot be easily undone.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedText
                )
            },
            confirmButton = {
                Button(
                    onClick = { onConfirm(selectedType!!) },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                    enabled = !isDeleting
                ) {
                    if (isDeleting)
                        CircularProgressIndicator(Modifier.size(16.dp), color = LightTextOnDark, strokeWidth = 2.dp)
                    else
                        Text("Confirm")
                }
            },
            dismissButton = {
                TextButton({ showConfirm = false }) { Text("Cancel", color = CaramelBrown) }
            },
            containerColor = CardSurface
        )
    }

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = CreamBackground) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "How would you like to leave?",
                style = MaterialTheme.typography.titleLarge,
                color = ChocolateBrown,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                "Select the type of account removal below.",
                style = MaterialTheme.typography.bodyMedium,
                color = MutedText,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(4.dp))

            DeleteType.values().forEach { type ->
                val isSelected = selectedType == type
                Card(
                    onClick = { selectedType = type },
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) ChocolateBrown.copy(alpha = 0.08f) else CardSurface
                    ),
                    shape = RoundedCornerShape(14.dp),
                    border = if (isSelected)
                        androidx.compose.foundation.BorderStroke(1.5.dp, ChocolateBrown)
                    else
                        androidx.compose.foundation.BorderStroke(1.dp, DividerColor)
                ) {
                    Row(
                        Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick  = { selectedType = type },
                            colors   = RadioButtonDefaults.colors(selectedColor = ChocolateBrown)
                        )
                        Spacer(Modifier.width(8.dp))
                        Column(Modifier.weight(1f)) {
                            Text(type.label, style = MaterialTheme.typography.titleSmall, color = BrownText)
                            Spacer(Modifier.height(2.dp))
                            Text(type.description, style = MaterialTheme.typography.bodySmall, color = MutedText)
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { if (selectedType != null) showConfirm = true },
                enabled = selectedType != null,
                colors  = ButtonDefaults.buttonColors(
                    containerColor        = ErrorRed,
                    disabledContainerColor = WarmBeigeMuted
                ),
                shape    = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Text("Continue", style = MaterialTheme.typography.labelLarge)
            }
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text("Cancel", color = MutedText)
            }
        }
    }
}