package com.jimaras199.devicediagnostics.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jimaras199.devicediagnostics.di.AppContainer
import com.jimaras199.devicediagnostics.ui.screens.deviceDetails.DeviceDetailsRoute
import com.jimaras199.devicediagnostics.ui.screens.devices.DevicesRoute
import com.jimaras199.devicediagnostics.ui.screens.login.LoginRoute

@Composable
fun AppNavHost(container: AppContainer) {
    val navController = rememberNavController()
    val token by container.tokenState.collectAsState()

    LaunchedEffect(token) {
        val target = if (token == null) Routes.LOGIN else Routes.DEVICES
        navController.navigate(target) {
            popUpTo(0) { inclusive = true }
            launchSingleTop = true
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (token == null) Routes.LOGIN else Routes.DEVICES
    ) {
        composable(Routes.LOGIN) {
            LoginRoute(container)
        }

        composable(Routes.DEVICES) {
            DevicesRoute(
                container = container,
                onDeviceClick = { id, _ ->
                    navController.navigate("${Routes.DEVICE_DETAILS}/$id")
                }
            )
        }

        composable(
            route = "${Routes.DEVICE_DETAILS}/{deviceId}",
            arguments = listOf(navArgument("deviceId") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("deviceId") ?: 0
            DeviceDetailsRoute(container = container, deviceId = id)
        }
    }
}
