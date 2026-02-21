package com.jimaras199.devicediagnostics.data.model

import com.jimaras199.devicediagnostics.ui.models.DeviceListItem

fun DeviceDashboardDto.toDeviceListItem(): DeviceListItem {
    val parts = latestMetrics
        .groupBy { it.metricName }
        .map { (_, values) ->
            values.maxByOrNull { it.timestampUtc }!!
        }
        .sortedBy { it.metricName.lowercase() }
        .mapNotNull { formatMetric(it.metricName, it.value) }
        .take(3)

    val metricsText = parts.takeIf { it.isNotEmpty() }?.joinToString(" • ")

    return DeviceListItem(
        id = id,
        name = name,
        model = model,
        lastSeenUtc = lastSeenUtc,
        latestMetricsText = metricsText
    )
}
private fun formatMetric(metricName: String, value: Double): String? {
    val key = metricName.lowercase()

    return when (key) {
        "battery_pct" -> "Battery ${value.toInt()}%"
        "temp_c" -> "Temp ${"%.1f".format(value)}°C"
        "signal_dbm" -> "Signal ${value.toInt()} dBm"
        "cpu_pct" -> "CPU ${value.toInt()}%"
        else -> null
    }
}