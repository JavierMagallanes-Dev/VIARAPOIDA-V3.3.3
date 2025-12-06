package com.viarapida.app.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot

/**
 * Modelo para métodos de pago del usuario
 */
data class PaymentMethod(
    val id: String = "",
    val userId: String = "",
    val type: PaymentType = PaymentType.CARD,
    val cardNumber: String = "", // Últimos 4 dígitos para tarjeta
    val cardHolderName: String = "",
    val cardBrand: String = "", // Visa, Mastercard, etc.
    val expiryDate: String = "", // MM/YY
    val phoneNumber: String = "", // Para Yape/Plin
    val accountName: String = "", // Nombre en cuenta Yape/Plin
    val isDefault: Boolean = false,
    val createdAt: Timestamp = Timestamp.now()
) {
    companion object {
        fun fromFirestore(document: DocumentSnapshot): PaymentMethod? {
            return try {
                PaymentMethod(
                    id = document.id,
                    userId = document.getString("userId") ?: "",
                    type = PaymentType.valueOf(document.getString("type") ?: "CARD"),
                    cardNumber = document.getString("cardNumber") ?: "",
                    cardHolderName = document.getString("cardHolderName") ?: "",
                    cardBrand = document.getString("cardBrand") ?: "",
                    expiryDate = document.getString("expiryDate") ?: "",
                    phoneNumber = document.getString("phoneNumber") ?: "",
                    accountName = document.getString("accountName") ?: "",
                    isDefault = document.getBoolean("isDefault") ?: false,
                    createdAt = document.getTimestamp("createdAt") ?: Timestamp.now()
                )
            } catch (e: Exception) {
                null
            }
        }
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "userId" to userId,
            "type" to type.name,
            "cardNumber" to cardNumber,
            "cardHolderName" to cardHolderName,
            "cardBrand" to cardBrand,
            "expiryDate" to expiryDate,
            "phoneNumber" to phoneNumber,
            "accountName" to accountName,
            "isDefault" to isDefault,
            "createdAt" to createdAt
        )
    }

    /**
     * Obtiene el nombre para mostrar del método de pago
     */
    fun getDisplayName(): String {
        return when (type) {
            PaymentType.CARD -> "$cardBrand •••• $cardNumber"
            PaymentType.YAPE -> "Yape - $phoneNumber"
            PaymentType.PLIN -> "Plin - $phoneNumber"
        }
    }

    /**
     * Obtiene el nombre del titular
     */
    fun getHolderName(): String {
        return when (type) {
            PaymentType.CARD -> cardHolderName
            PaymentType.YAPE, PaymentType.PLIN -> accountName
        }
    }
}

/**
 * Tipos de métodos de pago disponibles
 */
enum class PaymentType {
    CARD,    // Tarjeta de crédito/débito
    YAPE,    // Yape
    PLIN     // Plin
}

/**
 * Marcas de tarjetas soportadas
 */
enum class CardBrand(val displayName: String) {
    VISA("Visa"),
    MASTERCARD("Mastercard"),
    AMEX("American Express"),
    DINERS("Diners Club"),
    UNKNOWN("Desconocida");

    companion object {
        /**
         * Detecta la marca de la tarjeta basado en el número
         */
        fun fromCardNumber(cardNumber: String): CardBrand {
            val cleanNumber = cardNumber.replace(" ", "")
            return when {
                cleanNumber.startsWith("4") -> VISA
                cleanNumber.startsWith("5") -> MASTERCARD
                cleanNumber.startsWith("3") -> when {
                    cleanNumber.startsWith("34") || cleanNumber.startsWith("37") -> AMEX
                    cleanNumber.startsWith("36") || cleanNumber.startsWith("38") -> DINERS
                    else -> UNKNOWN
                }
                else -> UNKNOWN
            }
        }
    }
}