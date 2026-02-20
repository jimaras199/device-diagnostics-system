package com.jimaras199.devicediagnostics.ui.screens.settings

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jimaras199.devicediagnostics.di.AppContainer
import com.jimaras199.devicediagnostics.settings.ServerSettingsScreen

@Composable
fun ServerSettingsRoute(
    container: AppContainer,
    onSaved: () -> Unit
) {
    val vm: ServerSettingsViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ServerSettingsViewModel(container.serverSettingsStore) as T
            }
        }
    )

    ServerSettingsScreen(vm = vm, onSaved = onSaved)
}
