package ramirez.ruben.biometrics.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ramirez.ruben.biometrics.datastore.DataStoreManager

class LoginViewModel(private val dataStoreManager: DataStoreManager) : ViewModel() {

    private val emailCredential = "correo@gmail.com"
    private val passwordCredential = "abc123"

    var isLoggedIn by mutableStateOf(false)
        private set

    var currentUsername by mutableStateOf("")
        private set

    val isBiometricsEnabled: StateFlow<Boolean> = dataStoreManager.biometricsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val isLoggedInFlow: StateFlow<Boolean> = dataStoreManager.isLoggedInFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val usernameFlow: StateFlow<String> = dataStoreManager.usernameFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    fun login(email: String, password: String, enableBiometrics: Boolean = false) {
        if (email.lowercase() == emailCredential && password == passwordCredential) {
            isLoggedIn = true
            currentUsername = email
            viewModelScope.launch {
                dataStoreManager.saveSession(email, enableBiometrics)
            }
        }
    }

    fun onBiometricSuccess() {
        viewModelScope.launch {
            dataStoreManager.loginWithBiometrics()
            isLoggedIn = true
            currentUsername = usernameFlow.value
        }
    }

    fun setBiometricsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            dataStoreManager.setBiometricsActive(enabled)
        }
    }

    fun logout() {
        viewModelScope.launch {
            dataStoreManager.logout()
            isLoggedIn = false
            currentUsername = ""
        }
    }

    suspend fun getStoredUsername(): String {
        return dataStoreManager.usernameFlow.stateIn(viewModelScope).value
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val dataStoreManager = DataStoreManager(application)
            return LoginViewModel(dataStoreManager) as T
        }
    }
}