package com.jimaras199.devicediagnostics.auth

class InMemoryTokenProvider : TokenProvider {
    private var token: String? = null

    fun setToken(value: String?) {
        token = value
    }

    override fun getToken(): String? = token
}
