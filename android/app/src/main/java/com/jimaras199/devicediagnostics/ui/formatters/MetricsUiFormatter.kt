package com.jimaras199.devicediagnostics.ui.formatters

data class MetricUi(
    val label: String,
    val valueText: String
)

object MetricsUiFormatter {
    fun metricUi(metricName: String, value: Double): MetricUi? {
        return when (metricName.lowercase()) {
            "battery_pct" -> MetricUi("Battery", "${value.toInt()}%")
            "temp_c" -> MetricUi("Temp", "${"%.1f".format(value)}°C")
            "signal_dbm" -> MetricUi("Signal", "${value.toInt()} dBm")
            "cpu_pct" -> MetricUi("CPU", "${value.toInt()}%")
            else -> null
        }
    }
}