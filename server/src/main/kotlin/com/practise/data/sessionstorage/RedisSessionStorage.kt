package com.practise.data.sessionstorage

import com.practise.domain.model.NoSuchSessionException
import io.ktor.server.sessions.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import redis.clients.jedis.JedisPooled as RedisClient

/**
 * This class provides sessions management using Redis in-memory database
 */
class RedisSessionStorage(
    private val client: RedisClient
) : SessionStorage {

    override suspend fun invalidate(id: String) {
        withContext(Dispatchers.IO) {
            client.del(id)
        }
    }

    override suspend fun read(id: String): String {
        return withContext(Dispatchers.IO) {
            client.get(id) ?: throw NoSuchSessionException("Session $id not found")
        }
    }

    override suspend fun write(id: String, value: String) {
        withContext(Dispatchers.IO) {
            client.set(id, value)
        }
    }
}
