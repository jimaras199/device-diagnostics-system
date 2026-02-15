package com.jimaras199.devicediagnostics.auth

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val tokenProvider: TokenProvider
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        val token = tokenProvider.getToken()
        if (token.isNullOrBlank()) {
            return chain.proceed(original)
        }

        val authed = original.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()

        return chain.proceed(authed)
    }
}
