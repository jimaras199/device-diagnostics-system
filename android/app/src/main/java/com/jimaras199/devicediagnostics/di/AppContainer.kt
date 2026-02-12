package com.jimaras199.devicediagnostics.di

import com.jimaras199.devicediagnostics.data.api.ApiClient
import com.jimaras199.devicediagnostics.data.api.DashboardApi
import com.jimaras199.devicediagnostics.data.api.DevicesApi
import com.jimaras199.devicediagnostics.data.repository.DevicesRepository
import com.jimaras199.devicediagnostics.data.repository.DevicesRepositoryImpl

class AppContainer {
    val dashboardApi: DashboardApi = ApiClient.createDashboardApi()
    val devicesApi: DevicesApi = ApiClient.createDevicesApi()
    val devicesRepository: DevicesRepository = DevicesRepositoryImpl(devicesApi)
}
