package com.viarapida.app.data.repository

import com.viarapida.app.data.model.Ticket

interface TicketRepository {
    suspend fun createTicket(ticket: Ticket): Result<String>
    suspend fun getTicketById(ticketId: String): Result<Ticket?>
    suspend fun getUserTickets(userId: String): Result<List<Ticket>>
    suspend fun getAllTickets(): Result<List<Ticket>>
    suspend fun updateTicketStatus(ticketId: String, status: String): Result<Unit>
}