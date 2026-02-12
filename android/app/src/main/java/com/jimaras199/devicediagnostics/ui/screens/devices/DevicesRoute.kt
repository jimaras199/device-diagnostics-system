package com.jimaras199.devicediagnostics.ui.screens.devices

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jimaras199.devicediagnostics.di.AppContainer
import com.jimaras199.devicediagnostics.di.DevicesViewModelFactory

@Composable
fun DevicesRoute(
    container: AppContainer,
    onDeviceClick: (Int, String) -> Unit
) {
    val vm: DevicesViewModel = viewModel(
        factory = DevicesViewModelFactory(container.dashboardRepository)
    )

    val state by vm.uiState.collectAsState()
    DevicesScreen(
        state = state,
        onDeviceClick = onDeviceClick,
        onRefresh = { vm.refresh() }
    )
}