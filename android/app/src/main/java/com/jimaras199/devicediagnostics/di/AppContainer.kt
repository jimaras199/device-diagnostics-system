package com.jimaras199.devicediagnostics.di

import android.content.Context
import com.jimaras199.devicediagnostics.auth.AuthEvent
import com.jimaras199.devicediagnostics.auth.CachedTokenProvider
import com.jimaras199.devicediagnostics.auth.TokenProvider
import com.jimaras199.devicediagnostics.auth.TokenStore
import com.jimaras199.devicediagnostics.auth.UnauthorizedHandler
import com.jimaras199.devicediagnostics.data.api.ApiClient
import com.jimaras199.devicediagnostics.data.repository.*
import com.jimaras199.devicediagnostics.settings.ServerSettingsStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class AppContainer(context: Context) {

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val tokenStore = TokenStore(context)
    private val _tokenState = MutableStateFlow<String?>(null)
    val tokenState: StateFlow<String?> = _tokenState.asStateFlow()
    val serverSettingsStore = ServerSettingsStore(context)
    private val _baseUrl = MutableStateFlow("http://10.0.2.2:5275/")
    val baseUrl: StateFlow<String> = _baseUrl.asStateFlow()
    val tokenProvider: TokenProvider = CachedTokenProvider(tokenState)
    private val _authEvents = MutableSharedFlow<AuthEvent>(
        replay = 0,
        extraBufferCapacity = 1
    )
    val authEvents: SharedFlow<AuthEvent> = _authEvents.asSharedFlow()
    private val unauthorizedHandler = UnauthorizedHandler {
        appScope.launch {
            tokenStore.setToken(null)
            _authEvents.tryEmit(AuthEvent.SessionExpired)
        }
    }
    private val retrofit: Retrofit by lazy {
        val okHttp = ApiClient.buildOkHttp(
            tokenProvider = tokenProvider,
            unauthorizedHandler = unauthorizedHandler,
            baseUrlProvider = { _baseUrl.value }
        )
        ApiClient.buildRetrofit("http://localhost/", okHttp)
    }
    private val authApi = ApiClient.createAuthApi(retrofit)
    private val dashboardApi = ApiClient.createDashboardApi(retrofit)
    private val devicesApi = ApiClient.createDevicesApi(retrofit)
    private val telemetryApi = ApiClient.createTelemetryApi(retrofit)
    private val eventsApi = ApiClient.createEventsApi(retrofit)
    private val demoApi = ApiClient.createDemoApi(retrofit)
    val demoRepository: DemoRepository = DemoRepositoryImpl(demoApi)
    val authRepository: AuthRepository = AuthRepositoryImpl(authApi, tokenStore)
    val dashboardRepository: DashboardRepository = DashboardRepositoryImpl(dashboardApi)
    val devicesRepository: DevicesRepository = DevicesRepositoryImpl(devicesApi)
    val telemetryRepository: TelemetryRepository = TelemetryRepositoryImpl(telemetryApi)
    val eventsRepository: EventsRepository = EventsRepositoryImpl(eventsApi)

    init {
        appScope.launch {
            tokenStore.tokenFlow.collect { t ->
                _tokenState.value = t
            }
        }
        appScope.launch {
            serverSettingsStore.settingsFlow.collect { s ->
                _baseUrl.value = s.baseUrl()
            }
        }
    }
}
