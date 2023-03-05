package com.practise.domain.model.api

import kotlinx.serialization.Serializable

@Serializable
data class ErrorMessage(
    val code: Int,
    val message: String
)
