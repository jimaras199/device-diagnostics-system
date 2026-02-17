package com.jimaras199.devicediagnostics.data.api

import com.jimaras199.devicediagnostics.auth.AuthInterceptor
import com.jimaras199.devicediagnostics.auth.TokenProvider
import com.jimaras199.devicediagnostics.auth.UnauthorizedHandler
import com.jimaras199.devicediagnostics.auth.UnauthorizedInterceptor
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object ApiClient {

    private const val BASE_URL = "http://192.168.1.28:5275/"

    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    private fun buildOkHttp(tokenProvider: TokenProvider,
                            unauthorizedHandler: UnauthorizedHandler
    ): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenProvider))
            .addInterceptor(UnauthorizedInterceptor(unauthorizedHandler))
            .addInterceptor(logging)
            .build()
    }

    private fun buildRetrofit(tokenProvider: TokenProvider,
                              unauthorizedHandler: UnauthorizedHandler): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(buildOkHttp(tokenProvider,unauthorizedHandler))
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    fun createDashboardApi(tokenProvider: TokenProvider,unauthorizedHandler: UnauthorizedHandler): DashboardApi =
        buildRetrofit(tokenProvider,unauthorizedHandler).create(DashboardApi::class.java)

    fun createDevicesApi(tokenProvider: TokenProvider,unauthorizedHandler: UnauthorizedHandler): DevicesApi =
        buildRetrofit(tokenProvider,unauthorizedHandler).create(DevicesApi::class.java)

    fun createAuthApi(tokenProvider: TokenProvider,unauthorizedHandler: UnauthorizedHandler): AuthApi =
        buildRetrofit(tokenProvider,unauthorizedHandler).create(AuthApi::class.java)

    fun createTelemetryApi(tokenProvider: TokenProvider, unauthorizedHandler: UnauthorizedHandler): TelemetryApi =
        buildRetrofit(tokenProvider, unauthorizedHandler).create(TelemetryApi::class.java)

    fun createEventsApi(tokenProvider: TokenProvider, unauthorizedHandler: UnauthorizedHandler): EventsApi =
        buildRetrofit(tokenProvider, unauthorizedHandler).create(EventsApi::class.java)

}
