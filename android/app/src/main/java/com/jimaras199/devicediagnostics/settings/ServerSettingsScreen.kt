package com.jimaras199.devicediagnostics.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jimaras199.devicediagnostics.ui.screens.settings.ServerSettingsViewModel

@Composable
fun ServerSettingsScreen(
    vm: ServerSettingsViewModel,
    onSaved: () -> Unit
) {
    val state by vm.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Server Settings", style = MaterialTheme.typography.titleLarge)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = state.scheme == "http",
                onClick = { vm.updateScheme("http") },
                label = { Text("http") }
            )
            FilterChip(
                selected = state.scheme == "https",
                onClick = { vm.updateScheme("https") },
                label = { Text("https") }
            )
        }

        OutlinedTextField(
            value = state.host,
            onValueChange = vm::updateHost,
            label = { Text("Host / IP") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.port,
            onValueChange = vm::updatePort,
            label = { Text("Port") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { vm.save(onSaved) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save")
        }
        state.error?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }
        Text(
            text = "Example: Emulator host is 10.0.2.2",
            style = MaterialTheme.typography.bodySmall
        )
    }
}
