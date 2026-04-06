package com.jimaras199.devicediagnostics.ui.screens.deviceDetails

data class DeviceDetailsUi(
    val header: DeviceHeaderUi,
    val latestMetrics: List<MetricSummaryUi>,
    val telemetries: List<TelemetryItemUi>,
    val events: List<EventItemUi>
)

data class DeviceHeaderUi(
    val title: String,
    val subtitle: String
)

data class MetricSummaryUi(
    val label: String,
    val valueText: String
)

data class TelemetryItemUi(
    val label: String,
    val valueText: String,
    val timestampText: String
)

data class EventItemUi(
    val message: String,
    val levelText: String,
    val timestampText: String,
    val levelStyle: EventLevelUi
)

enum class EventLevelUi {
    INFO, WARNING, ERROR
}