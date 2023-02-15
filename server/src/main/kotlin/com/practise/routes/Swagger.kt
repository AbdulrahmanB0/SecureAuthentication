package com.practise.routes

import com.practise.domain.model.api.EndPoint
import io.ktor.server.plugins.swagger.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*

fun Route.swaggerRoute() {
    swaggerUI(
        path = application.href(EndPoint.Swagger()),
        swaggerFile = "openapi/documentation.yaml",
    )
}