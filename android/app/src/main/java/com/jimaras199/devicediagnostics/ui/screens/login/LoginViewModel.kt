package com.jimaras199.devicediagnostics.ui.screens.login

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

    fun login(email: String, password: String) {

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
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
                repo.login(email, password)
                _uiState.value = LoginUiState.Idle
            } catch (ex: Exception) {
                _uiState.value = LoginUiState.Error(mapLoginError(ex))
            }
        }
    }
}

private fun mapLoginError(ex: Exception): String {
    return when (ex) {
        is HttpException -> when (ex.code()) {
            400 -> "Invalid input. Please check email format."
            401 -> "Wrong email or password."
            else -> "Server error (${ex.code()}). Try again."
        }
        is IOException -> "Server unreachable. Check your connection."
        else -> ex.localizedMessage ?: "Unexpected error."
    }
}