package com.jimaras199.devicediagnostics.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jimaras199.devicediagnostics.data.repository.AuthRepository
import com.jimaras199.devicediagnostics.ui.screens.login.LoginViewModel

class LoginViewModelFactory(
    private val repo: AuthRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoginViewModel(repo) as T
    }
}
