package domain.model

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class TokenId(val value: String)
