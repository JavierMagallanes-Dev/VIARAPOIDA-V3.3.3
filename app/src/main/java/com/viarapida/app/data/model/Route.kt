package com.viarapida.app.data.model

import com.google.firebase.firestore.DocumentSnapshot

data class Route(
    val id: String = "",
    val origin: String = "",
    val destination: String = "",
    val departureTime: String = "",
    val price: Double = 0.0,
    val totalSeats: Int = 40,
    val occupiedSeats: List<Int> = emptyList()
) {
    companion object {
        fun fromFirestore(document: DocumentSnapshot): Route? {
            return try {
                Route(
                    id = document.id,
                    origin = document.getString("origin") ?: "",
                    destination = document.getString("destination") ?: "",
                    departureTime = document.getString("departureTime") ?: "",
                    price = document.getDouble("price") ?: 0.0,
                    totalSeats = document.getLong("totalSeats")?.toInt() ?: 40,
                    occupiedSeats = (document.get("occupiedSeats") as? List<*>)
                        ?.mapNotNull { (it as? Long)?.toInt() } ?: emptyList()
                )
            } catch (e: Exception) {
                null
            }
        }
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "origin" to origin,
            "destination" to destination,
            "departureTime" to departureTime,
            "price" to price,
            "totalSeats" to totalSeats,
            "occupiedSeats" to occupiedSeats
        )
    }

    fun getAvailableSeats(): List<Int> {
        return (1..totalSeats).filter { it !in occupiedSeats }
    }

    fun getAvailableSeatsCount(): Int {
        return totalSeats - occupiedSeats.size
    }
}