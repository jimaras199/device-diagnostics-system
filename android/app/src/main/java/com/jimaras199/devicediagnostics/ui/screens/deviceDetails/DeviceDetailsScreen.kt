package com.jimaras199.devicediagnostics.ui.screens.deviceDetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jimaras199.devicediagnostics.ui.components.DevicesTopBar

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
                    state.data.telemetry.take(10).forEach {
                        Text("${it.metricName}: ${it.value} @ ${it.timestampUtc}")
                    }

                    Spacer(Modifier.height(12.dp))

                    Text("Events", style = MaterialTheme.typography.titleMedium)
                    state.data.events.take(10).forEach {
                        Text("[${it.level}] ${it.message} @ ${it.timestampUtc}")
                    }
                }
            }
        }
    }
}