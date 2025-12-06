package com.viarapida.app.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.viarapida.app.ui.components.CustomButton
import com.viarapida.app.ui.components.CustomTextField
import com.viarapida.app.ui.components.LoadingDialog

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: RegisterViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.registerSuccess) {
        if (uiState.registerSuccess) {
            onNavigateToHome()
        }
    }

    if (uiState.isLoading) {
        LoadingDialog(message = "Creando cuenta...")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo
        Text(
            text = "🚌",
            style = MaterialTheme.typography.displayLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Título
        Text(
            text = "ViaRapida",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Crear Cuenta",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Name Field
        CustomTextField(
            value = uiState.name,
            onValueChange = { viewModel.onNameChange(it) },
            label = "Nombre Completo",
            isError = uiState.nameError.isNotEmpty(),
            errorMessage = uiState.nameError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Email Field
        CustomTextField(
            value = uiState.email,
            onValueChange = { viewModel.onEmailChange(it) },
            label = "Correo Electrónico",
            isError = uiState.emailError.isNotEmpty(),
            errorMessage = uiState.emailError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password Field
        CustomTextField(
            value = uiState.password,
            onValueChange = { viewModel.onPasswordChange(it) },
            label = "Contraseña",
            isPassword = true,
            isError = uiState.passwordError.isNotEmpty(),
            errorMessage = uiState.passwordError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Confirm Password Field
        CustomTextField(
            value = uiState.confirmPassword,
            onValueChange = { viewModel.onConfirmPasswordChange(it) },
            label = "Confirmar Contraseña",
            isPassword = true,
            isError = uiState.confirmPasswordError.isNotEmpty(),
            errorMessage = uiState.confirmPasswordError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Error general
        if (uiState.generalError.isNotEmpty()) {
            Text(
                text = uiState.generalError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Register Button
        CustomButton(
            text = "Registrarse",
            onClick = { viewModel.register() },
            enabled = !uiState.isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Login Link
        TextButton(onClick = onNavigateToLogin) {
            Text(
                text = "¿Ya tienes cuenta? Inicia sesión",
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}