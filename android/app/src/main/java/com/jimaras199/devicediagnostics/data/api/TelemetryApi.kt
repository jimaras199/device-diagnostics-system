package com.jimaras199.devicediagnostics.data.api

import com.jimaras199.devicediagnostics.data.model.TelemetryDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TelemetryApi {
    @GET("devices/{deviceId}/telemetry")
    suspend fun getTelemetry(
        @Path("deviceId") deviceId: Int,
        @Query("fromUtc") fromUtc: String? = null,
        @Query("toUtc") toUtc: String? = null,
        @Query("metric") metric: String? = null
    ): List<TelemetryDto>
}
