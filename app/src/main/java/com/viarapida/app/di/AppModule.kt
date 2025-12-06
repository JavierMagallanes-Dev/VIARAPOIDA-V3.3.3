package com.viarapida.app.di

import com.viarapida.app.data.repository.AuthRepository
import com.viarapida.app.data.repository.AuthRepositoryImpl
import com.viarapida.app.data.repository.PaymentRepository
import com.viarapida.app.data.repository.PaymentRepositoryImpl
import com.viarapida.app.data.repository.RouteRepository
import com.viarapida.app.data.repository.RouteRepositoryImpl
import com.viarapida.app.data.repository.TicketRepository
import com.viarapida.app.data.repository.TicketRepositoryImpl

object AppModule {

    private var authRepository: AuthRepository? = null
    private var routeRepository: RouteRepository? = null
    private var ticketRepository: TicketRepository? = null
    private var paymentRepository: PaymentRepository? = null // ⬅️ NUEVO

    fun provideAuthRepository(): AuthRepository {
        if (authRepository == null) {
            authRepository = AuthRepositoryImpl()
        }
        return authRepository!!
    }

    fun provideRouteRepository(): RouteRepository {
        if (routeRepository == null) {
            routeRepository = RouteRepositoryImpl()
        }
        return routeRepository!!
    }

    fun provideTicketRepository(): TicketRepository {
        if (ticketRepository == null) {
            ticketRepository = TicketRepositoryImpl()
        }
        return ticketRepository!!
    }

    // ⬅️ NUEVA FUNCIÓN
    fun providePaymentRepository(): PaymentRepository {
        if (paymentRepository == null) {
            paymentRepository = PaymentRepositoryImpl()
        }
        return paymentRepository!!
    }
}