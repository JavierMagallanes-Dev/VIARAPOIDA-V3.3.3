package com.viarapida.app.ui.screens.home

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.viarapida.app.ui.components.CustomButton
import com.viarapida.app.ui.components.LoadingDialog
import com.viarapida.app.ui.components.OutlinedCustomButton
import com.viarapida.app.ui.navigation.NavGraph
import com.viarapida.app.ui.theme.GradientEnd
import com.viarapida.app.ui.theme.GradientStart


@Composable
fun HomeScreen(
    onNavigateToSearch: () -> Unit,
    onNavigateToMyTickets: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onNavigateToLogin: () -> Unit,
    navController: NavHostController,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.logoutSuccess) {
        if (uiState.logoutSuccess) {
            onNavigateToLogin()
        }
    }

    if (uiState.isLoading) {
        LoadingDialog(message = "Cargando...")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header con gradiente
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(GradientStart, GradientEnd)
                        )
                    )
                    .padding(24.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsBus,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.size(12.dp))
                        Text(
                            text = "ViaRapida",
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    uiState.user?.let { user ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
                            ),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.size(12.dp))
                                Column {
                                    Text(
                                        text = "Bienvenido,",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                                    )
                                    Text(
                                        text = user.name,
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Contenido principal
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "¿Qué deseas hacer hoy?",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Botones principales
                ActionCard(
                    icon = Icons.Default.Search,
                    title = "Buscar Rutas",
                    description = "Encuentra tu próximo viaje",
                    onClick = onNavigateToSearch
                )

                ActionCard(
                    icon = Icons.Default.ConfirmationNumber,
                    title = "Mis Pasajes",
                    description = "Revisa tus tickets comprados",
                    onClick = onNavigateToMyTickets
                )


                // Panel Admin (solo si es admin)
                if (uiState.user?.isAdmin == true) {
                    ActionCard(
                        icon = Icons.Default.AdminPanelSettings,
                        title = "Panel Administrador",
                        description = "Gestiona el sistema",
                        onClick = onNavigateToAdmin,
                        isHighlighted = true
                    )
                }
                // En HomeScreen, AGREGAR estos ActionCard:

                ActionCard(
                    icon = Icons.Default.CreditCard,
                    title = "Métodos de Pago",
                    description = "Gestiona tus formas de pago",
                    onClick = {
                        navController.navigate(NavGraph.PaymentMethods.route)
                    }
                )

                ActionCard(
                    icon = Icons.Default.Receipt,
                    title = "Historial de Transacciones",
                    description = "Revisa tus pagos realizados",
                    onClick = {
                        navController.navigate(NavGraph.TransactionHistory.route)
                    }
                )


                if (uiState.user?.isAdmin == true) {
                    ActionCard(
                        icon = Icons.Default.AdminPanelSettings,
                        title = "Panel Administrador",
                        description = "Gestiona el sistema",
                        onClick = onNavigateToAdmin,
                        isHighlighted = true
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Cerrar Sesión
                OutlinedCustomButton(
                    text = "Cerrar Sesión",
                    onClick = { viewModel.logout() },
                    icon = Icons.Default.Logout
                )
            }

                Spacer(modifier = Modifier.height(16.dp))


            }
        }
    }


@Composable
private fun ActionCard(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit,
    isHighlighted: Boolean = false
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = if (isHighlighted)
                MaterialTheme.colorScheme.secondaryContainer
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 8.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(
                    containerColor = if (isHighlighted)
                        MaterialTheme.colorScheme.secondary
                    else
                        MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isHighlighted)
                            MaterialTheme.colorScheme.onSecondary
                        else
                            MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.size(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

}