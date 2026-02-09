package com.jimaras199.devicediagnostics.ui.screens.devices

import com.jimaras199.devicediagnostics.ui.models.DeviceListItem

sealed interface DevicesUiState {
    data object Loading : DevicesUiState
    data class Success(val devices: List<DeviceListItem>) : DevicesUiState
    data class Error(val message: String) : DevicesUiState
}
