package com.viarapida.app

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp

class ViaRapidaApp : Application() {

    companion object {
        private const val TAG = "ViaRapidaApp"
    }

    override fun onCreate() {
        super.onCreate()

        // Inicializar Firebase
        initializeFirebase()

        Log.d(TAG, "ViaRapida Application iniciada correctamente")
    }

    private fun initializeFirebase() {
        try {
            // Firebase se inicializa automáticamente con google-services.json
            // Pero podemos verificar que esté inicializado
            FirebaseApp.initializeApp(this)

            Log.d(TAG, "✅ Firebase inicializado correctamente")
            Log.d(TAG, "📦 Firebase App Name: ${FirebaseApp.getInstance().name}")

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error inicializando Firebase: ${e.message}", e)
        }
    }
}