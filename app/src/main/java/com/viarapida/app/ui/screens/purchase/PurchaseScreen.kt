package com.viarapida.app.ui.screens.purchase

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.viarapida.app.data.model.PaymentMethod
import com.viarapida.app.data.model.PaymentType
import com.viarapida.app.ui.components.CustomButton
import com.viarapida.app.ui.components.CustomTextField
import com.viarapida.app.ui.components.LoadingDialog
import androidx.compose.foundation.background
import androidx.compose.ui.text.font.FontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchaseScreen(
    routeId: String,
    seatNumber: Int,
    onNavigateBack: () -> Unit,
    onNavigateToTicketDetail: (String) -> Unit,
    onNavigateToAddPayment: () -> Unit,
    viewModel: PurchaseViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showPaymentMethodDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.initialize(routeId, seatNumber)
    }

    LaunchedEffect(uiState.purchaseSuccess) {
        if (uiState.purchaseSuccess && uiState.ticketId != null) {
            onNavigateToTicketDetail(uiState.ticketId!!)
        }
    }

    // Loading dialog mejorado con estado de pago
    uiState.paymentProcessState?.let { processState ->
        if (processState.isProcessing) {
            LoadingDialog(
                message = processState.message,
                progress = processState.progress,
                subtitle = if (processState.estimatedTimeSeconds > 0) {
                    "Tiempo estimado: ${processState.estimatedTimeSeconds}s"
                } else null
            )
        }
    }

// Loading dialog básico para otras operaciones
    if (uiState.isLoading && uiState.paymentProcessState == null) {
        LoadingDialog(message = "Procesando compra...")
    }

    // Diálogo de selección de método de pago
    if (showPaymentMethodDialog) {
        PaymentMethodSelectionDialog(
            paymentMethods = uiState.paymentMethods,
            selectedPaymentMethod = uiState.selectedPaymentMethod,
            onPaymentMethodSelected = {
                viewModel.onPaymentMethodSelected(it)
                showPaymentMethodDialog = false
            },
            onDismiss = { showPaymentMethodDialog = false },
            onAddNewMethod = {
                showPaymentMethodDialog = false
                onNavigateToAddPayment()
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Confirmar Compra") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Banner de Modo Demo
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFF3CD) // Amarillo suave
                ),
                shape = MaterialTheme.shapes.medium,
                border = BorderStroke(2.dp, Color(0xFFFFCA28))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Icono de laboratorio
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = Color(0xFFFFCA28).copy(alpha = 0.3f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🧪",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }

                    // Texto del banner
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "MODO DEMOSTRACIÓN",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF856404)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Esta es una simulación de pago. No se procesará dinero real.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF856404),
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            // Error
            if (uiState.error.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = uiState.error,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Resumen de la ruta
            uiState.route?.let { route ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Resumen de Compra",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(12.dp))

                        SummaryRow("Ruta:", "${route.origin} → ${route.destination}")
                        SummaryRow("Salida:", route.departureTime)
                        SummaryRow("Asiento:", uiState.seatNumber.toString())

                        Spacer(modifier = Modifier.height(8.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Total a Pagar:",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "S/ ${String.format("%.2f", route.price)}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Datos del Pasajero
                Text(
                    text = "Datos del Pasajero",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                CustomTextField(
                    value = uiState.passengerName,
                    onValueChange = { viewModel.onPassengerNameChange(it) },
                    label = "Nombre completo del pasajero",
                    leadingIcon = Icons.Default.Person,
                    isError = uiState.passengerNameError.isNotEmpty(),
                    errorMessage = uiState.passengerNameError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )

                Spacer(modifier = Modifier.height(16.dp))

                CustomTextField(
                    value = uiState.passengerDNI,
                    onValueChange = { viewModel.onPassengerDNIChange(it) },
                    label = "DNI del pasajero (8 dígitos)",
                    leadingIcon = Icons.Default.Badge,
                    isError = uiState.passengerDNIError.isNotEmpty(),
                    errorMessage = uiState.passengerDNIError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Método de Pago
                Text(
                    text = "Método de Pago",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Card con tarjetas de prueba
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "💳 Tarjetas de Prueba Disponibles",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Lista de tarjetas
                        TestCardItem(
                            number = "4111 1111 1111 1111",
                            status = "✅",
                            description = "VISA - Siempre aprobada"
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        TestCardItem(
                            number = "5555 5555 5555 4444",
                            status = "✅",
                            description = "Mastercard - Siempre aprobada"
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        TestCardItem(
                            number = "4000 0000 0000 0002",
                            status = "❌",
                            description = "VISA - Siempre rechazada"
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Nota adicional
                        Text(
                            text = "• CVV: Cualquier 3 dígitos (ej: 123)\n• Fecha: Cualquier fecha futura (ej: 12/25)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (uiState.selectedPaymentMethod != null) {
                    SelectedPaymentMethodCard(
                        paymentMethod = uiState.selectedPaymentMethod!!,
                        onClick = { showPaymentMethodDialog = true }
                    )
                } else {
                    OutlinedButton(
                        onClick = { showPaymentMethodDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CreditCard,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Seleccionar Método de Pago")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Info de seguridad
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Pago seguro y encriptado",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botón confirmar
                CustomButton(
                    text = "Confirmar y Pagar",
                    onClick = { viewModel.confirmPurchase() },
                    enabled = !uiState.isLoading && uiState.selectedPaymentMethod != null,
                    icon = Icons.Default.Payment
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
    Spacer(modifier = Modifier.height(4.dp))
}

@Composable
private fun SelectedPaymentMethodCard(
    paymentMethod: PaymentMethod,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = when (paymentMethod.type) {
                        PaymentType.CARD -> Icons.Default.CreditCard
                        PaymentType.YAPE -> Icons.Default.Phone
                        PaymentType.PLIN -> Icons.Default.PhoneAndroid
                    },
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = paymentMethod.getDisplayName(),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = paymentMethod.getHolderName(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Cambiar",
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
private fun PaymentMethodSelectionDialog(
    paymentMethods: List<PaymentMethod>,
    selectedPaymentMethod: PaymentMethod?,
    onPaymentMethodSelected: (PaymentMethod) -> Unit,
    onDismiss: () -> Unit,
    onAddNewMethod: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Payment, contentDescription = null) },
        title = { Text("Seleccionar Método de Pago") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                if (paymentMethods.isEmpty()) {
                    Text(
                        text = "No tienes métodos de pago guardados",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    paymentMethods.forEach { method ->
                        PaymentMethodDialogItem(
                            paymentMethod = method,
                            isSelected = method.id == selectedPaymentMethod?.id,
                            onClick = { onPaymentMethodSelected(method) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onAddNewMethod) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Agregar Nuevo")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun PaymentMethodDialogItem(
    paymentMethod: PaymentMethod,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (paymentMethod.type) {
                    PaymentType.CARD -> Icons.Default.CreditCard
                    PaymentType.YAPE -> Icons.Default.Phone
                    PaymentType.PLIN -> Icons.Default.PhoneAndroid
                },
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = paymentMethod.getDisplayName(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = paymentMethod.getHolderName(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Seleccionado",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
    @Composable
    private fun TestCardItem(
        number: String,
        status: String,
        description: String
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                    shape = MaterialTheme.shapes.small
                )
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = number,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = status,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}