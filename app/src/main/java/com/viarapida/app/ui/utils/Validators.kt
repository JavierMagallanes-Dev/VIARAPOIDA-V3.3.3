package com.viarapida.app.ui.utils

import android.util.Patterns

object Validators {

    fun validateEmail(email: String): ValidationResult {
        return when {
            email.isBlank() -> ValidationResult(false, "El correo no puede estar vacío")
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                ValidationResult(false, "Correo electrónico inválido")
            else -> ValidationResult(true, "")
        }
    }

    fun validatePassword(password: String): ValidationResult {
        return when {
            password.isBlank() -> ValidationResult(false, "La contraseña no puede estar vacía")
            password.length < Constants.MIN_PASSWORD_LENGTH ->
                ValidationResult(false, "La contraseña debe tener al menos ${Constants.MIN_PASSWORD_LENGTH} caracteres")
            else -> ValidationResult(true, "")
        }
    }

    fun validateName(name: String): ValidationResult {
        return when {
            name.isBlank() -> ValidationResult(false, "El nombre no puede estar vacío")
            name.length < 3 -> ValidationResult(false, "El nombre debe tener al menos 3 caracteres")
            name.length > 50 -> ValidationResult(false, "El nombre es demasiado largo")
            !name.matches(Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")) ->
                ValidationResult(false, "El nombre solo debe contener letras y espacios")
            else -> ValidationResult(true, "")
        }
    }

    fun validateDNI(dni: String): ValidationResult {
        return when {
            dni.isBlank() -> ValidationResult(false, "El DNI no puede estar vacío")
            dni.length != Constants.DNI_LENGTH ->
                ValidationResult(false, "El DNI debe tener ${Constants.DNI_LENGTH} dígitos")
            !dni.all { it.isDigit() } ->
                ValidationResult(false, "El DNI solo debe contener números")
            dni.all { it == dni[0] } ->
                ValidationResult(false, "DNI inválido (todos los dígitos son iguales)")
            else -> ValidationResult(true, "")
        }
    }

    fun validateNotEmpty(value: String, fieldName: String): ValidationResult {
        return when {
            value.isBlank() -> ValidationResult(false, "$fieldName no puede estar vacío")
            else -> ValidationResult(true, "")
        }
    }

    fun validateOriginDestination(origin: String, destination: String): ValidationResult {
        return when {
            origin.isBlank() || destination.isBlank() ->
                ValidationResult(false, "Origen y destino no pueden estar vacíos")
            origin.trim().equals(destination.trim(), ignoreCase = true) ->
                ValidationResult(false, "Origen y destino deben ser diferentes")
            else -> ValidationResult(true, "")
        }
    }
}

data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String
)