package com.jimaras199.devicediagnostics.di

import com.jimaras199.devicediagnostics.auth.InMemoryTokenProvider
import com.jimaras199.devicediagnostics.data.api.ApiClient
import com.jimaras199.devicediagnostics.data.api.DashboardApi
import com.jimaras199.devicediagnostics.data.api.DevicesApi
import com.jimaras199.devicediagnostics.data.repository.DevicesRepository
import com.jimaras199.devicediagnostics.data.repository.DevicesRepositoryImpl
import com.jimaras199.devicediagnostics.data.repository.DashboardRepository
import com.jimaras199.devicediagnostics.data.repository.DashboardRepositoryImpl

class AppContainer {
    val tokenProvider = InMemoryTokenProvider()
    val dashboardApi: DashboardApi = ApiClient.createDashboardApi(tokenProvider)
    val devicesApi: DevicesApi = ApiClient.createDevicesApi(tokenProvider)
    val devicesRepository: DevicesRepository = DevicesRepositoryImpl(devicesApi)
    val dashboardRepository: DashboardRepository = DashboardRepositoryImpl(dashboardApi)
}
