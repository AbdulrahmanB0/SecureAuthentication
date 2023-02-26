package com.practise.plugins

import com.practise.data.sessionstorage.RedisSessionStorage
import com.practise.domain.model.UserSession
import com.practise.core.Constants
import com.practise.domain.model.api.EndPoint
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.sessions.*
import org.koin.ktor.ext.inject
import kotlin.time.Duration.Companion.minutes

fun Application.configureSessions() {

    val redisSessionStorage by inject<RedisSessionStorage>()

    install(Sessions) {
        cookie<UserSession>(
            name = Constants.USER_SESSION,
            storage = redisSessionStorage,
        ) {
            with(cookie) {
                path = this@configureSessions.href(EndPoint.User())
                secure = true
                httpOnly = true
                maxAge = 30.minutes
                encoding = CookieEncoding.BASE64_ENCODING
            }
        }

        /*
        header<UserSession>(
            name = Constants.USER_SESSION,
            storage = redisSessionStorage,
        )
         */
    }
}