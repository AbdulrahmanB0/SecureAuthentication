package com.practise.plugins

import com.practise.domain.model.MessagesResource
import com.practise.domain.model.NoSuchSessionException
import domain.model.ApiResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<RequestValidationException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                ApiResponse<Unit>(
                    success = false,
                    message = "Bad Request: ${cause.reasons.joinToString("\n")}"
                )
            )
        }

        exception<NoSuchSessionException> { call, _ ->
            call.respond(
                HttpStatusCode.Unauthorized,
                ApiResponse<Unit>(
                    success = false,
                    message = "${HttpStatusCode.Unauthorized.value}: ${MessagesResource.UNAUTHORIZED.message}"
                )
            )
        }

        exception<Exception> { call, cause ->
            val stacktrace = buildString {
                cause.stackTrace.forEach {
                    appendLine("at $it")
                }
            }
            call.application.log.error("${cause::class.simpleName}: ${cause.message}\n$stacktrace")
            call.respond(
                HttpStatusCode.InternalServerError,
                ApiResponse<Unit>(
                    success = false,
                    message = "500: Unknown Internal Server Error"
                )
            )
        }

        status(HttpStatusCode.InternalServerError) { call, status ->
            call.application.log.error("Unknown Internal Server Error")
            call.respond(
                status,
                ApiResponse<Unit>(
                    success = false,
                    message = "${status.value}: Internal Server Error"
                )
            )
        }

        status(HttpStatusCode.Unauthorized) { call, status ->
            call.respond(
                status,
                ApiResponse<Unit>(
                    success = false,
                    message = "${status.value}: ${MessagesResource.UNAUTHORIZED.message}"
                )
            )
        }
    }
}
