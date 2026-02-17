package com.jimaras199.devicediagnostics.data.repository

import com.jimaras199.devicediagnostics.data.api.TelemetryApi
import com.jimaras199.devicediagnostics.data.model.TelemetryDto

class TelemetryRepositoryImpl(private val api: TelemetryApi) : TelemetryRepository {
    override suspend fun getTelemetry(deviceId: Int): List<TelemetryDto> =
        api.getTelemetry(deviceId)
}
