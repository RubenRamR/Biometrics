package ramirez.ruben.biometrics.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(innerPadding: PaddingValues){
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var authStatus by remember { mutableStateOf("") }
    var biometricAvailable by remember { mutableStateOf("") }
    Column() {
        Text("Iniciar sesión")
        TextField(email, onValueChange = {email = it}, label = {Text("Correo electronico")})
        TextField(password, onValueChange = {password = it}, label = {Text("Contraseña")},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
        Button(onClick = {}) { Text("Iniciar Sesión")}

        Spacer(modifier = Modifier.height(32.dp))
        Text()
    }
}