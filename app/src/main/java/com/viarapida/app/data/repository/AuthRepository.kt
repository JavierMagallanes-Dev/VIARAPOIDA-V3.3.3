// app/src/main/java/com/viarapida/app/data/repository/AuthRepository.kt

package com.viarapida.app.data.repository

import com.google.firebase.auth.AuthCredential
import com.viarapida.app.data.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(name: String, email: String, password: String): Result<User>

    // ⬇️ AGREGAR ESTA FUNCIÓN
    suspend fun loginWithGoogle(credential: AuthCredential): Result<User>

    suspend fun getCurrentUser(): Result<User?>
    fun logout()
    fun isUserLoggedIn(): Boolean
}