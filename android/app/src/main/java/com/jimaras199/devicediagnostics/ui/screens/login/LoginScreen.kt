package com.jimaras199.devicediagnostics.ui.screens.login

import android.util.Patterns
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions

@Composable
fun LoginScreen(
    state: LoginUiState,
    baseUrl: String,
    onSubmit: (AuthMode, String, String) -> Unit,
    onOpenSettings: () -> Unit
) {
    var mode by remember { mutableStateOf(AuthMode.Login) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // local validation error (πριν φτάσουμε στο API)
    var localError by remember { mutableStateOf<String?>(null) }

    val isLoading = state is LoginUiState.Loading
    val passwordFocus = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    // Αν ξεκινήσει loading, καθάρισε localError για να μην "μπερδεύει"
    LaunchedEffect(isLoading) {
        if (isLoading) localError = null
    }

    fun validateAndSubmit() {
        val e = email.trim()
        val p = password

        val emailOk = Patterns.EMAIL_ADDRESS.matcher(e).matches()
        val passOk = p.length >= 6

        localError = when {
            e.isBlank() -> "Email is required."
            !emailOk -> "Enter a valid email."
            p.isBlank() -> "Password is required."
            !passOk -> "Password must be at least 6 characters."
            else -> null
        }

        if (localError == null && !isLoading) {
            keyboard?.hide()
            onSubmit(mode, e, p)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (mode == AuthMode.Login) "Login" else "Register",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = mode == AuthMode.Login,
                onClick = { if (!isLoading) mode = AuthMode.Login },
                enabled = !isLoading,
                label = { Text("Login") }
            )
            FilterChip(
                selected = mode == AuthMode.Register,
                onClick = { if (!isLoading) mode = AuthMode.Register },
                enabled = !isLoading,
                label = { Text("Register") }
            )
        }

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                if (localError != null) localError = null
            },
            label = { Text("Email") },
            singleLine = true,
            enabled = !isLoading,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { passwordFocus.requestFocus() }
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                if (localError != null) localError = null
            },
            label = { Text("Password") },
            singleLine = true,
            enabled = !isLoading,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { validateAndSubmit() }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(passwordFocus)
        )

        Spacer(Modifier.height(10.dp))

        Text(
            text = "Server: $baseUrl",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { validateAndSubmit() },
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                } else {
                    Text(if (mode == AuthMode.Login) "Login" else "Register")
                }
            }

            TextButton(
                onClick = onOpenSettings,
                enabled = !isLoading
            ) {
                Text("Server settings")
            }
        }

        Spacer(Modifier.height(12.dp))

        // Προτεραιότητα: local validation error, μετά API error
        when {
            localError != null -> Text(
                text = localError!!,
                color = MaterialTheme.colorScheme.error
            )
            state is LoginUiState.Error -> Text(
                text = state.message,
                color = MaterialTheme.colorScheme.error
            )
            else -> Unit
        }
    }
}
