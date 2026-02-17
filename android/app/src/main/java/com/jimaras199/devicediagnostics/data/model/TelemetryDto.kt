package com.jimaras199.devicediagnostics.data.model

data class TelemetryDto(
    val id: Int,
    val metricName: String,
    val value: Double,
    val timestampUtc: String
)
