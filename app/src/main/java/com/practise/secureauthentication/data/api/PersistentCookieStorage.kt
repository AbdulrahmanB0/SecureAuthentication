package com.practise.secureauthentication.data.api

import io.ktor.client.plugins.cookies.*
import io.ktor.http.*
import java.time.Instant
import javax.inject.Inject

@Suppress("UNUSED")
class PersistentCookieStorage @Inject constructor(

): CookiesStorage {
    override suspend fun addCookie(requestUrl: Url, cookie: Cookie) {
        TODO("Not yet implemented")

    }

    override fun close() {
        TODO("Not yet implemented")
    }

    override suspend fun get(requestUrl: Url): List<Cookie> {
        TODO("Not yet implemented")
    }
}