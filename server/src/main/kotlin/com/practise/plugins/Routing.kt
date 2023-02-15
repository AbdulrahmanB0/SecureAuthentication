package com.practise.plugins

import com.practise.routes.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*

fun Application.configureRouting() {

    install(Resources)
    routing {
        welcomeRoute()
        authRoutes()
        userRoutes()
        swaggerRoute()
    }
}
