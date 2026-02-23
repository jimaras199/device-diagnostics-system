package com.jimaras199.devicediagnostics.ui.screens.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jimaras199.devicediagnostics.di.AppContainer
import com.jimaras199.devicediagnostics.di.LoginViewModelFactory
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.collectLatest
import com.jimaras199.devicediagnostics.auth.AuthEvent
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember

@Composable
fun LoginRoute(
    container: AppContainer,
    onOpenSettings: () -> Unit
) {
    val vm: LoginViewModel = viewModel(
        factory = LoginViewModelFactory(repo = container.authRepository)
    )

    val state by vm.uiState.collectAsState()
    val baseUrl by container.baseUrl.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        vm.events.collectLatest { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    LaunchedEffect(Unit) {
        container.authEvents.collectLatest { ev ->
            if (ev is AuthEvent.SessionExpired) {
                vm.showMessage("Session expired. Please login again.")
            }
        }
    }

    LoginScreen(
        state = state,
        baseUrl = baseUrl,
        snackbarHostState = snackbarHostState,
        onSubmit = { mode, email, pass ->
            vm.submit(mode, email, password = pass)
        },
        onOpenSettings = onOpenSettings
    )
}