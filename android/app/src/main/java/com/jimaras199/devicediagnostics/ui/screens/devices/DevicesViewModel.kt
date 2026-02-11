package com.jimaras199.devicediagnostics.ui.screens.devices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jimaras199.devicediagnostics.data.api.ApiClient
import com.jimaras199.devicediagnostics.data.model.toDeviceListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DevicesViewModel : ViewModel() {

    private val api = ApiClient.createDashboardApi()
    private val _uiState = MutableStateFlow<DevicesUiState>(DevicesUiState.Loading)
    val uiState: StateFlow<DevicesUiState> = _uiState.asStateFlow()

    init { refresh() }

    fun refresh() {
        val current = _uiState.value

        if (current is DevicesUiState.Success) {
            _uiState.value = current.copy(isRefreshing = true)
        } else {
            _uiState.value = DevicesUiState.Loading
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val dtoItems = api.getDevicesDashboard(metricsPerDevice = 5)
                val uiItems = dtoItems.map { it.toDeviceListItem() }

                _uiState.value = DevicesUiState.Success(
                    devices = uiItems,
                    isRefreshing = false
                )
            } catch (ex: Exception) {
                val now = _uiState.value
                if (now is DevicesUiState.Success) {
                    _uiState.value = now.copy(isRefreshing = false)
                } else {
                    _uiState.value = DevicesUiState.Error(ex.localizedMessage ?: ex.toString())
                }
            }
        }
    }
}