package ramirez.ruben.biometrics.screens

import android.content.Context
import androidx.biometric.BiometricManager
import android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import java.util.concurrent.Executor
import java.util.concurrent.Executors

@Composable
fun LoginScreen(innerPadding: PaddingValues, context: Context) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var authStatus by remember { mutableStateOf("") }
    var biometricAvailable by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val biometricManager: BiometricManager = BiometricManager.from(context)
        when (biometricManager.canAuthenticate(BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                authStatus = "Biometricos disponibles. Presiona el botón para iniciar sesión"
                biometricAvailable = true

            }

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                authStatus = "El dispositivo no tiene sensor biométrico"
                biometricAvailable = false
            }

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                authStatus = "Sensor no disponible"
                biometricAvailable = false
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                authStatus =
                    "Datos biométricos no registrados. Regístralos en la configuración de tu sipositivo"
                biometricAvailable = false
            }
        }
    }

    val activity = context as FragmentActivity
    val executor: Executor = ContextCompat.getMainExecutor(context)

    val biometricPrompt: BiometricPrompt = remember {
        BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    authStatus = "Error: ${errString}"
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    authStatus = "Autenticación existosa."
                }

                override fun onAuthenticationFailed() {
                    authStatus = "Failed"
                }
            }
        )
    }

val promptInfo = remember {
    BiometricPrompt.PromptInfo.Builder()
        .setTitle("Autenticación biométrica")
        .setSubtitle("Usa tu huella/cara para iniciar")
        .setDescription("Coloca tu dedp en el sensor o mira tu camara")
        .setNegativeButtonText("Cancelar")
        .build()
}

Column() {
    Text("Iniciar sesión")
    TextField(email, onValueChange = { email = it }, label = { Text("Correo electronico") })
    TextField(
        password, onValueChange = { password = it }, label = { Text("Contraseña") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
    )
    Button(onClick = {biometricPrompt.authenticate(promptInfo)}, enabled = biometricAvailable) { Text("Iniciar Sesión") }

    Spacer(modifier = Modifier.height(32.dp))
    Text("Fin")
}
}