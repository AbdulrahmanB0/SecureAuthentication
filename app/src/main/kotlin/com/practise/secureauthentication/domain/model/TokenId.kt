package com.practise.secureauthentication.domain.model

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class TokenId(val value: String)