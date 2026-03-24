package com.crumbatelier.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.crumbatelier.data.repository.AuthRepository
import com.crumbatelier.ui.components.CrumbButton
import com.crumbatelier.ui.components.CrumbTextField
import com.crumbatelier.ui.components.CrumbTopBar
import com.crumbatelier.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ForgotPasswordUiState(
    val email: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val auth: AuthRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ForgotPasswordUiState())
    val state = _state.asStateFlow()

    fun onEmailChange(v: String) = _state.update { it.copy(email = v, errorMessage = null) }

    fun sendReset() {
        val email = _state.value.email.trim()
        if (email.isBlank()) {
            _state.update { it.copy(errorMessage = "Please enter your email address.") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            auth.sendPasswordReset(email)
                .onSuccess { _state.update { it.copy(isLoading = false, isSuccess = true) } }
                .onFailure { e -> _state.update { it.copy(isLoading = false, errorMessage = e.message) } }
        }
    }
}

@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit,
    viewModel: ForgotPasswordViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { CrumbTopBar("Reset Password", onBack = onBack) },
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

            Text("🔑", style = MaterialTheme.typography.displayMedium)
            Spacer(Modifier.height(16.dp))
            Text(
                "Forgot your password?",
                style = MaterialTheme.typography.headlineMedium,
                color = ChocolateBrown,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Enter the email address linked to your account and we'll send you a reset link.",
                style = MaterialTheme.typography.bodyMedium,
                color = MutedText,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(40.dp))

            if (state.isSuccess) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SoftBeige),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("✓", style = MaterialTheme.typography.headlineLarge, color = CaramelBrown)
                        Text(
                            "Check your inbox!",
                            style = MaterialTheme.typography.titleMedium,
                            color = ChocolateBrown
                        )
                        Text(
                            "We sent a password reset link to ${state.email}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedText,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
                TextButton(onClick = onBack) {
                    Text("Back to Sign In", color = CaramelBrown, style = MaterialTheme.typography.labelLarge)
                }
            } else {
                CrumbTextField(
                    value         = state.email,
                    onValueChange = viewModel::onEmailChange,
                    label         = "Email address"
                )
                if (state.errorMessage != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        state.errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(Modifier.height(28.dp))
                CrumbButton(
                    text      = "Send Reset Link",
                    onClick   = viewModel::sendReset,
                    isLoading = state.isLoading
                )
                Spacer(Modifier.height(16.dp))
                TextButton(onClick = onBack) {
                    Text("Back to Sign In", color = MutedText, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}