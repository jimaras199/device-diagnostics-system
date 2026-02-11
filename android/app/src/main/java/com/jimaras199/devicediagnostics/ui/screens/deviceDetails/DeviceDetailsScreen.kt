package com.jimaras199.devicediagnostics.ui.screens.deviceDetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jimaras199.devicediagnostics.ui.components.DevicesTopBar

@Composable
fun DeviceDetailsScreen(state: DeviceDetailsUiState, onRetry: () -> Unit) {
    Scaffold(
        topBar = { DevicesTopBar(title = "Device Details") }
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
                    val d = state.device
                    Text("ID: ${d.id}")
                    Text("Name: ${d.name}")
                    Text("Model: ${d.model ?: "-"}")
                    Text("Last seen: ${d.lastSeen}")
                }
            }
        }
    }
}