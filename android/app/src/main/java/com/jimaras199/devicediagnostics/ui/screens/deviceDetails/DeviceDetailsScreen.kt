package com.jimaras199.devicediagnostics.ui.screens.deviceDetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jimaras199.devicediagnostics.ui.components.DevicesTopBar

@Composable
fun DeviceDetailsScreen(deviceId: Int, deviceName: String) {
    Scaffold(
        topBar = { DevicesTopBar(title = "Device Details") }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(12.dp)
        ) {
            Text(text = "ID: $deviceId")
            Text(text = "Name: $deviceName")
        }
    }
}
