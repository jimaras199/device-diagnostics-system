    package com.jimaras199.devicediagnostics.ui.screens.devices

    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.viewModelScope
    import com.jimaras199.devicediagnostics.data.model.toDeviceListItem
    import com.jimaras199.devicediagnostics.data.network.NetworkErrorMapper
    import com.jimaras199.devicediagnostics.data.repository.DashboardRepository
    import com.jimaras199.devicediagnostics.data.repository.DemoRepository
    import kotlinx.coroutines.Dispatchers
    import kotlinx.coroutines.flow.MutableStateFlow
    import kotlinx.coroutines.flow.StateFlow
    import kotlinx.coroutines.flow.asStateFlow
    import kotlinx.coroutines.launch
    import kotlinx.coroutines.withContext
    import com.jimaras199.devicediagnostics.ui.events.DevicesUiEvent
    import com.jimaras199.devicediagnostics.ui.models.DeviceListItem
    import kotlinx.coroutines.async
    import kotlinx.coroutines.coroutineScope
    import kotlinx.coroutines.flow.MutableSharedFlow
    import kotlinx.coroutines.flow.SharedFlow
    import kotlinx.coroutines.flow.asSharedFlow
    import retrofit2.HttpException

    class DevicesViewModel(
        private val repo: DashboardRepository,
        private val demoRepo: DemoRepository
    ) : ViewModel() {

        private val _events = MutableSharedFlow<DevicesUiEvent>(
            replay = 0,
            extraBufferCapacity = 1
        )
        val events: SharedFlow<DevicesUiEvent> = _events.asSharedFlow()

        private val _uiState = MutableStateFlow<DevicesUiState>(DevicesUiState.Loading)
        val uiState: StateFlow<DevicesUiState> = _uiState.asStateFlow()

        init {
            refresh()
        }

        fun refresh() {
            val current = _uiState.value

            _uiState.value = when (current) {
                is DevicesUiState.Success -> current.copy(isRefreshing = true)
                else -> DevicesUiState.Loading
            }

            viewModelScope.launch {
                try {
                    val (uiItems, demoSeeded) = withContext(Dispatchers.IO) {
                        coroutineScope {
                            val devicesDeferred = async {
                                repo.getDevicesDashboard(metricsPerDevice = 5)
                                    .map { it.toDeviceListItem() }
                            }
                            val demoStatusDeferred = async { demoRepo.getStatus() }

                            devicesDeferred.await() to demoStatusDeferred.await().seeded
                        }
                    }

                    _uiState.value = DevicesUiState.Success(
                        devices = uiItems,
                        isRefreshing = false,
                        demoSeeded = demoSeeded,
                        isSeedingDemo = false
                    )
                } catch (ex: Exception) {
                    val msg = NetworkErrorMapper.message(ex, NetworkErrorMapper.Context.GenericLoad)
                    val now = _uiState.value

                    if (now is DevicesUiState.Success) {
                        _uiState.value = now.copy(isRefreshing = false)
                        _events.tryEmit(
                            DevicesUiEvent.ShowSnackbar(
                                message = msg,
                                actionLabel = "Retry"
                            )
                        )
                    } else {
                        _uiState.value = DevicesUiState.Error(msg)
                    }
                }
            }
        }
        fun seedDemo() {
            val current = _uiState.value as? DevicesUiState.Success ?: return
            if (current.isSeedingDemo || current.isRefreshing || current.demoSeeded) return

            _uiState.value = current.copy(isSeedingDemo = true)

            viewModelScope.launch {
                try {
                    withContext(Dispatchers.IO) { demoRepo.seedDemo() }

                    _events.tryEmit(
                        DevicesUiEvent.ShowSnackbar("Demo data loaded")
                    )

                    val uiItems = loadDevices()
                    _uiState.value = DevicesUiState.Success(
                        devices = uiItems,
                        isRefreshing = false,
                        demoSeeded = true,
                        isSeedingDemo = false
                    )
                } catch (ex: Exception) {
                    if (ex is HttpException && ex.code() == 409) {
                        val uiItems = runCatching { loadDevices() }.getOrElse { emptyList() }

                        _uiState.value = DevicesUiState.Success(
                            devices = uiItems,
                            isRefreshing = false,
                            demoSeeded = true,
                            isSeedingDemo = false
                        )

                        _events.tryEmit(
                            DevicesUiEvent.ShowSnackbar("Demo already loaded")
                        )
                        return@launch
                    }

                    val msg = NetworkErrorMapper.message(ex, NetworkErrorMapper.Context.DemoSeed)
                    _uiState.value = current.copy(isSeedingDemo = false)

                    _events.tryEmit(
                        DevicesUiEvent.ShowSnackbar(msg)
                    )
                }
            }
        }
        private suspend fun loadDevices(): List<DeviceListItem> =
            withContext(Dispatchers.IO) {
                repo.getDevicesDashboard(metricsPerDevice = 5)
                    .map { it.toDeviceListItem() }
            }
    }
