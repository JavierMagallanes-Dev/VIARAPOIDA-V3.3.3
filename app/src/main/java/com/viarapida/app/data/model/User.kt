package com.viarapida.app.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val isAdmin: Boolean = false,
    val createdAt: Timestamp = Timestamp.now()
) {
    companion object {
        fun fromFirestore(document: DocumentSnapshot): User? {
            return try {
                User(
                    id = document.id,
                    name = document.getString("name") ?: "",
                    email = document.getString("email") ?: "",
                    isAdmin = document.getBoolean("isAdmin") ?: false,
                    createdAt = document.getTimestamp("createdAt") ?: Timestamp.now()
                )
            } catch (e: Exception) {
                null
            }
        }
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "name" to name,
            "email" to email,
            "isAdmin" to isAdmin,
            "createdAt" to createdAt
        )
    }
}