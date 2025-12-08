package com.viarapida.app.ui.utils

import com.viarapida.app.data.model.CardBrand

object PaymentValidators {

    // ✅ TARJETAS DE PRUEBA PERMITIDAS
    private val TEST_CARDS = setOf(
        "4111111111111111", // VISA - Aprobada
        "5555555555554444", // Mastercard - Aprobada
        "4000000000000002"  // VISA - Rechazada (para testing)
    )

    /**
     * Valida número de tarjeta usando algoritmo de Luhn
     * ✅ MODIFICADO: Acepta tarjetas de prueba sin validación de Luhn
     */
    fun validateCardNumber(cardNumber: String): ValidationResult {
        val cleaned = cardNumber.replace(" ", "").replace("-", "")

        return when {
            cleaned.isBlank() -> ValidationResult(false, "El número de tarjeta es requerido")
            !cleaned.all { it.isDigit() } -> ValidationResult(false, "El número solo debe contener dígitos")
            cleaned.length < 13 || cleaned.length > 19 -> ValidationResult(false, "Número de tarjeta inválido")
            // ✅ NUEVO: Permitir tarjetas de prueba sin validación de Luhn
            cleaned in TEST_CARDS -> ValidationResult(true, "")
            !isValidLuhn(cleaned) -> ValidationResult(false, "Número de tarjeta inválido")
            else -> ValidationResult(true, "")
        }
    }

    /**
     * Algoritmo de Luhn para validar número de tarjeta
     */
    private fun isValidLuhn(cardNumber: String): Boolean {
        var sum = 0
        var isEven = false

        for (i in cardNumber.length - 1 downTo 0) {
            var digit = cardNumber[i].toString().toInt()

            if (isEven) {
                digit *= 2
                if (digit > 9) {
                    digit -= 9
                }
            }

            sum += digit
            isEven = !isEven
        }

        return sum % 10 == 0
    }

    /**
     * Valida fecha de expiración MM/YY
     * ✅ MODIFICADO: Acepta cualquier fecha futura para modo demo
     */
    fun validateExpiryDate(expiryDate: String): ValidationResult {
        val cleaned = expiryDate.replace("/", "").replace(" ", "")

        return when {
            cleaned.isBlank() -> ValidationResult(false, "La fecha de expiración es requerida")
            cleaned.length != 4 -> ValidationResult(false, "Formato inválido (MM/YY)")
            !cleaned.all { it.isDigit() } -> ValidationResult(false, "Solo se permiten números")
            else -> {
                val month = cleaned.substring(0, 2).toIntOrNull() ?: 0
                val year = cleaned.substring(2, 4).toIntOrNull() ?: 0

                when {
                    month !in 1..12 -> ValidationResult(false, "Mes inválido")
                    // ✅ MODIFICADO: Permitir fechas del año actual o futuras
                    year < (java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) % 100) ->
                        ValidationResult(false, "Tarjeta expirada")
                    else -> ValidationResult(true, "")
                }
            }
        }
    }

    /**
     * Valida CVV
     * ✅ MODIFICADO: Más flexible para modo demo
     */
    fun validateCVV(cvv: String, cardBrand: CardBrand = CardBrand.UNKNOWN): ValidationResult {
        val expectedLength = if (cardBrand == CardBrand.AMEX) 4 else 3

        return when {
            cvv.isBlank() -> ValidationResult(false, "El CVV es requerido")
            !cvv.all { it.isDigit() } -> ValidationResult(false, "El CVV solo debe contener números")
            cvv.length < 3 || cvv.length > 4 -> ValidationResult(false, "CVV debe tener 3 o 4 dígitos")
            else -> ValidationResult(true, "")
        }
    }

    /**
     * Valida nombre del titular
     */
    fun validateCardHolderName(name: String): ValidationResult {
        return when {
            name.isBlank() -> ValidationResult(false, "El nombre del titular es requerido")
            name.length < 3 -> ValidationResult(false, "Nombre demasiado corto")
            name.length > 50 -> ValidationResult(false, "Nombre demasiado largo")
            !name.matches(Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")) ->
                ValidationResult(false, "Solo se permiten letras y espacios")
            else -> ValidationResult(true, "")
        }
    }

    /**
     * Valida número de teléfono para Yape/Plin (Perú)
     */
    fun validatePhoneNumber(phone: String): ValidationResult {
        val cleaned = phone.replace(" ", "").replace("-", "")

        return when {
            cleaned.isBlank() -> ValidationResult(false, "El número de teléfono es requerido")
            !cleaned.all { it.isDigit() } -> ValidationResult(false, "Solo se permiten números")
            cleaned.length != 9 -> ValidationResult(false, "El número debe tener 9 dígitos")
            !cleaned.startsWith("9") -> ValidationResult(false, "El número debe empezar con 9")
            else -> ValidationResult(true, "")
        }
    }

    /**
     * Formatea número de tarjeta con espacios cada 4 dígitos
     */
    fun formatCardNumber(cardNumber: String): String {
        val cleaned = cardNumber.replace(" ", "")
        return cleaned.chunked(4).joinToString(" ")
    }

    /**
     * Formatea fecha de expiración MM/YY
     */
    fun formatExpiryDate(expiryDate: String): String {
        val cleaned = expiryDate.replace("/", "").replace(" ", "")
        return if (cleaned.length >= 2) {
            "${cleaned.substring(0, 2)}/${cleaned.substring(2)}"
        } else {
            cleaned
        }
    }

    /**
     * Obtiene los últimos 4 dígitos de una tarjeta
     */
    fun getLastFourDigits(cardNumber: String): String {
        val cleaned = cardNumber.replace(" ", "").replace("-", "")
        return if (cleaned.length >= 4) {
            cleaned.takeLast(4)
        } else {
            cleaned
        }
    }

    /**
     * Enmascara un número de tarjeta: **** **** **** 1234
     */
    fun maskCardNumber(cardNumber: String): String {
        val cleaned = cardNumber.replace(" ", "")
        return if (cleaned.length >= 4) {
            "**** **** **** ${cleaned.takeLast(4)}"
        } else {
            cardNumber
        }
    }

    /**
     * Enmascara un número de teléfono: *** *** 789
     */
    fun maskPhoneNumber(phone: String): String {
        val cleaned = phone.replace(" ", "").replace("-", "")
        return if (cleaned.length >= 3) {
            "*** *** ${cleaned.takeLast(3)}"
        } else {
            phone
        }
    }
}