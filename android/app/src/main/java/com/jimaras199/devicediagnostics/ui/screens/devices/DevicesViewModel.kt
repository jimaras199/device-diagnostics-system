package com.jimaras199.devicediagnostics.ui.screens.devices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jimaras199.devicediagnostics.data.model.toDeviceListItem
import com.jimaras199.devicediagnostics.data.repository.DashboardRepository
import com.jimaras199.devicediagnostics.data.repository.DemoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.jimaras199.devicediagnostics.ui.events.DevicesUiEvent
import com.jimaras199.devicediagnostics.ui.models.DeviceListItem
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class DevicesViewModel(
    private val repo: DashboardRepository,
    private val demoRepo: DemoRepository
) : ViewModel() {

    private val _events = MutableSharedFlow<DevicesUiEvent>(
        replay = 0,
        extraBufferCapacity = 1
    )
    val events: SharedFlow<DevicesUiEvent> = _events.asSharedFlow()

    private val _uiState = MutableStateFlow<DevicesUiState>(DevicesUiState.Loading)
    val uiState: StateFlow<DevicesUiState> = _uiState.asStateFlow()

    init { refresh() }

    fun refresh() {
        val current = _uiState.value

        _uiState.value = when (current) {
            is DevicesUiState.Success -> current.copy(isRefreshing = true)
            else -> DevicesUiState.Loading
        }

        viewModelScope.launch {
            try {
                val uiItems = loadDevices()

                val previous = current as? DevicesUiState.Success

                _uiState.value = DevicesUiState.Success(
                    devices = uiItems,
                    isRefreshing = false,
                    demoSeeded = previous?.demoSeeded == true,
                    isSeedingDemo = false
                )
            } catch (ex: Exception) {
                val msg = ex.localizedMessage ?: ex.toString()
                val now = _uiState.value

                if (now is DevicesUiState.Success) {
                    _uiState.value = now.copy(isRefreshing = false)
                    _events.tryEmit(DevicesUiEvent.ShowRefreshError(msg))
                } else {
                    _uiState.value = DevicesUiState.Error(msg)
                }
            }
        }
    }


    fun seedDemo() {
        val current = _uiState.value
        if (current is DevicesUiState.Success && current.isSeedingDemo) return

        _uiState.value = when (current) {
            is DevicesUiState.Success -> current.copy(isSeedingDemo = true)
            else -> DevicesUiState.Loading
        }

        viewModelScope.launch {
            try {
                demoRepo.seedDemo()

                _events.tryEmit(
                    DevicesUiEvent.ShowMessage("Demo data loaded")
                )

                refreshWithDemoFlag()

            } catch (ex: Exception) {

                val http = ex as? retrofit2.HttpException

                if (http?.code() == 409) {
                    _events.tryEmit(
                        DevicesUiEvent.ShowMessage("Demo already loaded")
                    )
                    refreshWithDemoFlag()
                    return@launch
                }

                val msg = mapDemoSeedError(ex)
                val now = _uiState.value

                if (now is DevicesUiState.Success) {
                    _uiState.value = now.copy(isSeedingDemo = false)
                }

                _events.tryEmit(DevicesUiEvent.ShowMessage(msg))
            }
        }
    }

    private suspend fun loadDevices(): List<DeviceListItem> =
        withContext(Dispatchers.IO) {
            repo.getDevicesDashboard(metricsPerDevice = 5)
                .map { it.toDeviceListItem() }
        }

    private fun refreshWithDemoFlag() {
        viewModelScope.launch {
            val uiItems = loadDevices()
            _uiState.value = DevicesUiState.Success(
                devices = uiItems,
                isRefreshing = false,
                demoSeeded = true,
                isSeedingDemo = false
            )
        }
    }

    private fun mapDemoSeedError(ex: Exception): String {
        return when (ex) {
            is retrofit2.HttpException -> when (ex.code()) {
                401 -> "Session expired. Please login again."
                else -> "Failed to load demo data (${ex.code()})"
            }
            is java.io.IOException -> "Server unreachable. Check your connection."
            else -> ex.localizedMessage ?: "Failed to load demo data."
        }
    }
}
