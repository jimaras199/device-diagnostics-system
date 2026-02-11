package com.jimaras199.devicediagnostics.ui.screens.devices

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jimaras199.devicediagnostics.ui.components.DeviceRow
import com.jimaras199.devicediagnostics.ui.components.DevicesTopBar

@Composable
fun DevicesScreen(
    state: DevicesUiState,
    onDeviceClick: (Int, String) -> Unit
) {
    Scaffold(
        topBar = { DevicesTopBar(title = "Devices") }
    ) { padding ->
        when (state) {
            DevicesUiState.Loading -> {
                Text(
                    text = "Loading…",
                    modifier = Modifier.padding(padding).padding(12.dp)
                )
            }
            is DevicesUiState.Error -> {
                Text(
                    text = "Error: ${state.message}",
                    modifier = Modifier.padding(padding).padding(12.dp)
                )
            }
            is DevicesUiState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.devices) { device ->
                        DeviceRow(
                            device = device,
                            onClick = { onDeviceClick(device.id, device.name) }
                        )
                    }
                }
            }
        }
    }
}
