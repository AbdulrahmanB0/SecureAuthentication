package com.practise.domain.model.api

import kotlinx.serialization.Serializable
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime

@Serializable
data class ErrorMessage(
    val code: Int,
    val message: String
)