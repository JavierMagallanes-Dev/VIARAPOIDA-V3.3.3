package com.viarapida.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.viarapida.app.ui.theme.*
import com.viarapida.app.ui.utils.Constants

@Composable
fun SeatGrid(
    totalSeats: Int = Constants.TOTAL_SEATS,
    occupiedSeats: List<Int>,
    selectedSeat: Int?,
    onSeatSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Leyenda mejorada con estadísticas
        ImprovedSeatLegend(
            totalSeats = totalSeats,
            occupiedCount = occupiedSeats.size,
            selectedSeat = selectedSeat
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Cabina del conductor con diseño premium
        DriverCabinHeader()

        Spacer(modifier = Modifier.height(16.dp))

        // Grid de asientos con etiquetas de fila
        val seatsPerRow = Constants.SEATS_PER_ROW
        val rows = (totalSeats + seatsPerRow - 1) / seatsPerRow

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Indicador de posición
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "← VENTANA",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                    Spacer(modifier = Modifier.width(60.dp))
                    Text(
                        text = "PASILLO 🚶",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(60.dp))
                    Text(
                        text = "VENTANA →",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                for (rowIndex in 0 until rows) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Etiqueta de fila
                        Text(
                            text = "${rowIndex + 1}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.width(24.dp),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )

                        for (colIndex in 0 until seatsPerRow) {
                            val seatNumber = rowIndex * seatsPerRow + colIndex + 1

                            if (seatNumber <= totalSeats) {
                                val isOccupied = seatNumber in occupiedSeats
                                val isSelected = seatNumber == selectedSeat
                                val isWindow = colIndex == 0 || colIndex == 3

                                // Pasillo entre asientos 2 y 3
                                if (colIndex == 2) {
                                    Box(
                                        modifier = Modifier
                                            .width(32.dp)
                                            .height(70.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Divider(
                                            modifier = Modifier
                                                .width(2.dp)
                                                .fillMaxHeight(),
                                            color = MaterialTheme.colorScheme.outlineVariant
                                        )
                                    }
                                }

                                EnhancedSeatItem(
                                    seatNumber = seatNumber,
                                    isOccupied = isOccupied,
                                    isSelected = isSelected,
                                    isWindow = isWindow,
                                    onClick = {
                                        if (!isOccupied) {
                                            onSeatSelected(seatNumber)
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }

        // Mostrar selección actual si existe
        if (selectedSeat != null) {
            Spacer(modifier = Modifier.height(16.dp))
            SelectedSeatCard(
                seatNumber = selectedSeat,
                isWindow = (selectedSeat - 1) % Constants.SEATS_PER_ROW == 0 ||
                        (selectedSeat - 1) % Constants.SEATS_PER_ROW == 3
            )
        }
    }
}

@Composable
private fun ImprovedSeatLegend(
    totalSeats: Int,
    occupiedCount: Int,
    selectedSeat: Int?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                    text = "Disponibilidad",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.EventSeat,
                        contentDescription = null,
                        tint = SeatAvailable,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "${totalSeats - occupiedCount}/$totalSeats",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SeatAvailable
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                EnhancedLegendItem(
                    color = SeatAvailable,
                    label = "Disponible",
                    icon = Icons.Default.CheckCircle
                )
                EnhancedLegendItem(
                    color = SeatOccupied,
                    label = "Ocupado",
                    icon = Icons.Default.Cancel
                )
                EnhancedLegendItem(
                    color = SeatSelected,
                    label = "Seleccionado",
                    icon = Icons.Default.Star
                )
            }

            if (selectedSeat != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = SeatSelected,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tu selección: Asiento $selectedSeat",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun DriverCabinHeader() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.DirectionsBus,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "🚗 CABINA DEL CONDUCTOR",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Icon(
                imageVector = Icons.Default.DirectionsBus,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun EnhancedSeatItem(
    seatNumber: Int,
    isOccupied: Boolean,
    isSelected: Boolean,
    isWindow: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed && !isOccupied) 0.95f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessHigh),
        label = "seat scale"
    )

    val backgroundColor by animateColorAsState(
        targetValue = when {
            isOccupied -> SeatOccupied
            isSelected -> SeatSelected
            else -> SeatAvailable
        },
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "seat color"
    )

    Card(
        onClick = {
            if (!isOccupied) {
                isPressed = true
                onClick()
            }
        },
        modifier = modifier
            .height(75.dp)
            .scale(scale),
        enabled = !isOccupied,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor.copy(alpha = 0.15f),
            disabledContainerColor = SeatOccupied.copy(alpha = 0.1f)
        ),
        border = BorderStroke(
            width = if (isSelected) 3.dp else 2.dp,
            color = when {
                isOccupied -> SeatOccupied.copy(alpha = 0.5f)
                isSelected -> SeatSelected
                else -> backgroundColor
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = if (isSelected) {
                        Brush.verticalGradient(
                            colors = listOf(
                                backgroundColor.copy(alpha = 0.3f),
                                backgroundColor.copy(alpha = 0.1f)
                            )
                        )
                    } else {
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Transparent
                            )
                        )
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = when {
                        isSelected -> Icons.Default.CheckCircle
                        isOccupied -> Icons.Default.Block
                        else -> Icons.Default.EventSeat
                    },
                    contentDescription = null,
                    tint = when {
                        isOccupied -> SeatOccupied
                        isSelected -> SeatSelected
                        else -> backgroundColor
                    },
                    modifier = Modifier.size(28.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = seatNumber.toString(),
                    style = MaterialTheme.typography.labelLarge,
                    color = when {
                        isOccupied -> SeatOccupied
                        isSelected -> SeatSelected
                        else -> backgroundColor
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                if (isWindow && !isOccupied) {
                    Text(
                        text = "🪟",
                        fontSize = 10.sp
                    )
                }
            }
        }
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(150)
            isPressed = false
        }
    }
}

@Composable
private fun EnhancedLegendItem(
    color: Color,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(color.copy(alpha = 0.2f))
                .border(
                    width = 2.dp,
                    color = color,
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(18.dp)
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun SelectedSeatCard(
    seatNumber: Int,
    isWindow: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = SeatSelected.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(2.dp, SeatSelected)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = SeatSelected,
                    modifier = Modifier.size(32.dp)
                )
                Column {
                    Text(
                        text = "Asiento Seleccionado",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "Nº $seatNumber",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = SeatSelected
                    )
                }
            }

            if (isWindow) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🪟", fontSize = 16.sp)
                        Text(
                            text = "Ventana",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }
    }
}