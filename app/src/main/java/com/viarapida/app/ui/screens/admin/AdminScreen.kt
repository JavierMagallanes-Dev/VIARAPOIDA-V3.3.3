package com.viarapida.app.ui.screens.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.viarapida.app.data.model.Ticket
import com.viarapida.app.ui.components.LoadingDialog
import com.viarapida.app.ui.theme.StatusActive
import com.viarapida.app.ui.theme.StatusUsed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    onNavigateBack: () -> Unit,
    viewModel: AdminViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isLoading || uiState.isUpdating) {
        LoadingDialog(
            message = if (uiState.isLoading) "Cargando tickets..." else "Actualizando..."
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de Administración") },
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
                .padding(16.dp)
        ) {
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

            // Estadísticas
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
                    Text(
                        text = "📊 Estadísticas",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    StatRow(
                        label = "Total de pasajes",
                        value = uiState.totalTickets.toString(),
                        emoji = "🎫"
                    )
                    StatRow(
                        label = "Pasajes activos",
                        value = uiState.activeTickets.toString(),
                        emoji = "✅",
                        valueColor = StatusActive
                    )
                    StatRow(
                        label = "Pasajes usados",
                        value = uiState.usedTickets.toString(),
                        emoji = "✔️",
                        valueColor = StatusUsed
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Lista de tickets
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Todos los Pasajes",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                TextButton(onClick = { viewModel.loadAllTickets() }) {
                    Text("🔄 Actualizar")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (uiState.tickets.isEmpty() && !uiState.isLoading) {
                // Sin tickets
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        text = "No hay pasajes registrados en el sistema",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.tickets) { ticket ->
                        AdminTicketCard(
                            ticket = ticket,
                            onMarkAsUsed = {
                                if (ticket.isActive()) {
                                    viewModel.markTicketAsUsed(ticket.id)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatRow(
    label: String,
    value: String,
    emoji: String,
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = emoji,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = valueColor
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
private fun AdminTicketCard(
    ticket: Ticket,
    onMarkAsUsed: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Encabezado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ID: ${ticket.id.take(8)}...",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = ticket.status,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (ticket.isActive()) StatusActive else StatusUsed
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Información
            TicketInfoRow(label = "Pasajero", value = ticket.passengerName)
            TicketInfoRow(label = "DNI", value = ticket.passengerDNI)
            TicketInfoRow(label = "Usuario", value = ticket.userName)
            TicketInfoRow(
                label = "Ruta",
                value = "${ticket.origin} → ${ticket.destination}"
            )
            TicketInfoRow(label = "Salida", value = ticket.departureTime)
            TicketInfoRow(label = "Asiento", value = ticket.seatNumber.toString())
            TicketInfoRow(
                label = "Precio",
                value = "S/ ${String.format("%.2f", ticket.price)}"
            )
            TicketInfoRow(label = "Compra", value = ticket.getFormattedPurchaseDate())

            // Botón de acción
            if (ticket.isActive()) {
                Spacer(modifier = Modifier.height(12.dp))
                TextButton(
                    onClick = onMarkAsUsed,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("✔️ Marcar como Usado")
                }
            }
        }
    }
}

@Composable
private fun TicketInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
    Spacer(modifier = Modifier.height(4.dp))
}