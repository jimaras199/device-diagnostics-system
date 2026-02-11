package com.jimaras199.devicediagnostics.data.repository

import com.jimaras199.devicediagnostics.data.model.DeviceDto

interface DevicesRepository {
    suspend fun getDevice(id: Int): DeviceDto
}
