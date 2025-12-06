package com.viarapida.app.ui.screens.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viarapida.app.data.model.Route
import com.viarapida.app.data.repository.RouteRepository
import com.viarapida.app.di.AppModule
import com.viarapida.app.ui.utils.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val routeRepository: RouteRepository = AppModule.provideRouteRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState

    companion object {
        private const val TAG = "SearchViewModel"
    }

    fun onOriginChange(origin: String) {
        _uiState.value = _uiState.value.copy(
            origin = origin,
            originError = ""
        )
    }

    fun onDestinationChange(destination: String) {
        _uiState.value = _uiState.value.copy(
            destination = destination,
            destinationError = ""
        )
    }

    fun searchRoutes() {
        val currentState = _uiState.value

        // Validar origen y destino
        val validation = Validators.validateOriginDestination(
            currentState.origin,
            currentState.destination
        )

        if (!validation.isValid) {
            _uiState.value = currentState.copy(
                originError = if (currentState.origin.isBlank()) validation.errorMessage else "",
                destinationError = if (currentState.destination.isBlank() ||
                    currentState.origin == currentState.destination) validation.errorMessage else ""
            )
            return
        }

        // Buscar rutas
        viewModelScope.launch {
            _uiState.value = currentState.copy(isLoading = true, error = "")

            Log.d(TAG, "Buscando rutas: ${currentState.origin} -> ${currentState.destination}")

            routeRepository.searchRoutes(currentState.origin, currentState.destination)
                .onSuccess { routes ->
                    Log.d(TAG, "Rutas encontradas: ${routes.size}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        routes = routes,
                        hasSearched = true
                    )
                }
                .onFailure { error ->
                    Log.e(TAG, "Error buscando rutas: ${error.message}", error)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Error al buscar rutas"
                    )
                }
        }
    }
}

data class SearchUiState(
    val origin: String = "",
    val destination: String = "",
    val originError: String = "",
    val destinationError: String = "",
    val isLoading: Boolean = false,
    val routes: List<Route> = emptyList(),
    val hasSearched: Boolean = false,
    val error: String = ""
)