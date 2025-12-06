package com.viarapida.app.ui.screens.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viarapida.app.data.model.User
import com.viarapida.app.data.repository.AuthRepository
import com.viarapida.app.di.AppModule
import com.viarapida.app.ui.utils.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository = AppModule.provideAuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    companion object {
        private const val TAG = "LoginViewModel"
    }

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email,
            emailError = ""
        )
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            passwordError = ""
        )
    }

    fun login() {
        val currentState = _uiState.value

        // Validar email
        val emailValidation = Validators.validateEmail(currentState.email)
        if (!emailValidation.isValid) {
            _uiState.value = currentState.copy(emailError = emailValidation.errorMessage)
            return
        }

        // Validar password
        val passwordValidation = Validators.validatePassword(currentState.password)
        if (!passwordValidation.isValid) {
            _uiState.value = currentState.copy(passwordError = passwordValidation.errorMessage)
            return
        }

        // Iniciar login
        viewModelScope.launch {
            _uiState.value = currentState.copy(isLoading = true, generalError = "")

            Log.d(TAG, "Intentando login con email: ${currentState.email}")

            authRepository.login(currentState.email, currentState.password)
                .onSuccess { user ->
                    Log.d(TAG, "Login exitoso para: ${user.name}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        loginSuccess = true,
                        user = user
                    )
                }
                .onFailure { error ->
                    Log.e(TAG, "Error en login: ${error.message}", error)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        generalError = error.message ?: "Error al iniciar sesión"
                    )
                }
        }
    }
}

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String = "",
    val passwordError: String = "",
    val generalError: String = "",
    val isLoading: Boolean = false,
    val loginSuccess: Boolean = false,
    val user: User? = null
)