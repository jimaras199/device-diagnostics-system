package com.jimaras199.devicediagnostics.data.repository

import com.jimaras199.devicediagnostics.data.api.DashboardApi
import com.jimaras199.devicediagnostics.data.model.DeviceDashboardDto

class DashboardRepositoryImpl(
    private val api: DashboardApi
) : DashboardRepository {
    override suspend fun getDevicesDashboard(metricsPerDevice: Int): List<DeviceDashboardDto> =
        api.getDevicesDashboard(metricsPerDevice)
}
