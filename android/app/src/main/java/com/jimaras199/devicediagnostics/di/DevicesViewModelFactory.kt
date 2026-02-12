package com.jimaras199.devicediagnostics.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jimaras199.devicediagnostics.data.repository.DashboardRepository
import com.jimaras199.devicediagnostics.ui.screens.devices.DevicesViewModel

class DevicesViewModelFactory(
    private val repo: DashboardRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DevicesViewModel(repo) as T
    }
}
