package com.jimaras199.devicediagnostics.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DevicesTopBar(
    title: String,
    onLogout: () -> Unit,
    showDemoButton: Boolean,
    demoButtonEnabled: Boolean,
    onLoadDemo: () -> Unit,
    showBack: Boolean = false,
    onBack: (() -> Unit)? = null
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showBack && onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = if (showBack && onBack != null) 4.dp else 8.dp)
            )

            if (showDemoButton) {
                TextButton(
                    onClick = onLoadDemo,
                    enabled = demoButtonEnabled
                ) {
                    Text("Demo data")
                }
            }

            TextButton(onClick = onLogout) {
                Text("Logout")
            }
        }
    }
}