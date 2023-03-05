package com.practise.domain.model.security.hashing

interface HashingService {

    val saltLengthInBytes: Int
    fun generateSaltedHash(value: String): SaltedHash
    fun verify(value: String, saltedHash: SaltedHash): Boolean
}
