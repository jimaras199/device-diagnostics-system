package com.jimaras199.devicediagnostics.ui.screens.deviceDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jimaras199.devicediagnostics.data.repository.DevicesRepository
import com.jimaras199.devicediagnostics.data.repository.EventsRepository
import com.jimaras199.devicediagnostics.data.repository.TelemetryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface DeviceDetailsUiState {
    data object Loading : DeviceDetailsUiState
    data class Error(val message: String) : DeviceDetailsUiState
    data class Success(val data: DeviceDetailsData) : DeviceDetailsUiState
}

class DeviceDetailsViewModel(
    private val devicesRepo: DevicesRepository,
    private val telemetryRepo: TelemetryRepository,
    private val eventsRepo: EventsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DeviceDetailsUiState>(DeviceDetailsUiState.Loading)
    val uiState: StateFlow<DeviceDetailsUiState> = _uiState.asStateFlow()

    fun load(deviceId: Int) {
        _uiState.value = DeviceDetailsUiState.Loading

        viewModelScope.launch {
            try {
                val data = withContext(Dispatchers.IO) {
                    val device = devicesRepo.getDevice(deviceId)
                    val telemetry = telemetryRepo.getTelemetry(deviceId)
                    val events = eventsRepo.getEvents(deviceId)
                    DeviceDetailsData(device, telemetry, events)
                }

                _uiState.value = DeviceDetailsUiState.Success(data)
            } catch (ex: Exception) {
                _uiState.value = DeviceDetailsUiState.Error(ex.localizedMessage ?: ex.toString())
            }
        }
    }
}
