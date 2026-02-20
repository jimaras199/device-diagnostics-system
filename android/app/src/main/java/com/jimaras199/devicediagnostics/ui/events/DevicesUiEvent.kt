package com.jimaras199.devicediagnostics.ui.events

sealed interface DevicesUiEvent {
    data class ShowRefreshError(val message: String) : DevicesUiEvent
    data class ShowMessage(val message: String) : DevicesUiEvent
}
