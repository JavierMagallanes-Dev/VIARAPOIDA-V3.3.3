package com.viarapida.app.ui.screens.payment

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.viarapida.app.data.model.CardBrand
import com.viarapida.app.data.model.PaymentMethod
import com.viarapida.app.data.model.PaymentType
import com.viarapida.app.data.remote.FirebaseClient
import com.viarapida.app.data.repository.PaymentRepository
import com.viarapida.app.di.AppModule
import com.viarapida.app.ui.utils.PaymentValidators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddPaymentMethodViewModel(
    private val paymentRepository: PaymentRepository = AppModule.providePaymentRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddPaymentMethodUiState())
    val uiState: StateFlow<AddPaymentMethodUiState> = _uiState

    companion object {
        private const val TAG = "AddPaymentMethodViewModel"
    }

    fun onPaymentTypeSelected(type: PaymentType) {
        _uiState.value = _uiState.value.copy(
            selectedType = type,
            // Limpiar errores al cambiar tipo
            cardNumberError = "",
            cardHolderNameError = "",
            expiryDateError = "",
            cvvError = "",
            phoneNumberError = "",
            accountNameError = "",
            error = ""
        )
    }

    // === TARJETA ===

    fun onCardNumberChanged(cardNumber: String) {
        val formatted = PaymentValidators.formatCardNumber(cardNumber)
        if (formatted.replace(" ", "").length <= 19) {
            _uiState.value = _uiState.value.copy(
                cardNumber = formatted,
                cardNumberError = ""
            )
        }
    }

    fun onCardHolderNameChanged(name: String) {
        _uiState.value = _uiState.value.copy(
            cardHolderName = name.uppercase(),
            cardHolderNameError = ""
        )
    }

    fun onExpiryDateChanged(expiryDate: String) {
        val formatted = PaymentValidators.formatExpiryDate(expiryDate)
        if (formatted.replace("/", "").length <= 4) {
            _uiState.value = _uiState.value.copy(
                expiryDate = formatted,
                expiryDateError = ""
            )
        }
    }

    fun onCvvChanged(cvv: String) {
        if (cvv.length <= 4 && cvv.all { it.isDigit() }) {
            _uiState.value = _uiState.value.copy(
                cvv = cvv,
                cvvError = ""
            )
        }
    }

    // === YAPE/PLIN ===

    fun onPhoneNumberChanged(phone: String) {
        if (phone.length <= 9 && phone.all { it.isDigit() }) {
            _uiState.value = _uiState.value.copy(
                phoneNumber = phone,
                phoneNumberError = ""
            )
        }
    }

    fun onAccountNameChanged(name: String) {
        _uiState.value = _uiState.value.copy(
            accountName = name,
            accountNameError = ""
        )
    }

    fun onDefaultChanged(isDefault: Boolean) {
        _uiState.value = _uiState.value.copy(isDefault = isDefault)
    }

    fun savePaymentMethod() {
        val currentState = _uiState.value

        when (currentState.selectedType) {
            PaymentType.CARD -> validateAndSaveCard()
            PaymentType.YAPE, PaymentType.PLIN -> validateAndSaveDigitalWallet()
        }
    }

    private fun validateAndSaveCard() {
        val currentState = _uiState.value

        // Validar número de tarjeta
        val cardNumberValidation = PaymentValidators.validateCardNumber(currentState.cardNumber)
        if (!cardNumberValidation.isValid) {
            _uiState.value = currentState.copy(cardNumberError = cardNumberValidation.errorMessage)
            return
        }

        // Validar nombre
        val nameValidation = PaymentValidators.validateCardHolderName(currentState.cardHolderName)
        if (!nameValidation.isValid) {
            _uiState.value = currentState.copy(cardHolderNameError = nameValidation.errorMessage)
            return
        }

        // Validar fecha de expiración
        val expiryValidation = PaymentValidators.validateExpiryDate(currentState.expiryDate)
        if (!expiryValidation.isValid) {
            _uiState.value = currentState.copy(expiryDateError = expiryValidation.errorMessage)
            return
        }

        // Detectar marca de tarjeta
        val cardBrand = CardBrand.fromCardNumber(currentState.cardNumber)

        // Validar CVV
        val cvvValidation = PaymentValidators.validateCVV(currentState.cvv, cardBrand)
        if (!cvvValidation.isValid) {
            _uiState.value = currentState.copy(cvvError = cvvValidation.errorMessage)
            return
        }

        // Guardar tarjeta
        viewModelScope.launch {
            val userId = FirebaseClient.getCurrentUserId()
            if (userId == null) {
                _uiState.value = currentState.copy(error = "Usuario no autenticado")
                return@launch
            }

            _uiState.value = currentState.copy(isLoading = true, error = "")

            val paymentMethod = PaymentMethod(
                userId = userId,
                type = PaymentType.CARD,
                cardNumber = PaymentValidators.getLastFourDigits(currentState.cardNumber),
                cardHolderName = currentState.cardHolderName,
                cardBrand = cardBrand.displayName,
                expiryDate = currentState.expiryDate,
                isDefault = currentState.isDefault,
                createdAt = Timestamp.now()
            )

            Log.d(TAG, "Guardando tarjeta: ${paymentMethod.cardBrand} •••• ${paymentMethod.cardNumber}")

            paymentRepository.savePaymentMethod(paymentMethod)
                .onSuccess { id ->
                    Log.d(TAG, "Tarjeta guardada con ID: $id")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        saveSuccess = true
                    )
                }
                .onFailure { error ->
                    Log.e(TAG, "Error guardando tarjeta: ${error.message}", error)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Error al guardar tarjeta"
                    )
                }
        }
    }

    private fun validateAndSaveDigitalWallet() {
        val currentState = _uiState.value

        // Validar teléfono
        val phoneValidation = PaymentValidators.validatePhoneNumber(currentState.phoneNumber)
        if (!phoneValidation.isValid) {
            _uiState.value = currentState.copy(phoneNumberError = phoneValidation.errorMessage)
            return
        }

        // Validar nombre de cuenta
        val nameValidation = PaymentValidators.validateCardHolderName(currentState.accountName)
        if (!nameValidation.isValid) {
            _uiState.value = currentState.copy(accountNameError = nameValidation.errorMessage)
            return
        }

        // Guardar wallet digital
        viewModelScope.launch {
            val userId = FirebaseClient.getCurrentUserId()
            if (userId == null) {
                _uiState.value = currentState.copy(error = "Usuario no autenticado")
                return@launch
            }

            _uiState.value = currentState.copy(isLoading = true, error = "")

            val paymentMethod = PaymentMethod(
                userId = userId,
                type = currentState.selectedType,
                phoneNumber = currentState.phoneNumber,
                accountName = currentState.accountName,
                isDefault = currentState.isDefault,
                createdAt = Timestamp.now()
            )

            val walletName = if (currentState.selectedType == PaymentType.YAPE) "Yape" else "Plin"
            Log.d(TAG, "Guardando $walletName: ${paymentMethod.phoneNumber}")

            paymentRepository.savePaymentMethod(paymentMethod)
                .onSuccess { id ->
                    Log.d(TAG, "$walletName guardado con ID: $id")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        saveSuccess = true
                    )
                }
                .onFailure { error ->
                    Log.e(TAG, "Error guardando $walletName: ${error.message}", error)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Error al guardar $walletName"
                    )
                }
        }
    }
}

data class AddPaymentMethodUiState(
    val isLoading: Boolean = false,
    val saveSuccess: Boolean = false,
    val selectedType: PaymentType = PaymentType.CARD,

    // Tarjeta
    val cardNumber: String = "",
    val cardHolderName: String = "",
    val expiryDate: String = "",
    val cvv: String = "",
    val cardNumberError: String = "",
    val cardHolderNameError: String = "",
    val expiryDateError: String = "",
    val cvvError: String = "",

    // Yape/Plin
    val phoneNumber: String = "",
    val accountName: String = "",
    val phoneNumberError: String = "",
    val accountNameError: String = "",

    // Común
    val isDefault: Boolean = false,
    val error: String = ""
)