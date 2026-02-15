package com.jimaras199.devicediagnostics.data.repository

interface AuthRepository {
    suspend fun register(email: String, password: String)
    suspend fun login(email: String, password: String)
    suspend fun logout()
}
