package com.viarapida.app.ui.screens.payment

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.viarapida.app.data.model.PaymentType
import com.viarapida.app.ui.components.CustomButton
import com.viarapida.app.ui.components.CustomTextField
import com.viarapida.app.ui.components.LoadingDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPaymentMethodScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddPaymentMethodViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            onNavigateBack()
        }
    }

    if (uiState.isLoading) {
        LoadingDialog(message = "Guardando método de pago...")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agregar Método de Pago", fontWeight = FontWeight.Bold) },
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


            Spacer(modifier = Modifier.height(24.dp))

            // Selector de tipo de pago
            Text(
                text = "Tipo de Método",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            // ✅ NUEVO: Helper text
            Text(
                text = "Selecciona cómo deseas pagar tus pasajes",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PaymentType.values().forEach { type ->
                    PaymentTypeChip(
                        type = type,
                        isSelected = uiState.selectedType == type,
                        onClick = { viewModel.onPaymentTypeSelected(type) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Formulario según tipo seleccionado
            when (uiState.selectedType) {
                PaymentType.CARD -> CardForm(
                    uiState = uiState,
                    viewModel = viewModel
                )
                PaymentType.YAPE, PaymentType.PLIN -> DigitalWalletForm(
                    uiState = uiState,
                    viewModel = viewModel
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Checkbox predeterminado con explicación
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = uiState.isDefault,
                        onCheckedChange = { viewModel.onDefaultChanged(it) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Método predeterminado",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Se usará automáticamente en tus compras",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Error con animación
            AnimatedVisibility(visible = uiState.error.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = uiState.error,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Botón guardar con tooltip
            Column {
                CustomButton(
                    text = "Guardar Método de Pago",
                    onClick = { viewModel.savePaymentMethod() },
                    enabled = !uiState.isLoading,
                    icon = Icons.Default.Save
                )

                Spacer(modifier = Modifier.height(8.dp))

                // ✅ NUEVO: Mensaje de ayuda
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Podrás usarlo inmediatamente después de guardarlo",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}



@Composable
private fun PaymentTypeChip(
    type: PaymentType,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Text(
                text = when (type) {
                    PaymentType.CARD -> "Tarjeta"
                    PaymentType.YAPE -> "Yape"
                    PaymentType.PLIN -> "Plin"
                },
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.labelMedium
            )
        },
        leadingIcon = {
            Icon(
                imageVector = when (type) {
                    PaymentType.CARD -> Icons.Default.CreditCard
                    PaymentType.YAPE -> Icons.Default.Phone
                    PaymentType.PLIN -> Icons.Default.PhoneAndroid
                },
                contentDescription = null
            )
        },
        modifier = modifier
    )
}

@Composable
private fun CardForm(
    uiState: AddPaymentMethodUiState,
    viewModel: AddPaymentMethodViewModel
) {
    Column {
        Text(
            text = "Datos de la Tarjeta",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            value = uiState.cardNumber,
            onValueChange = { viewModel.onCardNumberChanged(it) },
            label = "Número de Tarjeta",
            placeholder = "4111 1111 1111 1111",
            leadingIcon = Icons.Default.CreditCard,
            isError = uiState.cardNumberError.isNotEmpty(),
            errorMessage = uiState.cardNumberError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            value = uiState.cardHolderName,
            onValueChange = { viewModel.onCardHolderNameChanged(it) },
            label = "Nombre del Titular",
            placeholder = "JUAN PEREZ",
            leadingIcon = Icons.Default.Person,
            isError = uiState.cardHolderNameError.isNotEmpty(),
            errorMessage = uiState.cardHolderNameError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CustomTextField(
                value = uiState.expiryDate,
                onValueChange = { viewModel.onExpiryDateChanged(it) },
                label = "Vencimiento",
                placeholder = "MM/YY",
                leadingIcon = Icons.Default.CalendarToday,
                isError = uiState.expiryDateError.isNotEmpty(),
                errorMessage = uiState.expiryDateError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )

            CustomTextField(
                value = uiState.cvv,
                onValueChange = { viewModel.onCvvChanged(it) },
                label = "CVV",
                placeholder = "123",
                leadingIcon = Icons.Default.Lock,
                isError = uiState.cvvError.isNotEmpty(),
                errorMessage = uiState.cvvError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isPassword = true,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))




        Spacer(modifier = Modifier.height(12.dp))

        // Info de seguridad
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
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
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Tu información está segura y encriptada",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

// ✅ NUEVO: Componente para mostrar tarjetas de prueba
@Composable
private fun TestCardRow(number: String, brand: String, status: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = number,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Text(
                text = brand,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
            )
        }
        Text(
            text = status,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
private fun DigitalWalletForm(
    uiState: AddPaymentMethodUiState,
    viewModel: AddPaymentMethodViewModel
) {
    Column {
        val walletName = if (uiState.selectedType == PaymentType.YAPE) "Yape" else "Plin"

        Text(
            text = "Datos de $walletName",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            value = uiState.phoneNumber,
            onValueChange = { viewModel.onPhoneNumberChanged(it) },
            label = "Número de Celular",
            placeholder = "987654321",
            leadingIcon = Icons.Default.Phone,
            isError = uiState.phoneNumberError.isNotEmpty(),
            errorMessage = uiState.phoneNumberError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            value = uiState.accountName,
            onValueChange = { viewModel.onAccountNameChanged(it) },
            label = "Nombre de la Cuenta",
            placeholder = "Juan Pérez",
            leadingIcon = Icons.Default.Person,
            isError = uiState.accountNameError.isNotEmpty(),
            errorMessage = uiState.accountNameError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Asegúrate que el número esté vinculado a tu cuenta de $walletName",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}