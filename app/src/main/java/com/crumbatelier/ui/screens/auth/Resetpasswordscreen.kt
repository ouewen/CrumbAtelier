package com.crumbatelier.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.crumbatelier.data.repository.AuthRepository
import com.crumbatelier.ui.components.CrumbButton
import com.crumbatelier.ui.components.CrumbTopBar
import com.crumbatelier.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ResetPasswordUiState(
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val auth: AuthRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ResetPasswordUiState())
    val state = _state.asStateFlow()

    fun onNewPasswordChange(v: String)     = _state.update { it.copy(newPassword = v, errorMessage = null) }
    fun onConfirmPasswordChange(v: String) = _state.update { it.copy(confirmPassword = v, errorMessage = null) }

    fun submit(onSuccess: () -> Unit) {
        val s = _state.value
        when {
            s.newPassword.length < 6 ->
                _state.update { it.copy(errorMessage = "Password must be at least 6 characters.") }
            s.newPassword != s.confirmPassword ->
                _state.update { it.copy(errorMessage = "Passwords do not match.") }
            else -> viewModelScope.launch {
                _state.update { it.copy(isLoading = true, errorMessage = null) }
                auth.updatePassword(s.newPassword)
                    .onSuccess {
                        _state.update { it.copy(isLoading = false, isSuccess = true) }
                        onSuccess()
                    }
                    .onFailure { e ->
                        _state.update { it.copy(isLoading = false, errorMessage = e.message) }
                    }
            }
        }
    }
}

@Composable
fun ResetPasswordScreen(
    onSuccess: () -> Unit,
    viewModel: ResetPasswordViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showNew     by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { CrumbTopBar("New Password", onBack = null) },
        containerColor = CreamBackground
    ) { pad ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(48.dp))
            Text("🔐", style = MaterialTheme.typography.displayMedium)
            Spacer(Modifier.height(16.dp))
            Text("Set a new password", style = MaterialTheme.typography.headlineMedium, color = ChocolateBrown, textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
            Text("Choose a strong password for your account.", style = MaterialTheme.typography.bodyMedium, color = MutedText, textAlign = TextAlign.Center)
            Spacer(Modifier.height(40.dp))

            OutlinedTextField(
                value         = state.newPassword,
                onValueChange = viewModel::onNewPasswordChange,
                label         = { Text("New Password") },
                modifier      = Modifier.fillMaxWidth(),
                singleLine    = true,
                visualTransformation = if (showNew) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon  = {
                    IconButton({ showNew = !showNew }) {
                        Icon(if (showNew) Icons.Default.VisibilityOff else Icons.Default.Visibility, null, tint = MutedText)
                    }
                },
                shape  = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor      = CaramelBrown,
                    unfocusedBorderColor    = DividerColor,
                    focusedContainerColor   = CardSurface,
                    unfocusedContainerColor = CardSurface,
                    cursorColor             = CaramelBrown,
                    focusedTextColor        = BrownText,
                    unfocusedTextColor      = BrownText,
                    focusedLabelColor       = CaramelBrown,
                    unfocusedLabelColor     = MutedText
                )
            )
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value         = state.confirmPassword,
                onValueChange = viewModel::onConfirmPasswordChange,
                label         = { Text("Confirm Password") },
                modifier      = Modifier.fillMaxWidth(),
                singleLine    = true,
                visualTransformation = if (showConfirm) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon  = {
                    IconButton({ showConfirm = !showConfirm }) {
                        Icon(if (showConfirm) Icons.Default.VisibilityOff else Icons.Default.Visibility, null, tint = MutedText)
                    }
                },
                shape  = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor      = CaramelBrown,
                    unfocusedBorderColor    = DividerColor,
                    focusedContainerColor   = CardSurface,
                    unfocusedContainerColor = CardSurface,
                    cursorColor             = CaramelBrown,
                    focusedTextColor        = BrownText,
                    unfocusedTextColor      = BrownText,
                    focusedLabelColor       = CaramelBrown,
                    unfocusedLabelColor     = MutedText
                )
            )

            if (state.errorMessage != null) {
                Spacer(Modifier.height(8.dp))
                Text(state.errorMessage!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(Modifier.height(28.dp))
            CrumbButton(
                text      = "Update Password",
                onClick   = { viewModel.submit(onSuccess) },
                isLoading = state.isLoading
            )
        }
    }
}