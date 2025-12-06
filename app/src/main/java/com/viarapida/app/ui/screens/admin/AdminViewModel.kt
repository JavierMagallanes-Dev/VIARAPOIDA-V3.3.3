package com.viarapida.app.ui.screens.admin

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viarapida.app.data.model.Ticket
import com.viarapida.app.data.repository.TicketRepository
import com.viarapida.app.di.AppModule
import com.viarapida.app.ui.utils.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AdminViewModel(
    private val ticketRepository: TicketRepository = AppModule.provideTicketRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState

    companion object {
        private const val TAG = "AdminViewModel"
    }

    init {
        loadAllTickets()
    }

    fun loadAllTickets() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = "")

            Log.d(TAG, "Cargando todos los tickets (Admin)")

            ticketRepository.getAllTickets()
                .onSuccess { tickets ->
                    Log.d(TAG, "Tickets cargados: ${tickets.size}")

                    val activeTickets = tickets.filter { it.status == Constants.STATUS_ACTIVE }
                    val usedTickets = tickets.filter { it.status == Constants.STATUS_USED }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        tickets = tickets,
                        totalTickets = tickets.size,
                        activeTickets = activeTickets.size,
                        usedTickets = usedTickets.size
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

    fun markTicketAsUsed(ticketId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUpdating = true, error = "")

            Log.d(TAG, "Marcando ticket como usado: $ticketId")

            ticketRepository.updateTicketStatus(ticketId, Constants.STATUS_USED)
                .onSuccess {
                    Log.d(TAG, "Ticket marcado como usado exitosamente")

                    // Recargar tickets
                    loadAllTickets()
                }
                .onFailure { error ->
                    Log.e(TAG, "Error marcando ticket: ${error.message}", error)
                    _uiState.value = _uiState.value.copy(
                        isUpdating = false,
                        error = error.message ?: "Error al actualizar el ticket"
                    )
                }
        }
    }
}

data class AdminUiState(
    val isLoading: Boolean = false,
    val isUpdating: Boolean = false,
    val tickets: List<Ticket> = emptyList(),
    val totalTickets: Int = 0,
    val activeTickets: Int = 0,
    val usedTickets: Int = 0,
    val error: String = ""
)