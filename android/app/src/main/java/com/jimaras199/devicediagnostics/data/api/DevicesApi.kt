package com.jimaras199.devicediagnostics.data.api

import com.jimaras199.devicediagnostics.data.model.DeviceDto
import retrofit2.http.GET
import retrofit2.http.Path

interface DevicesApi {
    @GET("devices/{id}")
    suspend fun getDevice(@Path("id") id: Int): DeviceDto
}
