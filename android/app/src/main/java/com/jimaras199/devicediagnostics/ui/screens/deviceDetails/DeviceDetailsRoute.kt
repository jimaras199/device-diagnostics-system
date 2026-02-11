package com.jimaras199.devicediagnostics.ui.screens.deviceDetails

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jimaras199.devicediagnostics.data.api.ApiClient
import com.jimaras199.devicediagnostics.data.repository.DevicesRepositoryImpl

@Composable
fun DeviceDetailsRoute(deviceId: Int) {
    val repo = DevicesRepositoryImpl(ApiClient.createDevicesApi())

    val vm: DeviceDetailsViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return DeviceDetailsViewModel(repo) as T
            }
        }
    )

    val state by vm.uiState.collectAsState()

    LaunchedEffect(deviceId) {
        vm.load(deviceId)
    }

    DeviceDetailsScreen(state = state)
}
