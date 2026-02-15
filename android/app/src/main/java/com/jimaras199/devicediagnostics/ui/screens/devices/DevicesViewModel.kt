package com.jimaras199.devicediagnostics.ui.screens.devices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jimaras199.devicediagnostics.data.model.toDeviceListItem
import com.jimaras199.devicediagnostics.data.repository.DashboardRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.jimaras199.devicediagnostics.ui.events.DevicesUiEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class DevicesViewModel(
    private val repo: DashboardRepository
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
                val uiItems = withContext(Dispatchers.IO) {
                    repo.getDevicesDashboard(metricsPerDevice = 5)
                        .map { it.toDeviceListItem() }
                }

                _uiState.value = DevicesUiState.Success(
                    devices = uiItems,
                    isRefreshing = false,
                )
            } catch (ex: Exception) {
                val msg = ex.localizedMessage ?: ex.toString()
                val now = _uiState.value

                if (now is DevicesUiState.Success) {
                    _uiState.value = now.copy(isRefreshing = false)
                    _events.tryEmit(DevicesUiEvent.ShowRefreshError(msg))
                } else {
                    _uiState.value = DevicesUiState.Error(msg)
                }            }
        }
    }
}
