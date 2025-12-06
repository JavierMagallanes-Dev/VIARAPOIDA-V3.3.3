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

class RegisterViewModel(
    private val authRepository: AuthRepository = AppModule.provideAuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState

    companion object {
        private const val TAG = "RegisterViewModel"
    }

    fun onNameChange(name: String) {
        _uiState.value = _uiState.value.copy(
            name = name,
            nameError = ""
        )
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

    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(
            confirmPassword = confirmPassword,
            confirmPasswordError = ""
        )
    }

    fun register() {
        val currentState = _uiState.value

        // Validar nombre
        val nameValidation = Validators.validateName(currentState.name)
        if (!nameValidation.isValid) {
            _uiState.value = currentState.copy(nameError = nameValidation.errorMessage)
            return
        }

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

        // Validar confirmación de password
        if (currentState.password != currentState.confirmPassword) {
            _uiState.value = currentState.copy(confirmPasswordError = "Las contraseñas no coinciden")
            return
        }

        // Iniciar registro
        viewModelScope.launch {
            _uiState.value = currentState.copy(isLoading = true, generalError = "")

            Log.d(TAG, "Intentando registro con email: ${currentState.email}")

            authRepository.register(
                name = currentState.name,
                email = currentState.email,
                password = currentState.password
            )
                .onSuccess { user ->
                    Log.d(TAG, "Registro exitoso para: ${user.name}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        registerSuccess = true,
                        user = user
                    )
                }
                .onFailure { error ->
                    Log.e(TAG, "Error en registro: ${error.message}", error)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        generalError = error.message ?: "Error al crear cuenta"
                    )
                }
        }
    }
}

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val nameError: String = "",
    val emailError: String = "",
    val passwordError: String = "",
    val confirmPasswordError: String = "",
    val generalError: String = "",
    val isLoading: Boolean = false,
    val registerSuccess: Boolean = false,
    val user: User? = null
)