package com.viarapida.app.data.repository

import android.util.Log
import com.google.firebase.Timestamp
import com.viarapida.app.data.model.User
import com.viarapida.app.data.remote.FirebaseClient
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl : AuthRepository {

    private val auth = FirebaseClient.auth
    private val firestore = FirebaseClient.firestore

    private companion object {
        const val TAG = "AuthRepository"
        const val USERS_COLLECTION = "users"
    }

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            Log.d(TAG, "Iniciando login para: $email")

            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: return Result.failure(
                Exception("Error al obtener ID de usuario")
            )

            Log.d(TAG, "Login exitoso, obteniendo datos del usuario: $userId")

            val userDoc = firestore.collection(USERS_COLLECTION)
                .document(userId)
                .get()
                .await()

            val user = User.fromFirestore(userDoc) ?: return Result.failure(
                Exception("Usuario no encontrado en base de datos")
            )

            Log.d(TAG, "Usuario obtenido: ${user.name}")
            Result.success(user)

        } catch (e: Exception) {
            Log.e(TAG, "Error en login: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun register(name: String, email: String, password: String): Result<User> {
        return try {
            Log.d(TAG, "Iniciando registro para: $email")

            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: return Result.failure(
                Exception("Error al crear usuario")
            )

            Log.d(TAG, "Usuario creado en Auth, guardando en Firestore: $userId")

            val user = User(
                id = userId,
                name = name,
                email = email,
                isAdmin = false,
                createdAt = Timestamp.now()
            )

            firestore.collection(USERS_COLLECTION)
                .document(userId)
                .set(user.toMap())
                .await()

            Log.d(TAG, "Usuario guardado en Firestore exitosamente")
            Result.success(user)

        } catch (e: Exception) {
            Log.e(TAG, "Error en registro: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun getCurrentUser(): Result<User?> {
        return try {
            val userId = auth.currentUser?.uid

            if (userId == null) {
                Log.d(TAG, "No hay usuario autenticado")
                return Result.success(null)
            }

            Log.d(TAG, "Obteniendo usuario actual: $userId")

            val userDoc = firestore.collection(USERS_COLLECTION)
                .document(userId)
                .get()
                .await()

            val user = User.fromFirestore(userDoc)
            Log.d(TAG, "Usuario actual obtenido: ${user?.name}")

            Result.success(user)

        } catch (e: Exception) {
            Log.e(TAG, "Error obteniendo usuario actual: ${e.message}", e)
            Result.failure(e)
        }
    }

    override fun logout() {
        Log.d(TAG, "Cerrando sesión")
        auth.signOut()
    }

    override fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }
}