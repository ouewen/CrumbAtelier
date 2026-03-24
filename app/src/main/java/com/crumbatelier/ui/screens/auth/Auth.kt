package com.crumbatelier.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.crumbatelier.data.repository.AuthRepository
import com.crumbatelier.ui.components.CrumbButton
import com.crumbatelier.ui.components.CrumbTextField
import com.crumbatelier.ui.state.LoginUiState
import com.crumbatelier.ui.state.RegisterUiState
import com.crumbatelier.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// ── LoginViewModel ──────────────────────────────────────────────────

@HiltViewModel
class LoginViewModel @Inject constructor(private val auth: AuthRepository) : ViewModel() {
    private val _state = MutableStateFlow(LoginUiState())
    val state = _state.asStateFlow()
    var onLoginSuccess: (() -> Unit)? = null

    fun onEmailChange(v: String)    = _state.update { it.copy(email    = v, errorMessage = null) }
    fun onPasswordChange(v: String) = _state.update { it.copy(password = v, errorMessage = null) }

    fun login() {
        val s = _state.value
        if (s.email.isBlank() || s.password.isBlank()) { _state.update { it.copy(errorMessage = "Please fill in all fields.") }; return }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            auth.login(s.email, s.password)
                .onSuccess { onLoginSuccess?.invoke() }
                .onFailure { e -> _state.update { it.copy(errorMessage = e.message) } }
            _state.update { it.copy(isLoading = false) }
        }
    }
}

// ── RegisterViewModel ───────────────────────────────────────────────

@HiltViewModel
class RegisterViewModel @Inject constructor(private val auth: AuthRepository) : ViewModel() {
    private val _state = MutableStateFlow(RegisterUiState())
    val state = _state.asStateFlow()
    var onRegisterSuccess: (() -> Unit)? = null

    fun onNameChange(v: String)     = _state.update { it.copy(name     = v, errorMessage = null) }
    fun onEmailChange(v: String)    = _state.update { it.copy(email    = v, errorMessage = null) }
    fun onPasswordChange(v: String) = _state.update { it.copy(password = v, errorMessage = null) }

    fun register() {
        val s = _state.value
        when {
            s.name.isBlank()      -> { _state.update { it.copy(errorMessage = "Name is required.")            }; return }
            s.email.isBlank()     -> { _state.update { it.copy(errorMessage = "Email is required.")           }; return }
            s.password.length < 6 -> { _state.update { it.copy(errorMessage = "Password min 6 characters.")  }; return }
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            auth.register(s.name, s.email, s.password)
                .onSuccess { onRegisterSuccess?.invoke() }
                .onFailure { e -> _state.update { it.copy(errorMessage = e.message) } }
            _state.update { it.copy(isLoading = false) }
        }
    }
}

// ── LoginScreen ─────────────────────────────────────────────────────

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onForgotPassword: () -> Unit,           // ← new
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel) { viewModel.onLoginSuccess = onLoginSuccess }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(80.dp))
        Text("✦", style = MaterialTheme.typography.displayMedium, color = CaramelBrown)
        Spacer(Modifier.height(16.dp))
        Text("Crumb Atelier", style = MaterialTheme.typography.displayMedium, color = ChocolateBrown, textAlign = TextAlign.Center)
        Text("Welcome back to your bakery archive", style = MaterialTheme.typography.bodyMedium, color = MutedText, textAlign = TextAlign.Center)
        Spacer(Modifier.height(48.dp))

        CrumbTextField(value = state.email, onValueChange = viewModel::onEmailChange, label = "Email address")
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = state.password,
            onValueChange = viewModel::onPasswordChange,
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CaramelBrown, unfocusedBorderColor = DividerColor,
                focusedLabelColor = CaramelBrown, unfocusedLabelColor = MutedText,
                cursorColor = CaramelBrown, focusedTextColor = BrownText, unfocusedTextColor = BrownText
            )
        )

        // Forgot password link
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
            TextButton(onClick = onForgotPassword) {
                Text("Forgot password?", style = MaterialTheme.typography.labelSmall, color = CaramelBrown)
            }
        }

        if (state.errorMessage != null) {
            Spacer(Modifier.height(8.dp))
            Text(state.errorMessage!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(Modifier.height(24.dp))
        CrumbButton("Sign In", onClick = viewModel::login, isLoading = state.isLoading)
        Spacer(Modifier.height(20.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("New to Crumb Atelier?", style = MaterialTheme.typography.bodyMedium, color = MutedText)
            TextButton(onClick = onNavigateToRegister) {
                Text("Create an account", style = MaterialTheme.typography.labelLarge, color = CaramelBrown)
            }
        }
        Spacer(Modifier.height(40.dp))
    }
}

// ── RegisterScreen ──────────────────────────────────────────────────

@Composable
fun RegisterScreen(onRegisterSuccess: () -> Unit, onNavigateToLogin: () -> Unit, viewModel: RegisterViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel) { viewModel.onRegisterSuccess = onRegisterSuccess }

    Column(modifier = Modifier.fillMaxSize().background(CreamBackground).verticalScroll(rememberScrollState()).padding(horizontal = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(60.dp))
        Text("Join Crumb Atelier", style = MaterialTheme.typography.displayMedium, color = ChocolateBrown, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        Text("Create a free account to explore our recipe archive", style = MaterialTheme.typography.bodyMedium, color = MutedText, textAlign = TextAlign.Center)
        Spacer(Modifier.height(40.dp))
        CrumbTextField(state.name,  viewModel::onNameChange,  "Full Name")
        Spacer(Modifier.height(16.dp))
        CrumbTextField(state.email, viewModel::onEmailChange, "Email address")
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(value = state.password, onValueChange = viewModel::onPasswordChange, label = { Text("Password (min. 6 characters)") },
            modifier = Modifier.fillMaxWidth(), singleLine = true, visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CaramelBrown, unfocusedBorderColor = DividerColor, focusedLabelColor = CaramelBrown, unfocusedLabelColor = MutedText, cursorColor = CaramelBrown, focusedTextColor = BrownText, unfocusedTextColor = BrownText)
        )
        if (state.errorMessage != null) { Spacer(Modifier.height(12.dp)); Text(state.errorMessage!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
        Spacer(Modifier.height(12.dp))
        Text("New accounts are created as Viewer by default.", style = MaterialTheme.typography.bodySmall, color = SubtleText, textAlign = TextAlign.Center)
        Spacer(Modifier.height(28.dp))
        CrumbButton("Create Account", onClick = viewModel::register, isLoading = state.isLoading)
        Spacer(Modifier.height(20.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Already have an account?", style = MaterialTheme.typography.bodyMedium, color = MutedText)
            TextButton(onClick = onNavigateToLogin) { Text("Sign in", style = MaterialTheme.typography.labelLarge, color = CaramelBrown) }
        }
        Spacer(Modifier.height(40.dp))
    }
}
