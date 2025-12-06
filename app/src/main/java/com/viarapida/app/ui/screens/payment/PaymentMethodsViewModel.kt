package com.viarapida.app.ui.screens.payment

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viarapida.app.data.model.PaymentMethod
import com.viarapida.app.data.remote.FirebaseClient
import com.viarapida.app.data.repository.PaymentRepository
import com.viarapida.app.di.AppModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PaymentMethodsViewModel(
    private val paymentRepository: PaymentRepository = AppModule.providePaymentRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PaymentMethodsUiState())
    val uiState: StateFlow<PaymentMethodsUiState> = _uiState

    companion object {
        private const val TAG = "PaymentMethodsViewModel"
    }

    init {
        loadPaymentMethods()
    }

    fun loadPaymentMethods() {
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

            Log.d(TAG, "Cargando métodos de pago del usuario: $userId")

            paymentRepository.getUserPaymentMethods(userId)
                .onSuccess { methods ->
                    Log.d(TAG, "Métodos de pago cargados: ${methods.size}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        paymentMethods = methods
                    )
                }
                .onFailure { error ->
                    Log.e(TAG, "Error cargando métodos de pago: ${error.message}", error)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Error al cargar métodos de pago"
                    )
                }
        }
    }

    fun setDefaultPaymentMethod(paymentMethodId: String) {
        viewModelScope.launch {
            val userId = FirebaseClient.getCurrentUserId() ?: return@launch

            _uiState.value = _uiState.value.copy(isLoading = true)

            Log.d(TAG, "Estableciendo método predeterminado: $paymentMethodId")

            paymentRepository.setDefaultPaymentMethod(userId, paymentMethodId)
                .onSuccess {
                    Log.d(TAG, "Método predeterminado establecido")
                    loadPaymentMethods()
                }
                .onFailure { error ->
                    Log.e(TAG, "Error estableciendo método predeterminado: ${error.message}", error)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Error al establecer método predeterminado"
                    )
                }
        }
    }

    fun deletePaymentMethod(paymentMethodId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            Log.d(TAG, "Eliminando método de pago: $paymentMethodId")

            paymentRepository.deletePaymentMethod(paymentMethodId)
                .onSuccess {
                    Log.d(TAG, "Método de pago eliminado")
                    loadPaymentMethods()
                }
                .onFailure { error ->
                    Log.e(TAG, "Error eliminando método de pago: ${error.message}", error)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Error al eliminar método de pago"
                    )
                }
        }
    }
}

data class PaymentMethodsUiState(
    val isLoading: Boolean = false,
    val paymentMethods: List<PaymentMethod> = emptyList(),
    val error: String = ""
)