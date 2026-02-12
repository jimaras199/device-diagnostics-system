package com.jimaras199.devicediagnostics.data.repository

import com.jimaras199.devicediagnostics.data.model.DeviceDashboardDto

interface DashboardRepository {
    suspend fun getDevicesDashboard(metricsPerDevice: Int): List<DeviceDashboardDto>
}
