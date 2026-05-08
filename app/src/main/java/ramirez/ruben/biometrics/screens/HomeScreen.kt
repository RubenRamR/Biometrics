package ramirez.ruben.biometrics.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ramirez.ruben.biometrics.viewmodels.LoginViewModel

@Composable
fun HomeScreen(
    innerPadding: PaddingValues,
    viewModel: LoginViewModel,
    onLogout: () -> Unit
) {
    val username by viewModel.usernameFlow.collectAsState(initial = "")
    val isBiometricsEnabled by viewModel.isBiometricsEnabled.collectAsState(initial = false)

    HomeScreenContent(
        innerPadding = innerPadding,
        username = username,
        isBiometricsEnabled = isBiometricsEnabled,
        onBiometricsChanged = { enabled -> viewModel.setBiometricsEnabled(enabled) },
        onLogout = {
            viewModel.logout()
            onLogout()
        }
    )
}

@Composable
fun HomeScreenContent(
    innerPadding: PaddingValues,
    username: String,
    isBiometricsEnabled: Boolean,
    onBiometricsChanged: (Boolean) -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Hola $username",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Configuración de Biometría",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Autenticación biométrica",
                style = MaterialTheme.typography.bodyLarge
            )
            Switch(
                checked = isBiometricsEnabled,
                onCheckedChange = onBiometricsChanged
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cerrar sesión")
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreenContent(
            innerPadding = PaddingValues(0.dp),
            username = "Rubén",
            isBiometricsEnabled = true,
            onBiometricsChanged = {},
            onLogout = {}
        )
    }
}