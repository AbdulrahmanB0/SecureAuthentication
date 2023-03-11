package com.practise.secureauthentication.data.util

import androidx.datastore.core.Serializer
import com.practise.secureauthentication.data.model.network.Cookies
import com.practise.secureauthentication.data.util.crypto.AndroidCryptoService
import com.practise.secureauthentication.data.util.crypto.CryptoService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object CookieSerializer: Serializer<Cookies> {
    private val crypto: CryptoService = AndroidCryptoService()
    override val defaultValue = Cookies()
    override suspend fun readFrom(input: InputStream): Cookies {
        val bytes = crypto.decrypt(input.readBytes())
        return Json.decodeFromString(bytes.decodeToString())
    }

    override suspend fun writeTo(t: Cookies, output: OutputStream) {

        withContext(Dispatchers.IO) {
            output.write(
                crypto.encrypt(
                    bytes = Json.encodeToString(t).toByteArray(),
                )
            )
        }
    }

}