package com.viarapida.app.data.repository

import android.util.Log
import com.viarapida.app.data.model.Route
import com.viarapida.app.data.remote.FirebaseClient
import kotlinx.coroutines.tasks.await

class RouteRepositoryImpl : RouteRepository {

    private val firestore = FirebaseClient.firestore

    private companion object {
        const val TAG = "RouteRepository"
        const val ROUTES_COLLECTION = "routes"
    }

    override suspend fun getAllRoutes(): Result<List<Route>> {
        return try {
            Log.d(TAG, "Obteniendo todas las rutas")

            val snapshot = firestore.collection(ROUTES_COLLECTION)
                .get()
                .await()

            val routes = snapshot.documents.mapNotNull { doc ->
                Route.fromFirestore(doc)
            }

            Log.d(TAG, "Rutas obtenidas: ${routes.size}")
            Result.success(routes)

        } catch (e: Exception) {
            Log.e(TAG, "Error obteniendo rutas: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun searchRoutes(origin: String, destination: String): Result<List<Route>> {
        return try {
            Log.d(TAG, "Buscando rutas: $origin -> $destination")

            val snapshot = firestore.collection(ROUTES_COLLECTION)
                .whereEqualTo("origin", origin)
                .whereEqualTo("destination", destination)
                .get()
                .await()

            val routes = snapshot.documents.mapNotNull { doc ->
                Route.fromFirestore(doc)
            }

            Log.d(TAG, "Rutas encontradas: ${routes.size}")
            Result.success(routes)

        } catch (e: Exception) {
            Log.e(TAG, "Error buscando rutas: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun getRouteById(routeId: String): Result<Route?> {
        return try {
            Log.d(TAG, "Obteniendo ruta: $routeId")

            val doc = firestore.collection(ROUTES_COLLECTION)
                .document(routeId)
                .get()
                .await()

            val route = Route.fromFirestore(doc)
            Log.d(TAG, "Ruta obtenida: ${route?.origin} -> ${route?.destination}")

            Result.success(route)

        } catch (e: Exception) {
            Log.e(TAG, "Error obteniendo ruta: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun createRoute(route: Route): Result<String> {
        return try {
            Log.d(TAG, "Creando ruta: ${route.origin} -> ${route.destination}")

            val docRef = firestore.collection(ROUTES_COLLECTION)
                .add(route.toMap())
                .await()

            Log.d(TAG, "Ruta creada con ID: ${docRef.id}")
            Result.success(docRef.id)

        } catch (e: Exception) {
            Log.e(TAG, "Error creando ruta: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun updateRoute(routeId: String, route: Route): Result<Unit> {
        return try {
            Log.d(TAG, "Actualizando ruta: $routeId")

            firestore.collection(ROUTES_COLLECTION)
                .document(routeId)
                .set(route.toMap())
                .await()

            Log.d(TAG, "Ruta actualizada exitosamente")
            Result.success(Unit)

        } catch (e: Exception) {
            Log.e(TAG, "Error actualizando ruta: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun updateOccupiedSeats(routeId: String, occupiedSeats: List<Int>): Result<Unit> {
        return try {
            Log.d(TAG, "Actualizando asientos ocupados para ruta: $routeId")

            firestore.collection(ROUTES_COLLECTION)
                .document(routeId)
                .update("occupiedSeats", occupiedSeats)
                .await()

            Log.d(TAG, "Asientos actualizados: ${occupiedSeats.size} ocupados")
            Result.success(Unit)

        } catch (e: Exception) {
            Log.e(TAG, "Error actualizando asientos: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun createInitialRoutes(): Result<Unit> {
        return try {
            Log.d(TAG, "Creando rutas para la próxima semana")

            val initialRoutes = listOf(
                // RUTA 1: Ayacucho → Lima (Lunes 08:00 AM)
                Route(
                    origin = "Ayacucho",
                    destination = "Lima",
                    departureTime = "Lunes 08:00 AM",
                    price = 50.0,
                    totalSeats = 40,
                    occupiedSeats = emptyList()
                ),

                // RUTA 2: Ayacucho → Lima (Miércoles 10:00 PM)
                Route(
                    origin = "Ayacucho",
                    destination = "Lima",
                    departureTime = "Miércoles 10:00 PM",
                    price = 45.0,
                    totalSeats = 40,
                    occupiedSeats = emptyList()
                ),

                // RUTA 3: Lima → Ayacucho (Martes 09:00 PM)
                Route(
                    origin = "Lima",
                    destination = "Ayacucho",
                    departureTime = "Martes 09:00 PM",
                    price = 50.0,
                    totalSeats = 40,
                    occupiedSeats = emptyList()
                ),

                // RUTA 4: Lima → Ayacucho (Jueves 09:00 PM)
                Route(
                    origin = "Lima",
                    destination = "Ayacucho",
                    departureTime = "Jueves 09:00 PM",
                    price = 50.0,
                    totalSeats = 40,
                    occupiedSeats = emptyList()
                ),

                // RUTA 5: Ayacucho → Huancayo (Miércoles 06:00 AM)
                Route(
                    origin = "Ayacucho",
                    destination = "Huancayo",
                    departureTime = "Miércoles 06:00 AM",
                    price = 30.0,
                    totalSeats = 40,
                    occupiedSeats = emptyList()
                ),

                // RUTA 6: Huancayo → Ayacucho (Viernes 07:00 AM)
                Route(
                    origin = "Huancayo",
                    destination = "Ayacucho",
                    departureTime = "Viernes 07:00 AM",
                    price = 30.0,
                    totalSeats = 40,
                    occupiedSeats = emptyList()
                )
            )

            initialRoutes.forEach { route ->
                firestore.collection(ROUTES_COLLECTION)
                    .add(route.toMap())
                    .await()
            }

            Log.d(TAG, "✅ ${initialRoutes.size} rutas creadas exitosamente")
            Result.success(Unit)

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error creando rutas: ${e.message}", e)
            Result.failure(e)
        }
    }
}
