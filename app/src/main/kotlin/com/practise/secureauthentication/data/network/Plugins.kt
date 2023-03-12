package com.practise.secureauthentication.data.network

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.resources.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import java.util.*

fun HttpClientConfig<*>.configureDefaultRequest() {
    defaultRequest {
        url("https://secureauth.abdulrahman.codes")
        contentType(ContentType.Application.Json)
        headers.append(HttpHeaders.AcceptLanguage, Locale.getDefault().language)
    }
}

fun HttpClientConfig<*>.configureSerialization() {
    install(ContentNegotiation) {
        json(json = Json {
            ignoreUnknownKeys = true
        })
    }
}

fun HttpClientConfig<*>.configureLogging() {
    install(Logging) {
        logger = Logger.ANDROID
        level = LogLevel.ALL
    }
}

fun HttpClientConfig<*>.configureCookies(
    storage: CookiesStorage = AcceptAllCookiesStorage()
) = install(HttpCookies) {
    this.storage = storage
}

fun HttpClientConfig<*>.configureTypeSafeRequests() = install(Resources)

fun HttpClientConfig<*>.configureRetryPolicy() = install(HttpRequestRetry) {
    retryOnServerErrors(maxRetries = 5)
    exponentialDelay()
}