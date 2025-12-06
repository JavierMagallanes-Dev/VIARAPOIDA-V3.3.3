package com.viarapida.app.ui.screens.payment

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viarapida.app.data.model.Transaction
import com.viarapida.app.data.remote.FirebaseClient
import com.viarapida.app.data.repository.PaymentRepository
import com.viarapida.app.di.AppModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TransactionHistoryViewModel(
    private val paymentRepository: PaymentRepository = AppModule.providePaymentRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionHistoryUiState())
    val uiState: StateFlow<TransactionHistoryUiState> = _uiState

    companion object {
        private const val TAG = "TransactionHistoryViewModel"
    }

    init {
        loadTransactions()
    }

    fun loadTransactions() {
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

            Log.d(TAG, "Cargando transacciones del usuario: $userId")

            paymentRepository.getUserTransactions(userId)
                .onSuccess { transactions ->
                    Log.d(TAG, "Transacciones cargadas: ${transactions.size}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        transactions = transactions
                    )
                }
                .onFailure { error ->
                    Log.e(TAG, "Error cargando transacciones: ${error.message}", error)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Error al cargar transacciones"
                    )
                }
        }
    }
}

data class TransactionHistoryUiState(
    val isLoading: Boolean = false,
    val transactions: List<Transaction> = emptyList(),
    val error: String = ""
)