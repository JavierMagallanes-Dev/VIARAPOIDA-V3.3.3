package com.viarapida.app.ui.navigation

sealed class NavGraph(val route: String) {
    object Splash : NavGraph("splash")
    object Login : NavGraph("login")
    object Register : NavGraph("register")
    object Home : NavGraph("home")
    object Search : NavGraph("search")
    object SeatSelection : NavGraph("seat_selection/{routeId}") {
        fun createRoute(routeId: String) = "seat_selection/$routeId"
    }
    object Purchase : NavGraph("purchase/{routeId}/{seatNumber}") {
        fun createRoute(routeId: String, seatNumber: Int) = "purchase/$routeId/$seatNumber"
    }
    object TicketDetail : NavGraph("ticket_detail/{ticketId}") {
        fun createRoute(ticketId: String) = "ticket_detail/$ticketId"
    }
    object MyTickets : NavGraph("my_tickets")
    object Admin : NavGraph("admin")

    // ============ NUEVAS RUTAS DE PAGO ============
    object PaymentMethods : NavGraph("payment_methods")
    object AddPayment : NavGraph("add_payment")
    object TransactionHistory : NavGraph("transaction_history")
}