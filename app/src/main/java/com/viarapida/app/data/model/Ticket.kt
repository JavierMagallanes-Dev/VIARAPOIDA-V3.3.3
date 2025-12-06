package com.viarapida.app.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import java.text.SimpleDateFormat
import java.util.Locale

data class Ticket(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val routeId: String = "",
    val passengerName: String = "",
    val passengerDNI: String = "",
    val seatNumber: Int = 0,
    val origin: String = "",
    val destination: String = "",
    val departureTime: String = "",
    val price: Double = 0.0,
    val purchaseDate: Timestamp = Timestamp.now(),
    val status: String = "Activo" // "Activo" o "Usado"
) {
    companion object {
        fun fromFirestore(document: DocumentSnapshot): Ticket? {
            return try {
                Ticket(
                    id = document.id,
                    userId = document.getString("userId") ?: "",
                    userName = document.getString("userName") ?: "",
                    routeId = document.getString("routeId") ?: "",
                    passengerName = document.getString("passengerName") ?: "",
                    passengerDNI = document.getString("passengerDNI") ?: "",
                    seatNumber = document.getLong("seatNumber")?.toInt() ?: 0,
                    origin = document.getString("origin") ?: "",
                    destination = document.getString("destination") ?: "",
                    departureTime = document.getString("departureTime") ?: "",
                    price = document.getDouble("price") ?: 0.0,
                    purchaseDate = document.getTimestamp("purchaseDate") ?: Timestamp.now(),
                    status = document.getString("status") ?: "Activo"
                )
            } catch (e: Exception) {
                null
            }
        }
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "userId" to userId,
            "userName" to userName,
            "routeId" to routeId,
            "passengerName" to passengerName,
            "passengerDNI" to passengerDNI,
            "seatNumber" to seatNumber,
            "origin" to origin,
            "destination" to destination,
            "departureTime" to departureTime,
            "price" to price,
            "purchaseDate" to purchaseDate,
            "status" to status
        )
    }

    fun getFormattedPurchaseDate(): String {
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            sdf.format(purchaseDate.toDate())
        } catch (e: Exception) {
            ""
        }
    }

    fun isActive(): Boolean = status == "Activo"
}