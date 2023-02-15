package com.practise.domain.model.api

import kotlinx.serialization.Serializable

@Serializable
data class ExperimentalApiResponse<T>(
    val isSuccessful: Boolean,
    val data: Map<String, T>? = null,
    val errorMessage: ErrorMessage? = null

)