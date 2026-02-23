package com.jimaras199.devicediagnostics.auth

sealed interface AuthEvent {
    data object SessionExpired : AuthEvent
}