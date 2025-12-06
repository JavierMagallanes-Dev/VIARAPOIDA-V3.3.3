package com.viarapida.app.data.repository

import com.viarapida.app.data.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(name: String, email: String, password: String): Result<User>
    suspend fun getCurrentUser(): Result<User?>
    fun logout()
    fun isUserLoggedIn(): Boolean
}
