package com.jimaras199.devicediagnostics.data.api

import com.jimaras199.devicediagnostics.data.model.EventDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface EventsApi {
    @GET("devices/{deviceId}/events")
    suspend fun getEvents(
        @Path("deviceId") deviceId: Int,
        @Query("fromUtc") fromUtc: String? = null,
        @Query("toUtc") toUtc: String? = null,
        @Query("level") level: String? = null
    ): List<EventDto>
}
