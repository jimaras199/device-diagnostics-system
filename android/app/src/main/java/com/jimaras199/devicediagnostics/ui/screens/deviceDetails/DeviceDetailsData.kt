package com.jimaras199.devicediagnostics.ui.screens.deviceDetails

import com.jimaras199.devicediagnostics.data.model.DeviceDto
import com.jimaras199.devicediagnostics.data.model.EventDto
import com.jimaras199.devicediagnostics.data.model.TelemetryDto

data class DeviceDetailsData(
    val device: DeviceDto,
    val telemetry: List<TelemetryDto>,
    val events: List<EventDto>
)
