package com.practise.plugins

import com.practise.routes.authRoutes
import com.practise.routes.swaggerRoute
import com.practise.routes.userRoutes
import io.ktor.server.application.*
import io.ktor.server.plugins.httpsredirect.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*

fun Application.configureRouting() {

    install(HttpsRedirect)
    install(Resources)
    routing {
        authRoutes()
        userRoutes()
        swaggerRoute()
    }
}
