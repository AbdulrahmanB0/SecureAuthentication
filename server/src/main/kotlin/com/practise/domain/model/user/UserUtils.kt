package com.practise.domain.model.user

import kotlinx.serialization.Serializable

@Serializable @JvmInline
value class EmailAddress(val value: String)

@Serializable @JvmInline
value class ProfilePhotoUrl(val value: String)

@Serializable
data class UserUpdate(
    val firstName: String,
    val lastName: String
)

@Serializable
data class UserCredentials(
    val username: String,
    val password: String
)
