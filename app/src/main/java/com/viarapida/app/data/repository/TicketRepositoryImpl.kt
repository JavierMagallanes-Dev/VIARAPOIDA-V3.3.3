package com.viarapida.app.data.repository

import android.util.Log
import com.viarapida.app.data.model.Ticket
import com.viarapida.app.data.remote.FirebaseClient
import kotlinx.coroutines.tasks.await

class TicketRepositoryImpl : TicketRepository {

    private val firestore = FirebaseClient.firestore

    private companion object {
        const val TAG = "TicketRepository"
        const val TICKETS_COLLECTION = "tickets"
    }

    override suspend fun createTicket(ticket: Ticket): Result<String> {
        return try {
            Log.d(TAG, "Creando ticket para: ${ticket.passengerName}")

            val docRef = firestore.collection(TICKETS_COLLECTION)
                .add(ticket.toMap())
                .await()

            Log.d(TAG, "Ticket creado con ID: ${docRef.id}")
            Result.success(docRef.id)

        } catch (e: Exception) {
            Log.e(TAG, "Error creando ticket: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun getTicketById(ticketId: String): Result<Ticket?> {
        return try {
            Log.d(TAG, "Obteniendo ticket: $ticketId")

            val doc = firestore.collection(TICKETS_COLLECTION)
                .document(ticketId)
                .get()
                .await()

            val ticket = Ticket.fromFirestore(doc)
            Log.d(TAG, "Ticket obtenido: ${ticket?.passengerName}")

            Result.success(ticket)

        } catch (e: Exception) {
            Log.e(TAG, "Error obteniendo ticket: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun getUserTickets(userId: String): Result<List<Ticket>> {
        return try {
            Log.d(TAG, "Obteniendo tickets del usuario: $userId")

            val snapshot = firestore.collection(TICKETS_COLLECTION)
                .whereEqualTo("userId", userId)
                .orderBy("purchaseDate", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()

            val tickets = snapshot.documents.mapNotNull { doc ->
                Ticket.fromFirestore(doc)
            }

            Log.d(TAG, "Tickets del usuario obtenidos: ${tickets.size}")
            Result.success(tickets)

        } catch (e: Exception) {
            Log.e(TAG, "Error obteniendo tickets del usuario: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun getAllTickets(): Result<List<Ticket>> {
        return try {
            Log.d(TAG, "Obteniendo todos los tickets")

            val snapshot = firestore.collection(TICKETS_COLLECTION)
                .orderBy("purchaseDate", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()

            val tickets = snapshot.documents.mapNotNull { doc ->
                Ticket.fromFirestore(doc)
            }

            Log.d(TAG, "Tickets obtenidos: ${tickets.size}")
            Result.success(tickets)

        } catch (e: Exception) {
            Log.e(TAG, "Error obteniendo todos los tickets: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun updateTicketStatus(ticketId: String, status: String): Result<Unit> {
        return try {
            Log.d(TAG, "Actualizando estado del ticket: $ticketId a $status")

            firestore.collection(TICKETS_COLLECTION)
                .document(ticketId)
                .update("status", status)
                .await()

            Log.d(TAG, "Estado del ticket actualizado exitosamente")
            Result.success(Unit)

        } catch (e: Exception) {
            Log.e(TAG, "Error actualizando estado del ticket: ${e.message}", e)
            Result.failure(e)
        }
    }
}