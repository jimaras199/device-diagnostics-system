package com.jimaras199.devicediagnostics.ui.screens.devices

import com.jimaras199.devicediagnostics.data.model.DeviceDashboardDto
import com.jimaras199.devicediagnostics.ui.formatters.MetricsUiFormatter
import com.jimaras199.devicediagnostics.ui.models.DeviceListItem

fun DeviceDashboardDto.toDeviceListItem(): DeviceListItem {
    val parts = latestMetrics
        .groupBy { it.metricName }
        .map { (_, values) ->
            values.maxByOrNull { it.timestampUtc }!!
        }
        .sortedBy { it.metricName.lowercase() }
        .mapNotNull {
            MetricsUiFormatter.metricUi(it.metricName, it.value)?.let { m ->
                "${m.label} ${m.valueText}"
            }
        }
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