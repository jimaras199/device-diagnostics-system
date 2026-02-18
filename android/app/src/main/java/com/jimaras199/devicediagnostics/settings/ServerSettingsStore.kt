package com.jimaras199.devicediagnostics.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.serverSettingsDataStore by preferencesDataStore(name = "server_settings")

data class ServerSettings(
    val scheme: String,
    val host: String,
    val port: String
) {
    fun baseUrl(): String {
        val cleanHost = host.trim().removeSuffix("/")
        val cleanPort = port.trim()
        return "$scheme://$cleanHost:$cleanPort/"
    }
}

class ServerSettingsStore(private val context: Context) {

    private val KEY_SCHEME = stringPreferencesKey("scheme")
    private val KEY_HOST = stringPreferencesKey("host")
    private val KEY_PORT = stringPreferencesKey("port")

    val settingsFlow: Flow<ServerSettings> =
        context.serverSettingsDataStore.data.map { prefs ->
            ServerSettings(
                scheme = prefs[KEY_SCHEME] ?: "http",
                host = prefs[KEY_HOST] ?: "192.168.68.55",
                port = prefs[KEY_PORT] ?: "5275"
            )
        }

    suspend fun save(settings: ServerSettings) {
        context.serverSettingsDataStore.edit { prefs ->
            prefs[KEY_SCHEME] = settings.scheme
            prefs[KEY_HOST] = settings.host
            prefs[KEY_PORT] = settings.port
        }
    }
}
