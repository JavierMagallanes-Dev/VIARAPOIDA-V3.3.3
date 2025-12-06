package com.viarapida.app.ui.utils

object Constants {

    // SharedPreferences
    const val PREFS_NAME = "ViaRapidaPrefs"
    const val KEY_FIRST_TIME = "isFirstTime"

    // Navigation Routes
    const val ROUTE_SPLASH = "splash"
    const val ROUTE_LOGIN = "login"
    const val ROUTE_REGISTER = "register"
    const val ROUTE_HOME = "home"
    const val ROUTE_SEARCH = "search"
    const val ROUTE_SEAT_SELECTION = "seat_selection/{routeId}"
    const val ROUTE_PURCHASE = "purchase/{routeId}/{seatNumber}"
    const val ROUTE_TICKET_DETAIL = "ticket_detail/{ticketId}"
    const val ROUTE_MY_TICKETS = "my_tickets"
    const val ROUTE_ADMIN = "admin"
    const val ROUTE_PAYMENT_METHODS = "payment_methods"
    const val ROUTE_ADD_PAYMENT = "add_payment"
    const val ROUTE_TRANSACTION_HISTORY = "transaction_history"

    // Ticket Status
    const val STATUS_ACTIVE = "Activo"
    const val STATUS_USED = "Usado"

    // Seat Configuration
    const val TOTAL_SEATS = 40
    const val SEATS_PER_ROW = 4

    // Cities
    val CITIES = listOf(
        "Ayacucho",
        "Lima",
        "Huancayo",
        "Cusco",
        "Arequipa"
    )

    // Validation
    const val MIN_PASSWORD_LENGTH = 6
    const val DNI_LENGTH = 8

    // Transaction Status
    const val TRANSACTION_STATUS_PENDING = "PENDING"
    const val TRANSACTION_STATUS_PROCESSING = "PROCESSING"
    const val TRANSACTION_STATUS_COMPLETED = "COMPLETED"
    const val TRANSACTION_STATUS_FAILED = "FAILED"
    const val TRANSACTION_STATUS_CANCELLED = "CANCELLED"
    const val TRANSACTION_STATUS_REFUNDED = "REFUNDED"

    // Payment Types
    const val PAYMENT_TYPE_CARD = "CARD"
    const val PAYMENT_TYPE_YAPE = "YAPE"
    const val PAYMENT_TYPE_PLIN = "PLIN"
}