package com.jimaras199.devicediagnostics.ui.screens.deviceDetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
fun DeviceDetailsScreen(state: DeviceDetailsUiState, onRetry: () -> Unit, onLogout: () -> Unit) {
    Scaffold(
        topBar = { DevicesTopBar(title = "Device Details", onLogout = onLogout) }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(12.dp)
        ) {
            when (state) {
                DeviceDetailsUiState.Loading -> Text("Loading…")
                is DeviceDetailsUiState.Error -> {
                    Text("Error: ${state.message}")
                    Button(onClick = onRetry) { Text("Retry") }
                }
                is DeviceDetailsUiState.Success -> {
                    val d = state.data.device
                    Text("Name: ${d.name}")
                    Text("Model: ${d.model ?: "-"}")
                    Spacer(Modifier.height(12.dp))

                    Text("Telemetry", style = MaterialTheme.typography.titleMedium)

                    if (state.data.telemetry.isEmpty()) {
                        Text(
                            "No telemetry yet",
                            style = MaterialTheme.typography.bodySmall
                        )
                    } else {
                        state.data.telemetry.take(10).forEach {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Column(Modifier.padding(12.dp)) {
                                    Text(it.metricName, style = MaterialTheme.typography.titleSmall)
                                    Text("Value: ${it.value}")
                                    Text(
                                        formatUtcTimestamp(it.timestampUtc),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    Text("Events", style = MaterialTheme.typography.titleMedium)

                    if (state.data.events.isEmpty()) {
                        Text(
                            "No events yet",
                            style = MaterialTheme.typography.bodySmall
                        )
                    } else {
                        state.data.events.take(10).forEach {
                            val levelColor = when (it.level.lowercase()) {
                                "error" -> Color.Red
                                "warning" -> Color(0xFFFFA500)
                                else -> MaterialTheme.colorScheme.primary
                            }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Column(Modifier.padding(12.dp)) {
                                    Text(
                                        "[${it.level}]",
                                        color = levelColor,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                    Text(it.message)
                                    Text(
                                        formatUtcTimestamp(it.timestampUtc),
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