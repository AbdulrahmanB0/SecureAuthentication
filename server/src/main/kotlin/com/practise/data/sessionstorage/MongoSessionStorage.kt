package com.practise.data.sessionstorage

import com.mongodb.client.model.ReplaceOptions
import com.practise.domain.model.UserSession
import io.ktor.server.sessions.*
import org.litote.kmongo.coroutine.CoroutineDatabase

/**
 * This class provides sessions management using MongoDB
 */
class MongoSessionStorage(
    database: CoroutineDatabase
) : SessionStorage {
    private val sessionsCollection = database.getCollection<UserSession>("sessions")
    override suspend fun invalidate(id: String) {
        sessionsCollection.deleteOneById(id)
    }

    override suspend fun read(id: String): String {
        return sessionsCollection.findOneById(id)?.id
            ?: throw NoSuchElementException("Could not find session ID: $id")
    }

    override suspend fun write(id: String, value: String) {
        sessionsCollection.replaceOneById(
            id = id,
            replacement = UserSession(value),
            options = ReplaceOptions().upsert(true)
        )
    }
}
