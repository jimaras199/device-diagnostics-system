package com.jimaras199.devicediagnostics.ui.screens.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jimaras199.devicediagnostics.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class LoginViewModel(
    private val repo: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun submit(mode: AuthMode, email: String, password: String) {
        val e = email.trim()

        if (!Patterns.EMAIL_ADDRESS.matcher(e).matches()) {
            _uiState.value = LoginUiState.Error("Please enter a valid email.")
            return
        }
        if (password.length < 6) {
            _uiState.value = LoginUiState.Error("Password must be at least 6 characters.")
            return
        }

        _uiState.value = LoginUiState.Loading

        viewModelScope.launch {
            try {
                when (mode) {
                    AuthMode.Login -> repo.login(e, password)
                    AuthMode.Register -> repo.register(e, password)
                }
                _uiState.value = LoginUiState.Idle
            } catch (ex: Exception) {
                _uiState.value = LoginUiState.Error(mapAuthError(ex, mode))
            }
        }
    }

    private fun mapAuthError(ex: Exception, mode: AuthMode): String {
        return when (ex) {
            is HttpException -> when (ex.code()) {
                400 -> "Invalid input. Please check email/password."
                401 -> "Wrong email or password."
                409 -> if (mode == AuthMode.Register) "Email already registered." else "Conflict."
                else -> "Server error (${ex.code()}). Try again."
            }
            is IOException -> "Server unreachable. Check your connection."
            else -> ex.localizedMessage ?: "Unexpected error."
        }
    }
}
