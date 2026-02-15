package com.jimaras199.devicediagnostics.auth

import okhttp3.Interceptor
import okhttp3.Response

class UnauthorizedInterceptor(
    private val handler: UnauthorizedHandler
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        val path = request.url.encodedPath
        val isAuthEndpoint = path.startsWith("/auth")

        if (!isAuthEndpoint && response.code == 401) {
            handler.onUnauthorized()
        }

        return response
    }
}
