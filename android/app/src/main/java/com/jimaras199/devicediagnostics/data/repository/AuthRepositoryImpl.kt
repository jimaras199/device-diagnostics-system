package com.jimaras199.devicediagnostics.data.repository

import com.jimaras199.devicediagnostics.auth.TokenStore
import com.jimaras199.devicediagnostics.data.api.AuthApi
import com.jimaras199.devicediagnostics.data.api.LoginRequest
import com.jimaras199.devicediagnostics.data.api.RegisterRequest

class AuthRepositoryImpl(
    private val api: AuthApi,
    private val tokenStore: TokenStore
) : AuthRepository {

    override suspend fun register(email: String, password: String) {
        val res = api.register(RegisterRequest(email.trim(), password))
        tokenStore.setToken(res.accessToken)
    }

    override suspend fun login(email: String, password: String) {
        val res = api.login(LoginRequest(email.trim(), password))
        tokenStore.setToken(res.accessToken)
    }

    override suspend fun logout() {
        tokenStore.setToken(null)
    }
}
