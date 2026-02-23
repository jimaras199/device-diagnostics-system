package com.jimaras199.devicediagnostics.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jimaras199.devicediagnostics.settings.ServerSettings
import com.jimaras199.devicediagnostics.settings.ServerSettingsStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ServerSettingsUiState(
    val scheme: String = "http",
    val host: String = "10.0.2.2",
    val port: String = "5275",
    val error: String? = null
)

class ServerSettingsViewModel(
    private val store: ServerSettingsStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(ServerSettingsUiState())
    val uiState: StateFlow<ServerSettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            store.settingsFlow.collect { s ->
                _uiState.value = ServerSettingsUiState(
                    scheme = s.scheme,
                    host = s.host,
                    port = s.port
                )
            }
        }
    }
    fun updateHost(v: String) { _uiState.value = _uiState.value.copy(host = v, error = null) }
    fun updatePort(v: String) { _uiState.value = _uiState.value.copy(port = v, error = null) }
    fun updateScheme(v: String) { _uiState.value = _uiState.value.copy(scheme = v, error = null) }
    fun save(onDone: () -> Unit) {
        viewModelScope.launch {
            val s = _uiState.value
            val hostOk = s.host.trim().isNotBlank()
            val portInt = s.port.trim().toIntOrNull()
            val portOk = portInt != null && portInt in 1..65535

            if (!hostOk) {
                _uiState.value = s.copy(error = "Host is required.")
                return@launch
            }
            if (!portOk) {
                _uiState.value = s.copy(error = "Port must be 1–65535.")
                return@launch
            }

            store.save(ServerSettings(s.scheme, s.host, s.port))
            _uiState.value = s.copy(error = null)
            onDone()
        }
    }
}
