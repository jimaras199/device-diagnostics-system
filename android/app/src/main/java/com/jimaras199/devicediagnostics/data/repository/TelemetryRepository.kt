package com.jimaras199.devicediagnostics.data.repository

import com.jimaras199.devicediagnostics.data.model.TelemetryDto

interface TelemetryRepository {
    suspend fun getTelemetry(deviceId: Int): List<TelemetryDto>
}
