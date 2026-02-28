package com.jimaras199.devicediagnostics.ui.screens.deviceDetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import com.jimaras199.devicediagnostics.ui.formatters.MetricsUiFormatter

@Composable
fun DeviceDetailsScreen(
    state: DeviceDetailsUiState,
    onRetry: () -> Unit,
    onLogout: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            DevicesTopBar(
                title = "Device Details",
                onLogout = onLogout,
                showDemoButton = false,
                demoButtonEnabled = false,
                onLoadDemo = {},
                showBack = true,
                onBack = onBack
            )
        }
    ) { padding ->
        when (state) {
            DeviceDetailsUiState.Loading -> {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Loading…",
                    modifier = Modifier.padding(padding).padding(horizontal = 12.dp)
                )
            }

            is DeviceDetailsUiState.Error -> {
                com.jimaras199.devicediagnostics.ui.components.ErrorState(
                    message = state.message,
                    onRetry = onRetry
                )
            }

            is DeviceDetailsUiState.Success -> {
                val data = state.data
                val d = data.device
                val latestPerMetric = data.telemetry
                    .groupBy { it.metricName }
                    .mapNotNull { (name, items) ->
                        val latest = items.maxByOrNull { it.timestampUtc } ?: return@mapNotNull null
                        MetricsUiFormatter.metricUi(name, latest.value)
                    }
                    .sortedBy { it.label.lowercase() }

                LazyColumn(
                    modifier = Modifier.padding(padding).padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item { Spacer(Modifier.height(4.dp)) }
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
                                    add("Last seen: ${formatUtcTimestamp(d.lastSeenUtc)}")
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
                                    val ui = MetricsUiFormatter.metricUi(t.metricName, t.value)
                                    val label = ui?.label ?: t.metricName
                                    val valueText = ui?.valueText ?: t.value.toString()

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
                            val (bgColor, contentColor) = when (e.level.lowercase()) {
                                "error" -> Color(0xFFFFEBEE) to Color(0xFFB00020)
                                "warning" -> Color(0xFFFFF8E1) to Color(0xFFEF6C00)
                                else -> MaterialTheme.colorScheme.primaryContainer to
                                        MaterialTheme.colorScheme.onPrimaryContainer
                            }
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Column(Modifier.padding(12.dp)) {

                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = e.message,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                Spacer(Modifier.height(6.dp))

                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = e.level.uppercase(),
                                        color = contentColor,
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier
                                            .background(bgColor, RoundedCornerShape(50))
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    )

                                    Text(
                                        text = formatUtcTimestamp(e.timestampUtc),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
}
