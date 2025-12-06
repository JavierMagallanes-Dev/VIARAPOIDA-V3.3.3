package com.viarapida.app.ui.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viarapida.app.data.model.User
import com.viarapida.app.data.repository.AuthRepository
import com.viarapida.app.di.AppModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val authRepository: AuthRepository = AppModule.provideAuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    companion object {
        private const val TAG = "HomeViewModel"
    }

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            Log.d(TAG, "Cargando usuario actual")

            authRepository.getCurrentUser()
                .onSuccess { user ->
                    Log.d(TAG, "Usuario cargado: ${user?.name}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        user = user
                    )
                }
                .onFailure { error ->
                    Log.e(TAG, "Error cargando usuario: ${error.message}", error)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
        }
    }

    fun logout() {
        Log.d(TAG, "Cerrando sesión")
        authRepository.logout()
        _uiState.value = _uiState.value.copy(logoutSuccess = true)
    }
}

data class HomeUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null,
    val logoutSuccess: Boolean = false
)