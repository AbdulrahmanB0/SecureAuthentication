package com.practise.secureauthentication.data.network

import android.util.Log
import androidx.datastore.core.DataStore
import com.practise.secureauthentication.data.model.network.Cookies
import io.ktor.client.plugins.cookies.*
import io.ktor.http.*
import io.ktor.util.*
import io.ktor.util.date.*
import kotlinx.coroutines.flow.first

private const val TAG = "PersistentCookieStorage"
class PersistentCookieStorage(
    private val datastore: DataStore<Cookies>,
): CookiesStorage {

    private var oldestCookie = Long.MAX_VALUE
    override suspend fun addCookie(requestUrl: Url, cookie: Cookie) {
        datastore.updateData { cookies ->
            Cookies(
                cookies.toMutableList().apply {
                    removeAll { it.name == cookie.name && it.matchesUrl(requestUrl) }
                    add(cookie)
                }
            )
        }.also {
            Log.i(TAG, "addCookie: successfully added cookie, current size: ${it.size}")
        }
    }

    override fun close() = Unit

    override suspend fun get(requestUrl: Url): List<Cookie> {
        val now = GMTDate().timestamp
        if (now > oldestCookie) {
            cleanup(now)
        }
        return datastore.data
            .first()
            .filter { cookie -> cookie.matchesUrl(requestUrl) }
            .also { Log.i(TAG, "get: successfully retrieved cookies, retrieved ${it.size}") }
    }

    private suspend fun cleanup(timestamp: Long) {
        datastore.updateData { cookie ->
            cookie.copy(list=
                cookie.toMutableList().apply {
                    removeAll { (it.expires?.timestamp ?: Long.MAX_VALUE) > timestamp }
                }
            )
        }.also {
            Log.i(TAG, "cleanup: successfully cleaned up cookies, cleaned up ${it.size}")
        }

        findOldestCookie()
    }

    private suspend fun findOldestCookie() {
        oldestCookie = datastore
            .data
            .first()
            .minBy { it.expires?.timestamp ?: Long.MAX_VALUE }
            .expires?.timestamp
            ?: Long.MAX_VALUE
                .also {
                    Log.i(TAG, "findOldestCookie: successfully found oldest cookie")
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