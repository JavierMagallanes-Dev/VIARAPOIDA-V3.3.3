package com.viarapida.app.ui.screens.seats

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viarapida.app.data.model.Route
import com.viarapida.app.data.repository.RouteRepository
import com.viarapida.app.di.AppModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SeatSelectionViewModel(
    private val routeRepository: RouteRepository = AppModule.provideRouteRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(SeatSelectionUiState())
    val uiState: StateFlow<SeatSelectionUiState> = _uiState

    companion object {
        private const val TAG = "SeatSelectionViewModel"
    }

    fun loadRoute(routeId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = "")

            Log.d(TAG, "Cargando ruta: $routeId")

            routeRepository.getRouteById(routeId)
                .onSuccess { route ->
                    if (route != null) {
                        Log.d(TAG, "Ruta cargada: ${route.origin} -> ${route.destination}")
                        Log.d(TAG, "Asientos ocupados: ${route.occupiedSeats}")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            route = route
                        )
                    } else {
                        Log.e(TAG, "Ruta no encontrada")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Ruta no encontrada"
                        )
                    }
                }
                .onFailure { error ->
                    Log.e(TAG, "Error cargando ruta: ${error.message}", error)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Error al cargar la ruta"
                    )
                }
        }
    }

    fun selectSeat(seatNumber: Int) {
        val currentRoute = _uiState.value.route ?: return

        // Verificar que el asiento no esté ocupado
        if (seatNumber in currentRoute.occupiedSeats) {
            Log.w(TAG, "Intento de seleccionar asiento ocupado: $seatNumber")
            return
        }

        Log.d(TAG, "Asiento seleccionado: $seatNumber")
        _uiState.value = _uiState.value.copy(selectedSeat = seatNumber)
    }

    fun clearSelection() {
        Log.d(TAG, "Limpiando selección de asiento")
        _uiState.value = _uiState.value.copy(selectedSeat = null)
    }
}

data class SeatSelectionUiState(
    val isLoading: Boolean = false,
    val route: Route? = null,
    val selectedSeat: Int? = null,
    val error: String = ""
)