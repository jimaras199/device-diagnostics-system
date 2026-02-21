package com.jimaras199.devicediagnostics.ui.screens.deviceDetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jimaras199.devicediagnostics.ui.components.DevicesTopBar
import com.jimaras199.devicediagnostics.ui.util.formatUtcTimestamp
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Alignment
import java.time.OffsetDateTime

@Composable
fun DeviceDetailsScreen(
    state: DeviceDetailsUiState,
    onRetry: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = { DevicesTopBar(title = "Device Details", onLogout = onLogout, showDemoButton = false,demoButtonEnabled = false,onLoadDemo = {}) }
    ) { padding ->
        when (state) {
            DeviceDetailsUiState.Loading -> {
                Text(
                    text = "Loading…",
                    modifier = Modifier.padding(padding).padding(12.dp)
                )
            }

            is DeviceDetailsUiState.Error -> {
                LazyColumn(
                    modifier = Modifier.padding(padding).padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item { Text("Error: ${state.message}") }
                    item { Button(onClick = onRetry) { Text("Retry") } }
                }
            }

            is DeviceDetailsUiState.Success -> {
                val data = state.data
                val d = data.device
                val latestPerMetric = data.telemetry
                    .groupBy { it.metricName }
                    .mapNotNull { (name, items) ->
                        val latest = items.maxByOrNull { parseUtc(it.timestampUtc) ?: OffsetDateTime.MIN }
                            ?: return@mapNotNull null

                        val line = metricLine(name, latest.value) ?: return@mapNotNull null
                        line
                    }
                    .sortedBy { it.label.lowercase() }

                LazyColumn(
                    modifier = Modifier.padding(padding).padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Text(
                                    text = d.name,
                                    style = MaterialTheme.typography.titleLarge
                                )

                                val subtitleParts = buildList {
                                    d.model?.takeIf { it.isNotBlank() }?.let { add(it) }
                                    add("Last seen: ${formatUtcTimestamp(d.lastSeen)}")
                                }

                                Text(
                                    text = subtitleParts.joinToString(" • "),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    item {
                        Text("Latest metrics", style = MaterialTheme.typography.titleMedium)
                    }

                    if (latestPerMetric.isEmpty()) {
                        item {
                            Text(
                                "No metrics yet",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    } else {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Column(Modifier.padding(12.dp)) {
                                    latestPerMetric.forEach { m ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(m.label, style = MaterialTheme.typography.bodyMedium)
                                            Text(m.valueText, style = MaterialTheme.typography.bodyMedium)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item { Spacer(Modifier.height(8.dp)) }

                    item {
                        Text("Telemetry", style = MaterialTheme.typography.titleMedium)
                    }

                    if (data.telemetry.isEmpty()) {
                        item {
                            Text(
                                "No telemetry yet",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    } else {
                        items(data.telemetry.take(10)) { t ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Column(
                                    Modifier.padding(12.dp)
                                ) {
                                    val line = metricLine(t.metricName, t.value)
                                    val label = line?.label ?: t.metricName
                                    val valueText = line?.valueText ?: t.value.toString()

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.titleSmall
                                        )
                                        Text(
                                            text = valueText,
                                            style = MaterialTheme.typography.titleSmall
                                        )
                                    }

                                    Text(
                                        text = formatUtcTimestamp(t.timestampUtc),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    item { Spacer(Modifier.height(8.dp)) }

                    item {
                        Text("Events", style = MaterialTheme.typography.titleMedium)
                    }

                    if (data.events.isEmpty()) {
                        item {
                            Text(
                                "No events yet",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    } else {
                        items(data.events.take(10)) { e ->
                            val levelColor = when (e.level.lowercase()) {
                                "error" -> Color.Red
                                "warning" -> Color(0xFFFFA500)
                                else -> MaterialTheme.colorScheme.primary
                            }

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Column(
                                    Modifier.padding(12.dp)
                                ) {
                                    Text(
                                        "[${e.level}]",
                                        color = levelColor,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                    Text(e.message)
                                    Text(
                                        formatUtcTimestamp(e.timestampUtc),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun parseUtc(ts: String): OffsetDateTime? =
    try { OffsetDateTime.parse(ts) } catch (_: Exception) { null }

private data class MetricLine(val label: String, val valueText: String)

private fun metricLine(metricName: String, value: Double): MetricLine? {
    return when (metricName.lowercase()) {
        "battery_pct" -> MetricLine("Battery", "${value.toInt()}%")
        "temp_c" -> MetricLine("Temp", "${"%.1f".format(value)}°C")
        "signal_dbm" -> MetricLine("Signal", "${value.toInt()} dBm")
        "cpu_pct" -> MetricLine("CPU", "${value.toInt()}%")
        else -> null
    }
}