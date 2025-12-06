package com.viarapida.app.ui.screens.mytickets

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viarapida.app.data.model.Ticket
import com.viarapida.app.data.remote.FirebaseClient
import com.viarapida.app.data.repository.TicketRepository
import com.viarapida.app.di.AppModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MyTicketsViewModel(
    private val ticketRepository: TicketRepository = AppModule.provideTicketRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyTicketsUiState())
    val uiState: StateFlow<MyTicketsUiState> = _uiState

    companion object {
        private const val TAG = "MyTicketsViewModel"
    }

    init {
        loadUserTickets()
    }

    fun loadUserTickets() {
        viewModelScope.launch {
            val userId = FirebaseClient.getCurrentUserId()

            if (userId == null) {
                Log.e(TAG, "Usuario no autenticado")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Usuario no autenticado"
                )
                return@launch
            }

            _uiState.value = _uiState.value.copy(isLoading = true, error = "")

            Log.d(TAG, "Cargando tickets del usuario: $userId")

            ticketRepository.getUserTickets(userId)
                .onSuccess { tickets ->
                    Log.d(TAG, "Tickets del usuario cargados: ${tickets.size}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        tickets = tickets
                    )
                }
                .onFailure { error ->
                    Log.e(TAG, "Error cargando tickets: ${error.message}", error)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Error al cargar los tickets"
                    )
                }
        }
    }
}

data class MyTicketsUiState(
    val isLoading: Boolean = false,
    val tickets: List<Ticket> = emptyList(),
    val error: String = ""
)