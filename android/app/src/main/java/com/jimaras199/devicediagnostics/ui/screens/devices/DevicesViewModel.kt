package com.jimaras199.devicediagnostics

import androidx.lifecycle.ViewModel
import com.jimaras199.devicediagnostics.ui.models.DeviceListItem
import com.jimaras199.devicediagnostics.ui.screens.devices.DevicesUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class DevicesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<DevicesUiState>(DevicesUiState.Loading)
    val uiState: StateFlow<DevicesUiState> = _uiState.asStateFlow()

    init {
        _uiState.value = DevicesUiState.Success(
            listOf(
                DeviceListItem(1, "Living Room Sensor", "ESP32", "2026-01-26T18:20:00Z"),
                DeviceListItem(2, "Garage Gateway", "RPI", "2026-01-26T18:18:00Z"),
                DeviceListItem(3, "Office Phone", "OnePlus", "2026-01-26T18:10:00Z")
            )
        )
    }
}
