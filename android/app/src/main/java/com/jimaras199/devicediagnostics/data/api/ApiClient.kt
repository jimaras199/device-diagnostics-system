package com.jimaras199.devicediagnostics.data.api

import com.jimaras199.devicediagnostics.auth.AuthInterceptor
import com.jimaras199.devicediagnostics.auth.TokenProvider
import com.jimaras199.devicediagnostics.auth.UnauthorizedHandler
import com.jimaras199.devicediagnostics.auth.UnauthorizedInterceptor
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import com.jimaras199.devicediagnostics.BuildConfig

object ApiClient {

    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    fun buildOkHttp(
        tokenProvider: TokenProvider,
        unauthorizedHandler: UnauthorizedHandler,
        baseUrlProvider: () -> String
    ): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        logging.redactHeader("Authorization")

        return OkHttpClient.Builder()
            .addInterceptor(BaseUrlInterceptor { baseUrlProvider().toHttpUrl() })
            .addInterceptor(AuthInterceptor(tokenProvider))
            .addInterceptor(UnauthorizedInterceptor(unauthorizedHandler))
            .addInterceptor(logging)
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .callTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    fun buildRetrofit(
        baseUrl: String,
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    fun createDashboardApi(retrofit: Retrofit): DashboardApi = retrofit.create(DashboardApi::class.java)
    fun createDevicesApi(retrofit: Retrofit): DevicesApi = retrofit.create(DevicesApi::class.java)
    fun createAuthApi(retrofit: Retrofit): AuthApi = retrofit.create(AuthApi::class.java)
    fun createTelemetryApi(retrofit: Retrofit): TelemetryApi = retrofit.create(TelemetryApi::class.java)
    fun createEventsApi(retrofit: Retrofit): EventsApi = retrofit.create(EventsApi::class.java)
    fun createDemoApi(retrofit: Retrofit): DemoApi = retrofit.create(DemoApi::class.java)
}
