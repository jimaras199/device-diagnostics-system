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
    val host: String = "192.168.68.55",
    val port: String = "5275"
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

    fun updateScheme(v: String) { _uiState.value = _uiState.value.copy(scheme = v) }
    fun updateHost(v: String) { _uiState.value = _uiState.value.copy(host = v) }
    fun updatePort(v: String) { _uiState.value = _uiState.value.copy(port = v) }

    fun save(onDone: () -> Unit) {
        viewModelScope.launch {
            val s = _uiState.value
            store.save(ServerSettings(s.scheme, s.host, s.port))
            onDone()
        }
    }
}
