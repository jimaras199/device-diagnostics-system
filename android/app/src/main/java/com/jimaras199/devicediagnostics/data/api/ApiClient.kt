package com.jimaras199.devicediagnostics.data.api

import com.jimaras199.devicediagnostics.auth.AuthInterceptor
import com.jimaras199.devicediagnostics.auth.TokenProvider
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object ApiClient {

    private const val BASE_URL = "http://192.168.68.54:5275/"

    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    private fun buildOkHttp(tokenProvider: TokenProvider): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenProvider))
            .addInterceptor(logging)
            .build()
    }

    private fun buildRetrofit(tokenProvider: TokenProvider): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(buildOkHttp(tokenProvider))
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    fun createDashboardApi(tokenProvider: TokenProvider): DashboardApi =
        buildRetrofit(tokenProvider).create(DashboardApi::class.java)

    fun createDevicesApi(tokenProvider: TokenProvider): DevicesApi =
        buildRetrofit(tokenProvider).create(DevicesApi::class.java)
}
