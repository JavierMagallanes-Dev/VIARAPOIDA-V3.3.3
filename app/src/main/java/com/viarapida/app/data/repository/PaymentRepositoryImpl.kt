package com.viarapida.app.data.repository

import android.util.Log
import com.google.firebase.firestore.Query
import com.viarapida.app.data.model.PaymentMethod
import com.viarapida.app.data.model.Transaction
import com.viarapida.app.data.model.TransactionStatus
import com.viarapida.app.data.remote.FirebaseClient
import kotlinx.coroutines.tasks.await

class PaymentRepositoryImpl : PaymentRepository {

    private val firestore = FirebaseClient.firestore

    companion object {
        private const val TAG = "PaymentRepository"
        private const val PAYMENT_METHODS_COLLECTION = "paymentMethods"
        private const val TRANSACTIONS_COLLECTION = "transactions"
    }

    // ============ MÉTODOS DE PAGO ============

    override suspend fun savePaymentMethod(paymentMethod: PaymentMethod): Result<String> {
        return try {
            Log.d(TAG, "Guardando método de pago para usuario: ${paymentMethod.userId}")

            // Si es el método por defecto, quitar el default de los demás
            if (paymentMethod.isDefault) {
                removeDefaultFromOtherMethods(paymentMethod.userId)
            }

            val docRef = if (paymentMethod.id.isEmpty()) {
                // Nuevo método de pago
                firestore.collection(PAYMENT_METHODS_COLLECTION)
                    .add(paymentMethod.toMap())
                    .await()
            } else {
                // Actualizar existente
                firestore.collection(PAYMENT_METHODS_COLLECTION)
                    .document(paymentMethod.id)
                    .set(paymentMethod.toMap())
                    .await()
                firestore.collection(PAYMENT_METHODS_COLLECTION)
                    .document(paymentMethod.id)
            }

            Log.d(TAG, "Método de pago guardado con ID: ${docRef.id}")
            Result.success(docRef.id)

        } catch (e: Exception) {
            Log.e(TAG, "Error guardando método de pago: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun getUserPaymentMethods(userId: String): Result<List<PaymentMethod>> {
        return try {
            Log.d(TAG, "Obteniendo métodos de pago del usuario: $userId")

            val snapshot = firestore.collection(PAYMENT_METHODS_COLLECTION)
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            val paymentMethods = snapshot.documents.mapNotNull { doc ->
                PaymentMethod.fromFirestore(doc)
            }

            Log.d(TAG, "Métodos de pago obtenidos: ${paymentMethods.size}")
            Result.success(paymentMethods)

        } catch (e: Exception) {
            Log.e(TAG, "Error obteniendo métodos de pago: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun getPaymentMethodById(paymentMethodId: String): Result<PaymentMethod?> {
        return try {
            Log.d(TAG, "Obteniendo método de pago: $paymentMethodId")

            val doc = firestore.collection(PAYMENT_METHODS_COLLECTION)
                .document(paymentMethodId)
                .get()
                .await()

            val paymentMethod = PaymentMethod.fromFirestore(doc)
            Log.d(TAG, "Método de pago obtenido: ${paymentMethod?.getDisplayName()}")

            Result.success(paymentMethod)

        } catch (e: Exception) {
            Log.e(TAG, "Error obteniendo método de pago: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun deletePaymentMethod(paymentMethodId: String): Result<Unit> {
        return try {
            Log.d(TAG, "Eliminando método de pago: $paymentMethodId")

            firestore.collection(PAYMENT_METHODS_COLLECTION)
                .document(paymentMethodId)
                .delete()
                .await()

            Log.d(TAG, "Método de pago eliminado exitosamente")
            Result.success(Unit)

        } catch (e: Exception) {
            Log.e(TAG, "Error eliminando método de pago: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun setDefaultPaymentMethod(
        userId: String,
        paymentMethodId: String
    ): Result<Unit> {
        return try {
            Log.d(TAG, "Estableciendo método de pago por defecto: $paymentMethodId")

            // Quitar default de todos los métodos del usuario
            removeDefaultFromOtherMethods(userId)

            // Establecer el nuevo default
            firestore.collection(PAYMENT_METHODS_COLLECTION)
                .document(paymentMethodId)
                .update("isDefault", true)
                .await()

            Log.d(TAG, "Método de pago por defecto establecido")
            Result.success(Unit)

        } catch (e: Exception) {
            Log.e(TAG, "Error estableciendo método por defecto: ${e.message}", e)
            Result.failure(e)
        }
    }

    private suspend fun removeDefaultFromOtherMethods(userId: String) {
        try {
            val snapshot = firestore.collection(PAYMENT_METHODS_COLLECTION)
                .whereEqualTo("userId", userId)
                .whereEqualTo("isDefault", true)
                .get()
                .await()

            snapshot.documents.forEach { doc ->
                doc.reference.update("isDefault", false).await()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error removiendo default: ${e.message}", e)
        }
    }

    // ============ TRANSACCIONES ============

    override suspend fun createTransaction(transaction: Transaction): Result<String> {
        return try {
            Log.d(TAG, "Creando transacción para ticket: ${transaction.ticketId}")

            val docRef = firestore.collection(TRANSACTIONS_COLLECTION)
                .add(transaction.toMap())
                .await()

            Log.d(TAG, "Transacción creada con ID: ${docRef.id}")
            Result.success(docRef.id)

        } catch (e: Exception) {
            Log.e(TAG, "Error creando transacción: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun updateTransactionStatus(
        transactionId: String,
        status: TransactionStatus,
        errorMessage: String
    ): Result<Unit> {
        return try {
            Log.d(TAG, "Actualizando transacción $transactionId a estado: $status")

            val updates = mutableMapOf<String, Any>(
                "status" to status.name
            )
            if (errorMessage.isNotEmpty()) {
                updates["errorMessage"] = errorMessage
            }

            firestore.collection(TRANSACTIONS_COLLECTION)
                .document(transactionId)
                .update(updates)
                .await()

            Log.d(TAG, "Transacción actualizada exitosamente")
            Result.success(Unit)

        } catch (e: Exception) {
            Log.e(TAG, "Error actualizando transacción: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun getUserTransactions(userId: String): Result<List<Transaction>> {
        return try {
            Log.d(TAG, "Obteniendo transacciones del usuario: $userId")

            val snapshot = firestore.collection(TRANSACTIONS_COLLECTION)
                .whereEqualTo("userId", userId)
                .orderBy("transactionDate", Query.Direction.DESCENDING)
                .get()
                .await()

            val transactions = snapshot.documents.mapNotNull { doc ->
                Transaction.fromFirestore(doc)
            }

            Log.d(TAG, "Transacciones obtenidas: ${transactions.size}")
            Result.success(transactions)

        } catch (e: Exception) {
            Log.e(TAG, "Error obteniendo transacciones: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun getAllTransactions(): Result<List<Transaction>> {
        return try {
            Log.d(TAG, "Obteniendo todas las transacciones")

            val snapshot = firestore.collection(TRANSACTIONS_COLLECTION)
                .orderBy("transactionDate", Query.Direction.DESCENDING)
                .get()
                .await()

            val transactions = snapshot.documents.mapNotNull { doc ->
                Transaction.fromFirestore(doc)
            }

            Log.d(TAG, "Total de transacciones: ${transactions.size}")
            Result.success(transactions)

        } catch (e: Exception) {
            Log.e(TAG, "Error obteniendo todas las transacciones: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun getTransactionById(transactionId: String): Result<Transaction?> {
        return try {
            Log.d(TAG, "Obteniendo transacción: $transactionId")

            val doc = firestore.collection(TRANSACTIONS_COLLECTION)
                .document(transactionId)
                .get()
                .await()

            val transaction = Transaction.fromFirestore(doc)
            Log.d(TAG, "Transacción obtenida: ${transaction?.id}")

            Result.success(transaction)

        } catch (e: Exception) {
            Log.e(TAG, "Error obteniendo transacción: ${e.message}", e)
            Result.failure(e)
        }
    }
}