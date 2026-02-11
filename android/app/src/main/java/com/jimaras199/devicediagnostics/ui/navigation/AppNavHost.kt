package com.jimaras199.devicediagnostics.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jimaras199.devicediagnostics.ui.screens.deviceDetails.DeviceDetailsRoute
import com.jimaras199.devicediagnostics.ui.screens.devices.DevicesRoute
import com.jimaras199.devicediagnostics.ui.screens.deviceDetails.DeviceDetailsScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.DEVICES
    ) {
        composable(Routes.DEVICES) {
            DevicesRoute(
                onDeviceClick = { deviceId, deviceName ->
                    navController.navigate("${Routes.DEVICE_DETAILS}/$deviceId")
                }
            )
        }

        composable(
            route = "${Routes.DEVICE_DETAILS}/{deviceId}",
            arguments = listOf(
                navArgument("deviceId") { type = NavType.IntType },
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("deviceId") ?: 0
            DeviceDetailsRoute(deviceId = id)
        }
    }
}
