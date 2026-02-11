package com.jimaras199.devicediagnostics

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jimaras199.devicediagnostics.ui.navigation.AppNavHost
import com.jimaras199.devicediagnostics.ui.screens.devices.DevicesScreen
import com.jimaras199.devicediagnostics.ui.screens.devices.DevicesViewModel
import com.jimaras199.devicediagnostics.ui.theme.DeviceDiagnosticsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            DeviceDiagnosticsTheme {
                AppNavHost()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview(){
    AppNavHost()
}