package com.jimaras199.devicediagnostics.ui.events

sealed interface DevicesUiEvent {
    data class ShowSnackbar(
        val message: String,
        val actionLabel: String? = null,
        val retryOnAction: Boolean = false
    ) : DevicesUiEvent
}