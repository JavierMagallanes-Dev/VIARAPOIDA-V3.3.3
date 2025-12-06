package com.viarapida.app.ui.screens.ticket

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viarapida.app.data.model.Ticket
import com.viarapida.app.data.repository.TicketRepository
import com.viarapida.app.di.AppModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TicketDetailViewModel(
    private val ticketRepository: TicketRepository = AppModule.provideTicketRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(TicketDetailUiState())
    val uiState: StateFlow<TicketDetailUiState> = _uiState

    companion object {
        private const val TAG = "TicketDetailViewModel"
    }

    fun loadTicket(ticketId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = "")

            Log.d(TAG, "Cargando ticket: $ticketId")

            ticketRepository.getTicketById(ticketId)
                .onSuccess { ticket ->
                    if (ticket != null) {
                        Log.d(TAG, "Ticket cargado: ${ticket.passengerName}")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            ticket = ticket
                        )
                    } else {
                        Log.e(TAG, "Ticket no encontrado")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Ticket no encontrado"
                        )
                    }
                }
                .onFailure { error ->
                    Log.e(TAG, "Error cargando ticket: ${error.message}", error)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Error al cargar el ticket"
                    )
                }
        }
    }
}

data class TicketDetailUiState(
    val isLoading: Boolean = false,
    val ticket: Ticket? = null,
    val error: String = ""
)