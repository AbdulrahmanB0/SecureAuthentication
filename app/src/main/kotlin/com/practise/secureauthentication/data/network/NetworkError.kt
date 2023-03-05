package com.practise.secureauthentication.data.network

import io.ktor.client.plugins.*
import io.ktor.http.*
import io.ktor.util.network.*

/*
sealed class NetworkError(
    override val e: Throwable,
): ResourceError {
    class UnexpectedRedirectError(e: RedirectResponseException) : NetworkError(e)
    class ClientError(e: ClientRequestException)                : NetworkError(e)
    class ServerError(e: ServerResponseException)               : NetworkError(e)
    class InternetError(e: UnresolvedAddressException)          : NetworkError(e)
    class RequestTimeoutError(e: HttpRequestTimeoutException)   : NetworkError(e)
    class SocketTimeoutError(e: SocketTimeoutException)         : NetworkError(e)
    class UnknownError(e: Throwable)                            : NetworkError(e)

    companion object {
        fun identifyError(e: Throwable): NetworkError {
            return when(e) {
                is ClientRequestException -> ClientError(e)
                is ServerResponseException -> ServerError(e)
                is UnresolvedAddressException -> InternetError(e)
                is HttpRequestTimeoutException -> RequestTimeoutError(e)
                is SocketTimeoutException -> SocketTimeoutError(e)
                is RedirectResponseException -> UnexpectedRedirectError(e)
                else -> UnknownError(e)
            }
        }
    }

}

 */

enum class ApiErrors {
    UNAUTHORIZED,
    NOT_FOUND,
    INVALID_VALUE,
    CONFLICT,
    INTERNAL,
    NO_INTERNET,
    UNKNOWN;

    companion object {
        fun identifyError(e: Throwable): ApiErrors {
            return when(e) {
                is ClientRequestException -> when(e.response.status) {
                    HttpStatusCode.Unauthorized -> UNAUTHORIZED
                    HttpStatusCode.NotFound -> NOT_FOUND
                    HttpStatusCode.Conflict -> CONFLICT
                    HttpStatusCode.BadRequest -> INVALID_VALUE
                    else -> UNKNOWN
                }
                is ServerResponseException -> INTERNAL
                is UnresolvedAddressException -> NO_INTERNET
                else -> UNKNOWN
            }
        }
    }
}