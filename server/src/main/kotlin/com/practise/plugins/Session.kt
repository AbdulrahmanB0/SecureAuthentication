package com.practise.plugins

import com.practise.data.sessionstorage.RedisSessionStorage
import com.practise.domain.model.UserSession
import com.practise.core.Constants
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.sessions.*
import org.koin.ktor.ext.inject
import kotlin.time.Duration.Companion.minutes

fun Application.configureSessions() {

    val redisSessionStorage by inject<RedisSessionStorage>()

    install(Sessions) {
        cookie<UserSession>(
            name = Constants.USER_SESSION,
            storage = redisSessionStorage
        ) {
            cookie.secure = false //"true" for production
            cookie.httpOnly = true
            cookie.maxAge = 30.minutes
            cookie.encoding = CookieEncoding.BASE64_ENCODING
        }
    }
}