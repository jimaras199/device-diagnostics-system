package com.jimaras199.devicediagnostics.ui.screens.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jimaras199.devicediagnostics.ui.error.NetworkErrorMapper
import com.jimaras199.devicediagnostics.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class LoginViewModel(
    private val repo: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<String>(replay = 0, extraBufferCapacity = 1)
    val events: SharedFlow<String> = _events.asSharedFlow()

    fun showMessage(msg: String) {
        _events.tryEmit(msg)
    }

    fun submit(mode: AuthMode, email: String, password: String) {
        val e = email.trim()

        if (!Patterns.EMAIL_ADDRESS.matcher(e).matches()) {
            _uiState.value = LoginUiState.Error("Please enter a valid email.")
            return
        }
        when (mode) {
            AuthMode.Login -> {
                if (password.isBlank()) {
                    _uiState.value = LoginUiState.Error("Password must not be empty.")
                    return
                }
            }
            AuthMode.Register -> {
                if (password.length < 6) {
                    _uiState.value = LoginUiState.Error("Password must be at least 6 characters.")
                    return
                }
            }
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
                val ctx = if (mode == AuthMode.Login)
                    NetworkErrorMapper.Context.AuthLogin
                else
                    NetworkErrorMapper.Context.AuthRegister

                _uiState.value = LoginUiState.Error(NetworkErrorMapper.message(ex, ctx))
            }
        }
    }
}
