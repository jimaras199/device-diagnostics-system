package com.jimaras199.devicediagnostics.ui.screens.devices

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jimaras199.devicediagnostics.di.AppContainer
import com.jimaras199.devicediagnostics.di.DevicesViewModelFactory
import com.jimaras199.devicediagnostics.ui.events.DevicesUiEvent

@Composable
fun DevicesRoute(
    container: AppContainer,
    onDeviceClick: (Int, String) -> Unit
) {
    val vm: DevicesViewModel = viewModel(
        factory = DevicesViewModelFactory(container.dashboardRepository)
    )

    val state by vm.uiState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(vm) {
        vm.events.collect { event ->
            when (event) {
                is DevicesUiEvent.ShowRefreshError -> {
                    val result = snackbarHostState.showSnackbar(
                        message = "Refresh failed",
                        actionLabel = "Retry"
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        vm.refresh()
                    }
                }
            }
        }
    }

    DevicesScreen(
        state = state,
        onDeviceClick = onDeviceClick,
        onRefresh = { vm.refresh() },
        snackbarHostState = snackbarHostState
    )
}
