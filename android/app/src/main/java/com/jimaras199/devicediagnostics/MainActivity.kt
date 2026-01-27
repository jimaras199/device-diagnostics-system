package com.jimaras199.devicediagnostics

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.jimaras199.devicediagnostics.ui.models.DeviceListItem
import com.jimaras199.devicediagnostics.ui.screens.DevicesScreen
import com.jimaras199.devicediagnostics.ui.theme.DeviceDiagnosticsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val devices = listOf(
            DeviceListItem(1, "Living Room Sensor", "ESP32", "2026-01-26T18:20:00Z"),
            DeviceListItem(2, "Garage Gateway", "RPI", "2026-01-26T18:18:00Z"),
            DeviceListItem(3, "Office Phone", "OnePlus", "2026-01-26T18:10:00Z")
        )

        setContent {
            DeviceDiagnosticsTheme {
                DevicesScreen(devices = devices)
            }
        }
    }
}