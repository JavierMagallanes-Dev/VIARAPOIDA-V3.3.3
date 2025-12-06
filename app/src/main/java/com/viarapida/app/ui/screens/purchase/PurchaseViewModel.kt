package com.viarapida.app.ui.screens.purchase

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.viarapida.app.data.model.*
import com.viarapida.app.data.remote.FirebaseClient
import com.viarapida.app.data.repository.AuthRepository
import com.viarapida.app.data.repository.PaymentRepository
import com.viarapida.app.data.repository.RouteRepository
import com.viarapida.app.data.repository.TicketRepository
import com.viarapida.app.di.AppModule
import com.viarapida.app.ui.utils.Constants
import com.viarapida.app.ui.utils.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PurchaseViewModel(
    private val authRepository: AuthRepository = AppModule.provideAuthRepository(),
    private val routeRepository: RouteRepository = AppModule.provideRouteRepository(),
    private val ticketRepository: TicketRepository = AppModule.provideTicketRepository(),
    private val paymentRepository: PaymentRepository = AppModule.providePaymentRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PurchaseUiState())
    val uiState: StateFlow<PurchaseUiState> = _uiState

    companion object {
        private const val TAG = "PurchaseViewModel"
    }

    fun initialize(routeId: String, seatNumber: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            Log.d(TAG, "Inicializando compra - Ruta: $routeId, Asiento: $seatNumber")

            // Cargar usuario actual
            authRepository.getCurrentUser()
                .onSuccess { user ->
                    if (user != null) {
                        _uiState.value = _uiState.value.copy(currentUser = user)

                        // Cargar métodos de pago del usuario
                        loadPaymentMethods(user.id)

                        // Cargar ruta
                        loadRoute(routeId, seatNumber)
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Usuario no autenticado"
                        )
                    }
                }
                .onFailure { error ->
                    Log.e(TAG, "Error cargando usuario: ${error.message}", error)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Error al cargar usuario"
                    )
                }
        }
    }

    private suspend fun loadPaymentMethods(userId: String) {
        paymentRepository.getUserPaymentMethods(userId)
            .onSuccess { methods ->
                Log.d(TAG, "Métodos de pago cargados: ${methods.size}")

                // Seleccionar método por defecto si existe
                val defaultMethod = methods.firstOrNull { it.isDefault }

                _uiState.value = _uiState.value.copy(
                    paymentMethods = methods,
                    selectedPaymentMethod = defaultMethod
                )
            }
            .onFailure { error ->
                Log.e(TAG, "Error cargando métodos de pago: ${error.message}", error)
            }
    }

    private suspend fun loadRoute(routeId: String, seatNumber: Int) {
        routeRepository.getRouteById(routeId)
            .onSuccess { route ->
                if (route != null) {
                    // Verificar que el asiento siga disponible
                    if (seatNumber in route.occupiedSeats) {
                        Log.w(TAG, "Asiento ya ocupado: $seatNumber")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "El asiento seleccionado ya no está disponible"
                        )
                    } else {
                        Log.d(TAG, "Ruta cargada correctamente")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            route = route,
                            seatNumber = seatNumber
                        )
                    }
                } else {
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

    fun onPassengerNameChange(name: String) {
        _uiState.value = _uiState.value.copy(
            passengerName = name,
            passengerNameError = ""
        )
    }

    fun onPassengerDNIChange(dni: String) {
        _uiState.value = _uiState.value.copy(
            passengerDNI = dni,
            passengerDNIError = ""
        )
    }

    fun onPaymentMethodSelected(paymentMethod: PaymentMethod) {
        _uiState.value = _uiState.value.copy(selectedPaymentMethod = paymentMethod)
        Log.d(TAG, "Método de pago seleccionado: ${paymentMethod.getDisplayName()}")
    }

    fun confirmPurchase() {
        val currentState = _uiState.value

        // Validar nombre del pasajero
        val nameValidation = Validators.validateName(currentState.passengerName)
        if (!nameValidation.isValid) {
            _uiState.value = currentState.copy(passengerNameError = nameValidation.errorMessage)
            return
        }

        // Validar DNI
        val dniValidation = Validators.validateDNI(currentState.passengerDNI)
        if (!dniValidation.isValid) {
            _uiState.value = currentState.copy(passengerDNIError = dniValidation.errorMessage)
            return
        }

        // Validar método de pago
        if (currentState.selectedPaymentMethod == null) {
            _uiState.value = currentState.copy(error = "Selecciona un método de pago")
            return
        }

        val route = currentState.route
        val user = currentState.currentUser
        val seatNumber = currentState.seatNumber

        if (route == null || user == null || seatNumber == null) {
            _uiState.value = currentState.copy(error = "Datos incompletos para la compra")
            return
        }

        // Procesar compra
        viewModelScope.launch {
            _uiState.value = currentState.copy(isLoading = true, error = "")

            Log.d(TAG, "Procesando compra con pago para asiento: $seatNumber")

            try {
                // 1. Verificar nuevamente que el asiento esté disponible
                routeRepository.getRouteById(route.id)
                    .onSuccess { updatedRoute ->
                        if (updatedRoute != null && seatNumber in updatedRoute.occupiedSeats) {
                            Log.w(TAG, "Asiento ocupado durante la compra: $seatNumber")
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = "El asiento fue ocupado por otro usuario. Por favor selecciona otro."
                            )
                            return@onSuccess
                        }

                        // 2. Crear transacción (PENDING inicialmente)
                        val transaction = Transaction(
                            userId = user.id,
                            userName = user.name,
                            paymentMethodId = currentState.selectedPaymentMethod!!.id,
                            paymentType = currentState.selectedPaymentMethod!!.type,
                            amount = route.price,
                            status = TransactionStatus.PENDING,
                            transactionDate = Timestamp.now(),
                            description = "Compra de pasaje",
                            origin = route.origin,
                            destination = route.destination,
                            paymentMethodDisplay = currentState.selectedPaymentMethod!!.getDisplayName()
                        )

                        paymentRepository.createTransaction(transaction)
                            .onSuccess { transactionId ->
                                Log.d(TAG, "Transacción creada: $transactionId")

                                // 3. Procesar pago (simulado - aquí iría integración real)
                                processPayment(transactionId, route, user, seatNumber, currentState)
                            }
                            .onFailure { error ->
                                Log.e(TAG, "Error creando transacción: ${error.message}", error)
                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    error = "Error al crear transacción: ${error.message}"
                                )
                            }
                    }
                    .onFailure { error ->
                        Log.e(TAG, "Error verificando disponibilidad: ${error.message}", error)
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Error al verificar disponibilidad: ${error.message}"
                        )
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Error en compra: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al procesar la compra: ${e.message}"
                )
            }
        }
    }

    private suspend fun processPayment(
        transactionId: String,
        route: Route,
        user: User,
        seatNumber: Int,
        currentState: PurchaseUiState
    ) {
        // SIMULACIÓN DE PAGO - En producción aquí irían las integraciones reales
        // con Yape, Plin, o pasarelas de pago

        // Simulamos un delay de procesamiento
        kotlinx.coroutines.delay(2000)

        // Simulamos éxito (95% de las veces)
        val paymentSuccess = (0..100).random() > 5

        if (paymentSuccess) {
            // Pago exitoso
            Log.d(TAG, "Pago procesado exitosamente")

            // Actualizar estado de transacción a COMPLETED
            paymentRepository.updateTransactionStatus(
                transactionId,
                TransactionStatus.COMPLETED
            ).onSuccess {
                // Crear ticket
                val ticket = Ticket(
                    userId = user.id,
                    userName = user.name,
                    routeId = route.id,
                    passengerName = currentState.passengerName,
                    passengerDNI = currentState.passengerDNI,
                    seatNumber = seatNumber,
                    origin = route.origin,
                    destination = route.destination,
                    departureTime = route.departureTime,
                    price = route.price,
                    purchaseDate = Timestamp.now(),
                    status = Constants.STATUS_ACTIVE
                )

                ticketRepository.createTicket(ticket)
                    .onSuccess { ticketId ->
                        Log.d(TAG, "Ticket creado: $ticketId")

                        // Actualizar transacción con el ticketId
                        paymentRepository.getTransactionById(transactionId)
                            .onSuccess { trans ->
                                trans?.let {
                                    paymentRepository.createTransaction(
                                        it.copy(ticketId = ticketId)
                                    )
                                }
                            }

                        // Actualizar asientos ocupados
                        val updatedOccupiedSeats = route.occupiedSeats + seatNumber
                        routeRepository.updateOccupiedSeats(route.id, updatedOccupiedSeats)
                            .onSuccess {
                                Log.d(TAG, "Asientos actualizados correctamente")
                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    purchaseSuccess = true,
                                    ticketId = ticketId
                                )
                            }
                            .onFailure { error ->
                                Log.e(TAG, "Error actualizando asientos: ${error.message}", error)
                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    error = "Error al actualizar asientos: ${error.message}"
                                )
                            }
                    }
                    .onFailure { error ->
                        Log.e(TAG, "Error creando ticket: ${error.message}", error)

                        // Marcar transacción como fallida
                        paymentRepository.updateTransactionStatus(
                            transactionId,
                            TransactionStatus.FAILED,
                            "Error creando ticket: ${error.message}"
                        )

                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Error al crear el ticket: ${error.message}"
                        )
                    }
            }
        } else {
            // Pago fallido (simulado)
            Log.e(TAG, "Pago rechazado (simulado)")

            paymentRepository.updateTransactionStatus(
                transactionId,
                TransactionStatus.FAILED,
                "Pago rechazado por el procesador"
            )

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = "El pago fue rechazado. Por favor intenta con otro método de pago."
            )
        }
    }
}

data class PurchaseUiState(
    val isLoading: Boolean = false,
    val currentUser: User? = null,
    val route: Route? = null,
    val seatNumber: Int? = null,
    val passengerName: String = "",
    val passengerDNI: String = "",
    val passengerNameError: String = "",
    val passengerDNIError: String = "",

    // Pagos
    val paymentMethods: List<PaymentMethod> = emptyList(),
    val selectedPaymentMethod: PaymentMethod? = null,

    val error: String = "",
    val purchaseSuccess: Boolean = false,
    val ticketId: String? = null
)