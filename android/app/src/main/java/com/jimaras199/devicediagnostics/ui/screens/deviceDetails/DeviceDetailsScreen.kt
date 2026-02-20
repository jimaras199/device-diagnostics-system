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

                LazyColumn(
                    modifier = Modifier.padding(padding).padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text("Name: ${d.name}")
                        Text("Model: ${d.model ?: "-"}")
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
                                androidx.compose.foundation.layout.Column(
                                    Modifier.padding(12.dp)
                                ) {
                                    Text(t.metricName, style = MaterialTheme.typography.titleSmall)
                                    Text("Value: ${t.value}")
                                    Text(
                                        formatUtcTimestamp(t.timestampUtc),
                                        style = MaterialTheme.typography.bodySmall
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
                                androidx.compose.foundation.layout.Column(
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
