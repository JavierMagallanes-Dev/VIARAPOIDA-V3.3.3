// app/src/main/java/com/viarapida/app/ui/screens/auth/LoginScreen.kt

package com.viarapida.app.ui.screens.auth

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.viarapida.app.R
import com.viarapida.app.ui.components.CustomButton
import com.viarapida.app.ui.components.CustomTextField
import com.viarapida.app.ui.components.LoadingDialog
import com.viarapida.app.ui.theme.GradientEnd
import com.viarapida.app.ui.theme.GradientStart

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // ⬇️ ESTE ES EL FIX IMPORTANTE
    LaunchedEffect(uiState.loginSuccess) {
        Log.d("LoginScreen", "loginSuccess cambió a: ${uiState.loginSuccess}")
        if (uiState.loginSuccess) {
            Log.d("LoginScreen", "Navegando al Home...")
            onNavigateToHome()
        }
    }

    // ⬇️ CONFIGURAR GOOGLE SIGN-IN
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d("LoginScreen", "Result code: ${result.resultCode}")
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                Log.d("LoginScreen", "Cuenta de Google obtenida: ${account.email}")
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                viewModel.loginWithGoogle(credential)
            } catch (e: ApiException) {
                Log.e("LoginScreen", "Error al obtener cuenta de Google: ${e.message}", e)
            }
        } else {
            Log.w("LoginScreen", "Login con Google cancelado")
        }
    }

    if (uiState.isLoading) {
        LoadingDialog(message = "Iniciando sesión...")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header con gradiente
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(GradientStart, GradientEnd)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsBus,
                        contentDescription = "Logo",
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "ViaRapida",
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Bienvenido de nuevo",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                    )
                }
            }

            // Formulario
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                shape = MaterialTheme.shapes.extraLarge,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Iniciar Sesión",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    CustomTextField(
                        value = uiState.email,
                        onValueChange = { viewModel.onEmailChange(it) },
                        label = "Correo Electrónico",
                        placeholder = "ejemplo@correo.com",
                        leadingIcon = Icons.Default.Email,
                        isError = uiState.emailError.isNotEmpty(),
                        errorMessage = uiState.emailError,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    CustomTextField(
                        value = uiState.password,
                        onValueChange = { viewModel.onPasswordChange(it) },
                        label = "Contraseña",
                        placeholder = "••••••••",
                        leadingIcon = Icons.Default.Lock,
                        isPassword = true,
                        isError = uiState.passwordError.isNotEmpty(),
                        errorMessage = uiState.passwordError,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )

                    AnimatedVisibility(
                        visible = uiState.generalError.isNotEmpty(),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Text(
                                text = uiState.generalError,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    CustomButton(
                        text = "Iniciar Sesión",
                        onClick = { viewModel.login() },
                        enabled = !uiState.isLoading,
                        loading = uiState.isLoading
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Divisor
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f))
                        Text(
                            text = "  O  ",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botón de Google
                    OutlinedButton(
                        onClick = {
                            Log.d("LoginScreen", "Botón Google presionado")
                            try {
                                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                    .requestIdToken(context.getString(R.string.default_web_client_id))
                                    .requestEmail()
                                    .build()

                                val googleSignInClient = GoogleSignIn.getClient(context, gso)

                                // Cerrar sesión anterior para forzar selector de cuenta
                                googleSignInClient.signOut()

                                launcher.launch(googleSignInClient.signInIntent)
                            } catch (e: Exception) {
                                Log.e("LoginScreen", "Error al iniciar Google Sign-In: ${e.message}", e)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = !uiState.isLoading,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            width = 1.dp
                        )
                    ) {
                        Text(
                            text = "G",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Continuar con Google",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(
                        onClick = onNavigateToRegister,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "¿No tienes cuenta? Regístrate",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}