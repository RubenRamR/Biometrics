package ramirez.ruben.biometrics.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class DataStoreManager(private val context: Context) {

    private val Context.dataStore by preferencesDataStore("session_preferences")

    companion object {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val USERNAME = stringPreferencesKey("username")
        val BIOMETRICS_ACTIVE = booleanPreferencesKey("biometrics_active")
    }

    val isLoggedInFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_LOGGED_IN] ?: false
    }

    val usernameFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[USERNAME] ?: ""
    }

    val biometricsFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[BIOMETRICS_ACTIVE] ?: false
    }

    suspend fun saveSession(username: String, biometricsEnabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[USERNAME] = username
            preferences[IS_LOGGED_IN] = true
            preferences[BIOMETRICS_ACTIVE] = biometricsEnabled
        }
    }

    suspend fun setBiometricsActive(active: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[BIOMETRICS_ACTIVE] = active
        }
    }

    suspend fun logout() {
        val biometricsEnabled = biometricsFlow.first()

        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = false

            if (!biometricsEnabled) {
                preferences[USERNAME] = ""
                preferences[BIOMETRICS_ACTIVE] = false
            }
        }
    }

    suspend fun loginWithBiometrics() {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = true
        }
    }

    suspend fun hasStoredCredentials(): Boolean {
        val username = usernameFlow.first()
        return username.isNotEmpty()
    }
}