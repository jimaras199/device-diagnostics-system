package com.jimaras199.devicediagnostics.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import com.jimaras199.devicediagnostics.di.AppContainer
import com.jimaras199.devicediagnostics.ui.screens.devices.DevicesRoute
import com.jimaras199.devicediagnostics.ui.screens.login.LoginRoute

@Composable
fun AppNavHost(container: AppContainer) {
    val navController = rememberNavController()
    val token by container.tokenState.collectAsState()

    if (token == null) {
        LoginRoute(container)
    } else {
        DevicesRoute(
            container = container,
            onDeviceClick = { id, _ ->
                navController.navigate("${Routes.DEVICE_DETAILS}/$id")
            }
        )
    }
}