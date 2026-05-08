package ramirez.ruben.biometrics.screens

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import ramirez.ruben.biometrics.viewmodels.LoginViewModel
import java.util.concurrent.Executor

@Composable
fun LoginScreen(
    innerPadding: PaddingValues,
    activity: FragmentActivity,
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit = {}
) {
    val isBiometricsEnabled by viewModel.isBiometricsEnabled.collectAsState(initial = false)
    val storedUsername by viewModel.usernameFlow.collectAsState(initial = "")

    var authStatus by remember { mutableStateOf("") }
    var biometricAvailable by remember { mutableStateOf(false) }

    val hasStoredCredentials = storedUsername.isNotEmpty() && isBiometricsEnabled

    LaunchedEffect(activity, storedUsername, isBiometricsEnabled) {
        val biometricManager = BiometricManager.from(activity)
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                if (hasStoredCredentials) {
                    authStatus = "Sesión previa detectada. Usa tu huella para iniciar sesión."
                } else {
                    authStatus = "Biométricos disponibles. Ingresa tus credenciales."
                }
                biometricAvailable = true
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                authStatus = "El dispositivo no tiene sensor biométrico."
                biometricAvailable = false
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                authStatus = "Sensor no disponible temporalmente."
                biometricAvailable = false
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                authStatus = "No hay huellas registradas en el dispositivo."
                biometricAvailable = false
            }
            else -> {
                authStatus = "Error biométrico desconocido."
                biometricAvailable = false
            }
        }
    }

    val executor: Executor = ContextCompat.getMainExecutor(activity)

    val biometricPrompt = remember {
        BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    authStatus = "Error: $errString"
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    authStatus = "Autenticación exitosa."
                    viewModel.onBiometricSuccess()
                    onLoginSuccess()
                }

                override fun onAuthenticationFailed() {
                    authStatus = "Autenticación fallida. Intenta de nuevo."
                }
            }
        )
    }

    val promptInfo = remember {
        BiometricPrompt.PromptInfo.Builder()
            .setTitle("Autenticación biométrica")
            .setSubtitle("Usa tu huella o rostro para iniciar")
            .setDescription("Verifica tu identidad para continuar")
            .setNegativeButtonText("Cancelar")
            .build()
    }

    LoginScreenContent(
        innerPadding = innerPadding,
        hasStoredCredentials = hasStoredCredentials,
        storedUsername = storedUsername,
        authStatus = authStatus,
        biometricAvailable = biometricAvailable,
        onLoginClick = { email, password ->
            if (hasStoredCredentials) {
                biometricPrompt.authenticate(promptInfo)
            } else {
                viewModel.login(email, password, isBiometricsEnabled)
                if (viewModel.isLoggedIn) {
                    onLoginSuccess()
                } else {
                    authStatus = "Credenciales incorrectas."
                }
            }
        }
    )
}

@Composable
fun LoginScreenContent(
    innerPadding: PaddingValues,
    hasStoredCredentials: Boolean,
    storedUsername: String,
    authStatus: String,
    biometricAvailable: Boolean,
    onLoginClick: (String, String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 32.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Iniciar sesión",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (hasStoredCredentials) {
            Text(
                text = "Bienvenido de nuevo,\n$storedUsername",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(bottom = 16.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            singleLine = true,
            enabled = !hasStoredCredentials,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            singleLine = true,
            enabled = !hasStoredCredentials,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { onLoginClick(email, password) },
            enabled = biometricAvailable || !hasStoredCredentials,
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text(if (hasStoredCredentials) "Iniciar con huella" else "Iniciar Sesión")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = authStatus,
            color = if (authStatus.contains("Error") || authStatus.contains("incorrectas") || authStatus.contains("fallida"))
                MaterialTheme.colorScheme.error
            else
                MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    MaterialTheme {
        LoginScreenContent(
            innerPadding = PaddingValues(0.dp),
            hasStoredCredentials = false,
            storedUsername = "Rubén",
            authStatus = "Biométricos disponibles. Ingresa tus credenciales.",
            biometricAvailable = true,
            onLoginClick = { _, _ -> }
        )
    }
}