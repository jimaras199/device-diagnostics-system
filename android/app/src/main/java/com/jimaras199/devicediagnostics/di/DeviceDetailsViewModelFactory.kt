package com.jimaras199.devicediagnostics.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jimaras199.devicediagnostics.data.repository.DevicesRepository
import com.jimaras199.devicediagnostics.ui.screens.deviceDetails.DeviceDetailsViewModel

class DeviceDetailsViewModelFactory(
    private val repo: DevicesRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DeviceDetailsViewModel(repo) as T
    }
}
