package com.practise.secureauthentication.data.datasource.cookies

import androidx.datastore.core.DataStore
import com.practise.secureauthentication.data.model.network.Cookies
import io.ktor.client.plugins.cookies.*
import io.ktor.http.*
import io.ktor.util.*
import io.ktor.util.date.*
import kotlinx.coroutines.flow.first

class CookiesLocalDataSource (
    private val datastore: DataStore<Cookies>,
): CookiesStorage {

    override suspend fun addCookie(requestUrl: Url, cookie: Cookie) {
        datastore.updateData { cookies ->

            cookies.toMutableList().apply {
                removeAll { it.name == cookie.name && it.matchesUrl(requestUrl) }
                add(cookie)
            }.let { Cookies(it) }

        }
    }

    override fun close() = Unit

    override suspend fun get(requestUrl: Url): List<Cookie> {
        val now = GMTDate().timestamp
        cleanupExpired(now)

        return datastore.data.first().filter { cookie -> cookie.matchesUrl(requestUrl) }
    }

    private suspend fun cleanupExpired(upperTimestamp: Long) {

        datastore.updateData { cookies ->
            cookies.filterNot {
                val expires = it.expires?.timestamp ?: return@filterNot true
                expires < upperTimestamp
            }.let { Cookies(it) }
        }

    }
}

private fun Cookie.matchesUrl(requestUrl: Url): Boolean {
    val domain = domain?.toLowerCasePreservingASCIIRules()?.trimStart('.')
        ?: error("Domain field should have the default value")

    val path = with(path) {
        val current = path ?: error("Path field should have the default value")
        if (current.endsWith('/')) current else "$path/"
    }

    val host = requestUrl.host.toLowerCasePreservingASCIIRules()
    val requestPath = let {
        val pathInRequest = requestUrl.encodedPath
        if (pathInRequest.endsWith('/')) pathInRequest else "$pathInRequest/"
    }

    if (host != domain && (hostIsIp(host) || !host.endsWith(".$domain"))) {
        return false
    }

    if (path != "/" &&
        requestPath != path &&
        !requestPath.startsWith(path)
    ) return false

    return !(secure && !requestUrl.protocol.isSecure())
}