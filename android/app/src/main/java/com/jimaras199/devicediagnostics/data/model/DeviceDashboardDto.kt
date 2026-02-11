package com.jimaras199.devicediagnostics.data.model

data class DeviceDashboardDto(
    val id: Int,
    val name: String,
    val model: String?,
    val lastSeenUtc: String,
    val latestMetrics: List<MetricSnapshotDto>
)

data class MetricSnapshotDto(
    val metricName: String,
    val value: Double,
    val timestampUtc: String
)
