package com.practise.secureauthentication.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val name: String,
    val emailAddress: EmailAddress,
    @SerialName("photoUrl")
    val profilePhoto: ProfilePhoto
)

@Serializable
@JvmInline
value class EmailAddress(val value: String)

@Serializable
@JvmInline
value class ProfilePhoto(val url: String)

@Serializable
data class UserUpdate(
    val firstName: String,
    val lastName: String
)
