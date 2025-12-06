package com.viarapida.app.data.repository

import com.viarapida.app.data.model.Route

interface RouteRepository {
    suspend fun getAllRoutes(): Result<List<Route>>
    suspend fun searchRoutes(origin: String, destination: String): Result<List<Route>>
    suspend fun getRouteById(routeId: String): Result<Route?>
    suspend fun createRoute(route: Route): Result<String>
    suspend fun updateRoute(routeId: String, route: Route): Result<Unit>
    suspend fun updateOccupiedSeats(routeId: String, occupiedSeats: List<Int>): Result<Unit>
    suspend fun createInitialRoutes(): Result<Unit>
}