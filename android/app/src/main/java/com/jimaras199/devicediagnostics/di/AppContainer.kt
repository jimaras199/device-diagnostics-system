package com.jimaras199.devicediagnostics.di

import com.jimaras199.devicediagnostics.data.api.ApiClient
import com.jimaras199.devicediagnostics.data.api.DashboardApi
import com.jimaras199.devicediagnostics.data.api.DevicesApi

class AppContainer {
    val dashboardApi: DashboardApi = ApiClient.createDashboardApi()
    val devicesApi: DevicesApi = ApiClient.createDevicesApi()
}
