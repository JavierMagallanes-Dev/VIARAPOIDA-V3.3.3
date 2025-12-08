package com.viarapida.app.ui.screens.purchase

/**
 * Estados del proceso de pago
 */
enum class PaymentStep(val displayMessage: String, val progress: Float) {
    IDLE("Preparando...", 0f),
    VALIDATING("🔐 Verificando método de pago...", 0.25f),
    AUTHORIZING("💳 Contactando con procesador...", 0.50f),
    PROCESSING("⚡ Procesando transacción...", 0.75f),
    COMPLETING("✅ Confirmando pago...", 0.95f),
    SUCCESS("✅ ¡Pago exitoso!", 1.0f),
    FAILED("❌ Pago rechazado", 1.0f)
}

/**
 * Estado del procesamiento de pago para la UI
 */
data class PaymentProcessState(
    val isProcessing: Boolean = false,
    val currentStep: PaymentStep = PaymentStep.IDLE,
    val progress: Float = 0f,
    val message: String = "",
    val estimatedTimeSeconds: Int = 0
)