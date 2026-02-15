package com.jimaras199.devicediagnostics.di

import android.content.Context
import com.jimaras199.devicediagnostics.auth.CachedTokenProvider
import com.jimaras199.devicediagnostics.auth.TokenProvider
import com.jimaras199.devicediagnostics.auth.TokenStore
import com.jimaras199.devicediagnostics.data.api.ApiClient
import com.jimaras199.devicediagnostics.data.repository.AuthRepository
import com.jimaras199.devicediagnostics.data.repository.AuthRepositoryImpl
import com.jimaras199.devicediagnostics.data.repository.DevicesRepository
import com.jimaras199.devicediagnostics.data.repository.DevicesRepositoryImpl
import com.jimaras199.devicediagnostics.data.repository.DashboardRepository
import com.jimaras199.devicediagnostics.data.repository.DashboardRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AppContainer(context: Context) {
    private val tokenStore = TokenStore(context)
    private val _tokenState = MutableStateFlow<String?>(null)
    private val appScope = CoroutineScope(
        kotlinx.coroutines.SupervisorJob() + Dispatchers.IO
    )
    private val unauthorizedHandler = com.jimaras199.devicediagnostics.auth.UnauthorizedHandler {
        appScope.launch { authRepository.logout() }
    }
    val tokenState: StateFlow<String?> = _tokenState
    val tokenProvider: TokenProvider = CachedTokenProvider(tokenState)
    val authApi = ApiClient.createAuthApi(tokenProvider,unauthorizedHandler)
    val dashboardApi = ApiClient.createDashboardApi(tokenProvider,unauthorizedHandler)
    val devicesApi = ApiClient.createDevicesApi(tokenProvider,unauthorizedHandler)
    val authRepository: AuthRepository = AuthRepositoryImpl(authApi, tokenStore)
    val dashboardRepository: DashboardRepository = DashboardRepositoryImpl(dashboardApi)
    val devicesRepository: DevicesRepository = DevicesRepositoryImpl(devicesApi)

    init {
        CoroutineScope(Dispatchers.IO).launch {
            tokenStore.tokenFlow.collect { t -> _tokenState.value = t }
        }
    }
}
