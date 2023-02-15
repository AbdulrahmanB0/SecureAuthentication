package com.practise.routes

import com.practise.domain.model.api.EndPoint
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.welcomeRoute() {
    get<EndPoint> {
        call.respondText("Welcome to Secure Authentication Ktor Server!")
    }
}