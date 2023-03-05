package com.practise.data.sessionstorage

import io.ktor.server.sessions.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.minutes
import redis.clients.jedis.JedisPooled as RedisClient

/**
 * This class provides sessions management using Redis in-memory database
 */
class RedisSessionStorage(
    private val client: RedisClient
) : SessionStorage {

    private fun extendExpire(id: String) =
        client.expire(id, 30.minutes.inWholeSeconds)

    override suspend fun invalidate(id: String) {
        withContext(Dispatchers.IO) {
            client.del(id)
        }
    }

    override suspend fun read(id: String): String {
        return withContext(Dispatchers.IO) {
            client.get(id)?.also { extendExpire(id) } ?: "" // Empty string will cause the authentication to fail
        }
    }

    override suspend fun write(id: String, value: String) {
        withContext(Dispatchers.IO) {
            client.set(id, value)
            extendExpire(id)
        }
    }
}
