package com.jimaras199.devicediagnostics.ui.screens.deviceDetails

import com.jimaras199.devicediagnostics.data.model.DeviceDto
import com.jimaras199.devicediagnostics.data.model.EventDto
import com.jimaras199.devicediagnostics.data.model.TelemetryDto
import com.jimaras199.devicediagnostics.ui.formatters.MetricsUiFormatter
import com.jimaras199.devicediagnostics.ui.util.formatUtcTimestamp

internal fun DeviceDto.toHeaderUi (): DeviceHeaderUi {
    val subtitle = buildList {
        model?.takeIf { it.isNotBlank() }?.let { add(it) }
        add("Last seen: ${formatUtcTimestamp(lastSeenUtc)}")
    }.joinToString(" • ")

    return DeviceHeaderUi(
        title = name,
        subtitle = subtitle
    )
}

internal fun List<TelemetryDto>.toLatestMetricsUi(): List<MetricSummaryUi> {
    val latestPerMetric = this
        .groupBy { it.metricName }
        .mapNotNull { (name, items) ->
            val latest = items.maxByOrNull { it.timestampUtc } ?: return@mapNotNull null
            val ui = MetricsUiFormatter.metricUi(name, latest.value) ?: return@mapNotNull null

            MetricSummaryUi(
                label = ui.label,
                valueText = ui.valueText
            )
        }
        .sortedBy { it.label.lowercase() }
    return latestPerMetric
}

internal fun List<TelemetryDto>.toTelemetryItemUiList(): List<TelemetryItemUi> {
    return this.map{
        val ui = MetricsUiFormatter.metricUi(it.metricName, it.value)
        TelemetryItemUi(
            label = ui?.label ?: it.metricName,
            valueText = ui?.valueText ?: it.value.toString(),
            timestampText = formatUtcTimestamp(it.timestampUtc)
        )
    }
}

internal fun String.toEventLevelUi(): EventLevelUi {
    return when (this.lowercase()) {
        "error" -> EventLevelUi.ERROR
        "warning" -> EventLevelUi.WARNING
        else -> EventLevelUi.INFO
    }
}

internal fun List<EventDto>.toEventItemUiList(): List<EventItemUi> {
    return this.map{
        EventItemUi(
            message = it.message,
            levelText = it.level.uppercase(),
            timestampText = formatUtcTimestamp(it.timestampUtc),
            levelStyle = it.level.toEventLevelUi()
        )
    }
}