package com.jimaras199.devicediagnostics.ui.screens.deviceDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jimaras199.devicediagnostics.data.model.DeviceDto
import com.jimaras199.devicediagnostics.data.repository.DevicesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface DeviceDetailsUiState {
    data object Loading : DeviceDetailsUiState
    data class Error(val message: String) : DeviceDetailsUiState
    data class Success(val device: DeviceDto) : DeviceDetailsUiState
}

class DeviceDetailsViewModel(
    private val repo: DevicesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DeviceDetailsUiState>(DeviceDetailsUiState.Loading)
    val uiState: StateFlow<DeviceDetailsUiState> = _uiState.asStateFlow()

    fun load(deviceId: Int) {
        _uiState.value = DeviceDetailsUiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val device = repo.getDevice(deviceId)
                _uiState.value = DeviceDetailsUiState.Success(device)
            } catch (ex: Exception) {
                _uiState.value = DeviceDetailsUiState.Error(ex.localizedMessage ?: ex.toString())
            }
        }
    }
}
