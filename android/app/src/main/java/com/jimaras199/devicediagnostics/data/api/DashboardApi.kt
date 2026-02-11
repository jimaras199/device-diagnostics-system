package com.jimaras199.devicediagnostics.data.api

import com.jimaras199.devicediagnostics.data.model.DeviceDashboardDto
import retrofit2.http.GET
import retrofit2.http.Query

interface DashboardApi {

    @GET("dashboard/devices")
    suspend fun getDevicesDashboard(
        @Query("metricsPerDevice") metricsPerDevice: Int = 5
    ): List<DeviceDashboardDto>
}
