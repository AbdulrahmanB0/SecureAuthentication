package com.practise.secureauthentication.data.util

import io.ktor.http.*
import io.ktor.util.date.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json

object CookieKSerializer: KSerializer<Cookie> {

    override val descriptor = PrimitiveSerialDescriptor("Cookie", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder): Cookie {
        val map = Json.decodeFromString<Map<String, String?>>(decoder.decodeString())
        return Cookie(
            name = map["name"]!!,
            value = map["value"]!!,
            domain = map["domain"],
            path = map["path"],
            maxAge = map["maxAge"]!!.toInt(),
            expires = GMTDate(map["expires"]!!.toLong()),
            secure = map["secure"]!!.toBoolean(),
            httpOnly = map["httpOnly"]!!.toBoolean(),
            encoding = map["encoding"]!!.let { CookieEncoding.valueOf(it) },
            extensions = map["extensions"]!!.let { Json.decodeFromString(it) }
        )
    }


    override fun serialize(encoder: Encoder, value: Cookie) {
        with(value) {
            encoder.encodeString(
                Json.encodeToString(
                    mapOf(
                        "name" to name,
                        "value" to this.value,
                        "domain" to domain,
                        "path" to path,
                        "maxAge" to maxAge.toString(),
                        "expires" to expires?.timestamp?.toString(),
                        "secure" to secure.toString(),
                        "httpOnly" to httpOnly.toString(),
                        "encoding" to encoding.name,
                        "extensions" to Json.encodeToString(extensions)
                    )
                )
            )
        }
    }
}