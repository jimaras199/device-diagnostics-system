package com.jimaras199.devicediagnostics.ui.events

sealed interface DevicesUiEvent {
    data class ShowSnackbar(
        val message: String,
        val actionLabel: String? = null
    ) : DevicesUiEvent
}