package com.practise.secureauthentication.data.core

import androidx.datastore.core.Serializer
import com.practise.secureauthentication.domain.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object UserSerializer: Serializer<User?> {
    override val defaultValue: User? = null

    override suspend fun readFrom(input: InputStream): User? {
        return Json.decodeFromString(input.bufferedReader().readText())
    }

    override suspend fun writeTo(t: User?, output: OutputStream) {
        withContext(Dispatchers.IO) {
            output.write(Json.encodeToString(t).toByteArray())
        }
    }
}