package com.jimaras199.devicediagnostics.ui.screens.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jimaras199.devicediagnostics.di.AppContainer
import com.jimaras199.devicediagnostics.di.LoginViewModelFactory

@Composable
fun LoginRoute(
    container: AppContainer,
    onOpenSettings: () -> Unit
) {
    val vm: LoginViewModel = viewModel(
        factory = LoginViewModelFactory(repo = container.authRepository)
    )

    val state by vm.uiState.collectAsState()
    val baseUrl by container.baseUrl.collectAsState() //δβ


    LoginScreen(
        state = state,
        baseUrl = baseUrl,
        onSubmit = { mode, email, pass ->
            vm.submit(mode, email, password = pass)
        },
        onOpenSettings = onOpenSettings
    )
}
