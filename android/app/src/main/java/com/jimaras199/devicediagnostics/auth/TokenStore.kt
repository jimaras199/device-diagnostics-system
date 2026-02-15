package com.jimaras199.devicediagnostics.auth

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "auth")

class TokenStore(private val context: Context) {

    private val KEY_ACCESS_TOKEN = stringPreferencesKey("access_token")

    val tokenFlow: Flow<String?> =
        context.dataStore.data.map { prefs -> prefs[KEY_ACCESS_TOKEN] }

    suspend fun setToken(token: String?) {
        context.dataStore.edit { prefs: MutablePreferences ->
            if (token.isNullOrBlank()) {
                prefs.remove(KEY_ACCESS_TOKEN)
            } else {
                prefs[KEY_ACCESS_TOKEN] = token
            }
        }
    }
}
