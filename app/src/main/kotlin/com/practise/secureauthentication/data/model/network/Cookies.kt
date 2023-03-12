package com.practise.secureauthentication.data.model.network

import com.practise.secureauthentication.data.util.CookieKSerializer
import io.ktor.http.*
import io.ktor.util.date.*
import kotlinx.serialization.*

@Serializable
data class Cookies(
    private val list: List<@Serializable(CookieKSerializer::class) Cookie> = listOf(),
): List<Cookie> by list