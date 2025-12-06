package com.viarapida.app.data.repository

import com.viarapida.app.data.model.PaymentMethod
import com.viarapida.app.data.model.Transaction

/**
 * Interfaz del repositorio de pagos
 */
interface PaymentRepository {
    // Métodos de Pago
    suspend fun savePaymentMethod(paymentMethod: PaymentMethod): Result<String>
    suspend fun getUserPaymentMethods(userId: String): Result<List<PaymentMethod>>
    suspend fun getPaymentMethodById(paymentMethodId: String): Result<PaymentMethod?>
    suspend fun deletePaymentMethod(paymentMethodId: String): Result<Unit>
    suspend fun setDefaultPaymentMethod(userId: String, paymentMethodId: String): Result<Unit>

    // Transacciones
    suspend fun createTransaction(transaction: Transaction): Result<String>
    suspend fun updateTransactionStatus(
        transactionId: String,
        status: com.viarapida.app.data.model.TransactionStatus,
        errorMessage: String = ""
    ): Result<Unit>
    suspend fun getUserTransactions(userId: String): Result<List<Transaction>>
    suspend fun getAllTransactions(): Result<List<Transaction>>
    suspend fun getTransactionById(transactionId: String): Result<Transaction?>
}