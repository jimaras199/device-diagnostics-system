package com.jimaras199.devicediagnostics.ui.screens.deviceDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jimaras199.devicediagnostics.data.network.NetworkErrorMapper
import com.jimaras199.devicediagnostics.data.repository.DevicesRepository
import com.jimaras199.devicediagnostics.data.repository.EventsRepository
import com.jimaras199.devicediagnostics.data.repository.TelemetryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface DeviceDetailsUiState {
    data object Loading : DeviceDetailsUiState
    data class Error(val message: String) : DeviceDetailsUiState
    data class Success(val data: DeviceDetailsUi) : DeviceDetailsUiState
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
                    coroutineScope {
                        val deviceDeferred = async { devicesRepo.getDevice(deviceId) }
                        val telemetryDeferred = async { telemetryRepo.getTelemetry(deviceId) }
                        val eventsDeferred = async { eventsRepo.getEvents(deviceId) }
                        val device = deviceDeferred.await()
                        val telemetry = telemetryDeferred.await()
                        val events = eventsDeferred.await()

                        DeviceDetailsUi(
                            header = device.toHeaderUi(),
                            latestMetrics = telemetry.toLatestMetricsUi(),
                            telemetries = telemetry.toTelemetryItemUiList(),
                            events = events.toEventItemUiList()
                        )
                    }
                }

                _uiState.value = DeviceDetailsUiState.Success(data)
            } catch (ex: Exception) {
                val msg = NetworkErrorMapper.message(ex, NetworkErrorMapper.Context.GenericLoad)
                _uiState.value = DeviceDetailsUiState.Error(msg)
            }
        }
    }
}
