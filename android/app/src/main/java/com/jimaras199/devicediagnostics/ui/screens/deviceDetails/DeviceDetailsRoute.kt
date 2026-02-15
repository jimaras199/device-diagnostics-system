package com.jimaras199.devicediagnostics.ui.screens.deviceDetails

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jimaras199.devicediagnostics.di.AppContainer
import com.jimaras199.devicediagnostics.di.DeviceDetailsViewModelFactory
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@Composable
fun DeviceDetailsRoute(container: AppContainer, deviceId: Int) {

    val vm: DeviceDetailsViewModel = viewModel(
        factory = DeviceDetailsViewModelFactory(container.devicesRepository)
    )

    val state by vm.uiState.collectAsState()

    LaunchedEffect(deviceId) {
        vm.load(deviceId)
    }

    val scope = rememberCoroutineScope()
    DeviceDetailsScreen(state = state, onRetry = { vm.load(deviceId)}, onLogout = { scope.launch { container.authRepository.logout() } })
}
