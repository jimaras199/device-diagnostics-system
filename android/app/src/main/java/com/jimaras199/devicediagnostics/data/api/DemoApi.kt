package com.jimaras199.devicediagnostics.data.api

import retrofit2.http.GET
import retrofit2.http.POST

data class DemoSeedResponseDto(
    val devicesCreated: Int,
    val telemetryCreated: Int,
    val eventsCreated: Int
)
data class DemoStatusDto(
    val seeded: Boolean
)
interface DemoApi {
    @POST("users/me/demo/seed")
    suspend fun seedDemo(): DemoSeedResponseDto

    @GET("users/me/demo/status")
    suspend fun getStatus(): DemoStatusDto
}