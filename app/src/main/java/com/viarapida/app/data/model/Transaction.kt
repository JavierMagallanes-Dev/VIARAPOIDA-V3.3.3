package com.viarapida.app.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Modelo para transacciones de pago
 */
data class Transaction(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val ticketId: String = "",
    val paymentMethodId: String = "",
    val paymentType: PaymentType = PaymentType.CARD,
    val amount: Double = 0.0,
    val status: TransactionStatus = TransactionStatus.PENDING,
    val transactionDate: Timestamp = Timestamp.now(),
    val description: String = "",
    val origin: String = "",
    val destination: String = "",
    val paymentMethodDisplay: String = "", // Para mostrar en UI
    val errorMessage: String = "" // Si hay error
) {
    companion object {
        fun fromFirestore(document: DocumentSnapshot): Transaction? {
            return try {
                Transaction(
                    id = document.id,
                    userId = document.getString("userId") ?: "",
                    userName = document.getString("userName") ?: "",
                    ticketId = document.getString("ticketId") ?: "",
                    paymentMethodId = document.getString("paymentMethodId") ?: "",
                    paymentType = PaymentType.valueOf(
                        document.getString("paymentType") ?: "CARD"
                    ),
                    amount = document.getDouble("amount") ?: 0.0,
                    status = TransactionStatus.valueOf(
                        document.getString("status") ?: "PENDING"
                    ),
                    transactionDate = document.getTimestamp("transactionDate") ?: Timestamp.now(),
                    description = document.getString("description") ?: "",
                    origin = document.getString("origin") ?: "",
                    destination = document.getString("destination") ?: "",
                    paymentMethodDisplay = document.getString("paymentMethodDisplay") ?: "",
                    errorMessage = document.getString("errorMessage") ?: ""
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
            "ticketId" to ticketId,
            "paymentMethodId" to paymentMethodId,
            "paymentType" to paymentType.name,
            "amount" to amount,
            "status" to status.name,
            "transactionDate" to transactionDate,
            "description" to description,
            "origin" to origin,
            "destination" to destination,
            "paymentMethodDisplay" to paymentMethodDisplay,
            "errorMessage" to errorMessage
        )
    }

    /**
     * Obtiene la fecha formateada
     */
    fun getFormattedDate(): String {
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            sdf.format(transactionDate.toDate())
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * Verifica si la transacción fue exitosa
     */
    fun isSuccessful(): Boolean = status == TransactionStatus.COMPLETED

    /**
     * Verifica si la transacción está pendiente
     */
    fun isPending(): Boolean = status == TransactionStatus.PENDING

    /**
     * Verifica si la transacción falló
     */
    fun isFailed(): Boolean = status == TransactionStatus.FAILED || status == TransactionStatus.CANCELLED
}

/**
 * Estados posibles de una transacción
 */
enum class TransactionStatus(val displayName: String) {
    PENDING("Pendiente"),
    PROCESSING("Procesando"),
    COMPLETED("Completada"),
    FAILED("Fallida"),
    CANCELLED("Cancelada"),
    REFUNDED("Reembolsada");

    /**
     * Obtiene el color asociado al estado
     */
    fun getColorName(): String {
        return when (this) {
            PENDING, PROCESSING -> "Warning"
            COMPLETED -> "Success"
            FAILED, CANCELLED -> "Error"
            REFUNDED -> "Info"
        }
    }
}