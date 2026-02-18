package com.jimaras199.devicediagnostics.ui.screens.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(
    state: LoginUiState,
    baseUrl: String,//δβ
    onSubmit: (AuthMode, String, String) -> Unit,
    onOpenSettings: () -> Unit
) {
    var mode by remember { mutableStateOf(AuthMode.Login) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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
                onClick = { mode = AuthMode.Login },
                label = { Text("Login") }
            )
            FilterChip(
                selected = mode == AuthMode.Register,
                onClick = { mode = AuthMode.Register },
                label = { Text("Register") }
            )
        }

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(10.dp))

        Text(
            text = "Server: $baseUrl",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )


        Spacer(Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween) {
            Button(
                onClick = { onSubmit(mode, email, password) },
                enabled = state !is LoginUiState.Loading
            ) {
                Text(if (mode == AuthMode.Login) "Login" else "Register")
            }

            Spacer(Modifier.height(16.dp))

            TextButton(onClick = onOpenSettings) {
                Text("Server settings")
            }
        }

        Spacer(Modifier.height(12.dp))

        when (state) {
            LoginUiState.Idle -> Unit
            LoginUiState.Loading -> CircularProgressIndicator()
            is LoginUiState.Error -> Text(
                text = state.message,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
