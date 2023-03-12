package com.practise.secureauthentication.data.model.user

import androidx.datastore.core.Serializer
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
        return Json.decodeFromString(input.readBytes().decodeToString())
    }

    override suspend fun writeTo(t: User?, output: OutputStream) {
        withContext(Dispatchers.IO) {
            output.write(Json.encodeToString(t).toByteArray())
        }
    }
}