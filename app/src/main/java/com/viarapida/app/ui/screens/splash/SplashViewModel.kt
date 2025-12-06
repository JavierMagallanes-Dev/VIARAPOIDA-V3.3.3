package com.viarapida.app.ui.screens.splash

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viarapida.app.data.repository.AuthRepository
import com.viarapida.app.data.repository.RouteRepository
import com.viarapida.app.di.AppModule
import com.viarapida.app.ui.utils.Constants
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SplashViewModel(
    private val authRepository: AuthRepository = AppModule.provideAuthRepository(),
    private val routeRepository: RouteRepository = AppModule.provideRouteRepository()
) : ViewModel() {

    private val _navigationState = MutableStateFlow<SplashNavigationState>(SplashNavigationState.Loading)
    val navigationState: StateFlow<SplashNavigationState> = _navigationState

    companion object {
        private const val TAG = "SplashViewModel"
        private const val SPLASH_DELAY = 2000L
    }

    fun checkAuthAndInitialize(context: Context) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Iniciando verificación de autenticación")

                // Verificar si es primera vez y crear rutas
                checkAndCreateInitialRoutes(context)

                // Delay mínimo para mostrar splash
                delay(SPLASH_DELAY)

                // Verificar si usuario está autenticado
                val isLoggedIn = authRepository.isUserLoggedIn()

                _navigationState.value = if (isLoggedIn) {
                    Log.d(TAG, "Usuario autenticado, navegando a Home")
                    SplashNavigationState.NavigateToHome
                } else {
                    Log.d(TAG, "Usuario no autenticado, navegando a Login")
                    SplashNavigationState.NavigateToLogin
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error en splash: ${e.message}", e)
                _navigationState.value = SplashNavigationState.NavigateToLogin
            }
        }
    }

    private suspend fun checkAndCreateInitialRoutes(context: Context) {
        try {
            val prefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
            val isFirstTime = prefs.getBoolean(Constants.KEY_FIRST_TIME, true)

            if (isFirstTime) {
                Log.d(TAG, "Primera vez abriendo la app, creando rutas iniciales")

                routeRepository.createInitialRoutes()
                    .onSuccess {
                        Log.d(TAG, "Rutas iniciales creadas exitosamente")
                        prefs.edit().putBoolean(Constants.KEY_FIRST_TIME, false).apply()
                    }
                    .onFailure { error ->
                        Log.e(TAG, "Error creando rutas iniciales: ${error.message}", error)
                    }
            } else {
                Log.d(TAG, "Rutas iniciales ya existen")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error verificando rutas iniciales: ${e.message}", e)
        }
    }
}

sealed class SplashNavigationState {
    object Loading : SplashNavigationState()
    object NavigateToLogin : SplashNavigationState()
    object NavigateToHome : SplashNavigationState()
}