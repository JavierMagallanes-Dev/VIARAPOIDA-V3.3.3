package com.viarapida.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.viarapida.app.ui.screens.admin.AdminScreen
import com.viarapida.app.ui.screens.auth.LoginScreen
import com.viarapida.app.ui.screens.auth.RegisterScreen
import com.viarapida.app.ui.screens.home.HomeScreen
import com.viarapida.app.ui.screens.mytickets.MyTicketsScreen
import com.viarapida.app.ui.screens.payment.AddPaymentMethodScreen
import com.viarapida.app.ui.screens.payment.PaymentMethodsScreen
import com.viarapida.app.ui.screens.payment.TransactionHistoryScreen
import com.viarapida.app.ui.screens.purchase.PurchaseScreen
import com.viarapida.app.ui.screens.search.SearchScreen
import com.viarapida.app.ui.screens.seats.SeatSelectionScreen
import com.viarapida.app.ui.screens.splash.SplashScreen
import com.viarapida.app.ui.screens.ticket.TicketDetailScreen

@Composable
fun ViaRapidaNavigation(
    navController: NavHostController,
    startDestination: String = NavGraph.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(NavGraph.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(NavGraph.Login.route) {
                        popUpTo(NavGraph.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(NavGraph.Home.route) {
                        popUpTo(NavGraph.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(NavGraph.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(NavGraph.Register.route)
                },
                onNavigateToHome = {
                    navController.navigate(NavGraph.Home.route) {
                        popUpTo(NavGraph.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(NavGraph.Register.route) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onNavigateToHome = {
                    navController.navigate(NavGraph.Home.route) {
                        popUpTo(NavGraph.Register.route) { inclusive = true }
                    }
                }
            )
        }

        composable(NavGraph.Home.route) {
            HomeScreen(
                onNavigateToSearch = {
                    navController.navigate(NavGraph.Search.route)
                },
                onNavigateToMyTickets = {
                    navController.navigate(NavGraph.MyTickets.route)
                },
                onNavigateToAdmin = {
                    navController.navigate(NavGraph.Admin.route)
                },
                onNavigateToLogin = {
                    navController.navigate(NavGraph.Login.route) {
                        popUpTo(NavGraph.Home.route) { inclusive = true }
                    }
                },
                navController = navController // ⬅️ NUEVO: Para navegación interna
            )
        }

        composable(NavGraph.Search.route) {
            SearchScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToSeatSelection = { routeId ->
                    navController.navigate(NavGraph.SeatSelection.createRoute(routeId))
                }
            )
        }

        composable(
            route = NavGraph.SeatSelection.route,
            arguments = listOf(
                navArgument("routeId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val routeId = backStackEntry.arguments?.getString("routeId") ?: ""
            SeatSelectionScreen(
                routeId = routeId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToPurchase = { selectedSeat ->
                    navController.navigate(
                        NavGraph.Purchase.createRoute(routeId, selectedSeat)
                    )
                }
            )
        }

        composable(
            route = NavGraph.Purchase.route,
            arguments = listOf(
                navArgument("routeId") { type = NavType.StringType },
                navArgument("seatNumber") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val routeId = backStackEntry.arguments?.getString("routeId") ?: ""
            val seatNumber = backStackEntry.arguments?.getInt("seatNumber") ?: 0
            PurchaseScreen(
                routeId = routeId,
                seatNumber = seatNumber,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToTicketDetail = { ticketId ->
                    navController.navigate(NavGraph.TicketDetail.createRoute(ticketId)) {
                        popUpTo(NavGraph.Home.route)
                    }
                },
                onNavigateToAddPayment = { // ⬅️ NUEVO: Para agregar métodos de pago
                    navController.navigate(NavGraph.AddPayment.route)
                }
            )
        }

        composable(
            route = NavGraph.TicketDetail.route,
            arguments = listOf(
                navArgument("ticketId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val ticketId = backStackEntry.arguments?.getString("ticketId") ?: ""
            TicketDetailScreen(
                ticketId = ticketId,
                onNavigateToHome = {
                    navController.navigate(NavGraph.Home.route) {
                        popUpTo(NavGraph.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(NavGraph.MyTickets.route) {
            MyTicketsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToTicketDetail = { ticketId ->
                    navController.navigate(NavGraph.TicketDetail.createRoute(ticketId))
                }
            )
        }

        composable(NavGraph.Admin.route) {
            AdminScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // ============ NUEVAS RUTAS DE PAGO ============

        composable(NavGraph.PaymentMethods.route) {
            PaymentMethodsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToAddPayment = {
                    navController.navigate(NavGraph.AddPayment.route)
                }
            )
        }

        composable(NavGraph.AddPayment.route) {
            AddPaymentMethodScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(NavGraph.TransactionHistory.route) {
            TransactionHistoryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}