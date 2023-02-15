package com.practise.domain.model

import io.ktor.server.auth.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserSession(
    @SerialName("userId")
    val id: String,
): Principal
