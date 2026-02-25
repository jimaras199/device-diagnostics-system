package com.jimaras199.devicediagnostics.ui.screens.devices

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jimaras199.devicediagnostics.ui.components.DeviceRow
import com.jimaras199.devicediagnostics.ui.components.DevicesTopBar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState

@OptIn(androidx.compose.material.ExperimentalMaterialApi::class)
@Composable
fun DevicesScreen(
    state: DevicesUiState,
    onDeviceClick: (Int, String) -> Unit,
    onRefresh: () -> Unit,
    snackbarHostState: SnackbarHostState,
    onLogout: () -> Unit,
    onLoadDemo: () -> Unit
) {
    val refreshing = (state as? DevicesUiState.Success)?.isRefreshing == true
    val pullRefreshState = rememberPullRefreshState(
        refreshing = refreshing,
        onRefresh = onRefresh
    )

    Scaffold(
        topBar = {
            val success = state as? DevicesUiState.Success
            val showDemo = success?.demoSeeded != true
            val demoEnabled = success?.isSeedingDemo != true && success?.isRefreshing != true

            DevicesTopBar(
                title = "Devices",
                onLogout = onLogout,
                showDemoButton = showDemo,
                demoButtonEnabled = demoEnabled,
                onLoadDemo = onLoadDemo
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .pullRefresh(pullRefreshState)
        ) {
            when (state) {
                DevicesUiState.Loading -> {
                    Text(
                        text = "Loading…",
                        modifier = Modifier.padding(12.dp)
                    )
                }

                is DevicesUiState.Error -> {
                    com.jimaras199.devicediagnostics.ui.components.ErrorState(
                        message = state.message,
                        onRetry = onRefresh
                    )
                }

                is DevicesUiState.Success -> {
                    if (state.devices.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "No devices yet")
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
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

            PullRefreshIndicator(
                refreshing = refreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}
