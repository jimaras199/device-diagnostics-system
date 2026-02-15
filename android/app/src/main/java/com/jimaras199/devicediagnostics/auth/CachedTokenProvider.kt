package com.jimaras199.devicediagnostics.auth

import kotlinx.coroutines.flow.StateFlow

class CachedTokenProvider(
    private val tokenState: StateFlow<String?>
) : TokenProvider {
    override fun getToken(): String? = tokenState.value
}
