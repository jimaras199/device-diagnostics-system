package com.jimaras199.devicediagnostics

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import com.jimaras199.devicediagnostics.di.AppContainer
import com.jimaras199.devicediagnostics.ui.navigation.AppNavHost
import com.jimaras199.devicediagnostics.ui.theme.DeviceDiagnosticsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            DeviceDiagnosticsTheme {
                val container = remember { AppContainer(applicationContext) }
                AppNavHost(container)
            }
        }
    }
}