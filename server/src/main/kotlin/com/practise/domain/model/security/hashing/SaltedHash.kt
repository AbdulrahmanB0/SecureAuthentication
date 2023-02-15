package com.practise.domain.model.security.hashing

data class SaltedHash(
    val hash: String,
    val salt: String
)
