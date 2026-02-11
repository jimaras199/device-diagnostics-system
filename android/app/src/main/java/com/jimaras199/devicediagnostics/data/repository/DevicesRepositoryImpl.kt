package com.jimaras199.devicediagnostics.data.repository

import com.jimaras199.devicediagnostics.data.api.DevicesApi
import com.jimaras199.devicediagnostics.data.model.DeviceDto

class DevicesRepositoryImpl(
    private val api: DevicesApi
) : DevicesRepository {
    override suspend fun getDevice(id: Int): DeviceDto = api.getDevice(id)
}
