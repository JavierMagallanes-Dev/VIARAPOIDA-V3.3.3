package com.viarapida.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirlineSeatReclineNormal
import androidx.compose.material.icons.filled.Chair
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.viarapida.app.ui.theme.SeatAvailable
import com.viarapida.app.ui.theme.SeatOccupied
import com.viarapida.app.ui.theme.SeatSelected
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
        // Leyenda mejorada
        SeatLegend()

        Spacer(modifier = Modifier.height(24.dp))

        // Indicador de cabina
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomStart = 0.dp, bottomEnd = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Chair,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "CONDUCTOR",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Grid de asientos
        val seatsPerRow = Constants.SEATS_PER_ROW
        val rows = (totalSeats + seatsPerRow - 1) / seatsPerRow

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            for (rowIndex in 0 until rows) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    for (colIndex in 0 until seatsPerRow) {
                        val seatNumber = rowIndex * seatsPerRow + colIndex + 1

                        if (seatNumber <= totalSeats) {
                            val isOccupied = seatNumber in occupiedSeats
                            val isSelected = seatNumber == selectedSeat

                            // Pasillo después del segundo asiento
                            if (colIndex == 2) {
                                Spacer(modifier = Modifier.width(20.dp))
                            }

                            SeatItem(
                                seatNumber = seatNumber,
                                isOccupied = isOccupied,
                                isSelected = isSelected,
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
}

@Composable
private fun SeatItem(
    seatNumber: Int,
    isOccupied: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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
        onClick = onClick,
        modifier = modifier.height(70.dp),
        enabled = !isOccupied,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor.copy(alpha = 0.2f),
            disabledContainerColor = SeatOccupied.copy(alpha = 0.2f)
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = when {
                isOccupied -> SeatOccupied
                isSelected -> SeatSelected
                else -> SeatAvailable
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 0.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.AirlineSeatReclineNormal,
                contentDescription = null,
                tint = when {
                    isOccupied -> SeatOccupied
                    isSelected -> SeatSelected
                    else -> SeatAvailable
                },
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = seatNumber.toString(),
                style = MaterialTheme.typography.labelMedium,
                color = when {
                    isOccupied -> SeatOccupied
                    isSelected -> SeatSelected
                    else -> SeatAvailable
                },
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SeatLegend() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            LegendItem(
                color = SeatAvailable,
                label = "Disponible"
            )
            LegendItem(
                color = SeatOccupied,
                label = "Ocupado"
            )
            LegendItem(
                color = SeatSelected,
                label = "Seleccionado"
            )
        }
    }
}

@Composable
private fun LegendItem(
    color: Color,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(MaterialTheme.shapes.small)
                .background(color.copy(alpha = 0.2f))
                .border(
                    width = 2.dp,
                    color = color,
                    shape = MaterialTheme.shapes.small
                )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
    }
}