package com.practise.secureauthentication.data.model

import io.ktor.client.plugins.*
import io.ktor.http.*
import io.ktor.util.network.*

object DataError {
    fun identifyError(e: Throwable): Exception {
        return when(e) {
            is ClientRequestException -> when(e.response.status) {
                HttpStatusCode.Unauthorized -> UserUnauthorizedException()
                HttpStatusCode.NotFound -> ResourceNotFoundException()
                HttpStatusCode.BadRequest -> InvalidValuesException()
                else -> e as? Exception ?: Exception()
            }
            is ServerResponseException -> e
            is UnresolvedAddressException -> e
            else -> e as? Exception ?: Exception()
        }
    }

}