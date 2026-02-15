package com.jimaras199.devicediagnostics.auth

interface TokenProvider {
    fun getToken(): String?
}
