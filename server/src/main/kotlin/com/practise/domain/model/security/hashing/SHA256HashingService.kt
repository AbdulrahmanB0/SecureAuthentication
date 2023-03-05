package com.practise.domain.model.security.hashing

import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils
import java.security.SecureRandom

class SHA256HashingService : HashingService {
    override val saltLengthInBytes: Int = 32
    override fun generateSaltedHash(value: String): SaltedHash {
        val salt = SecureRandom().generateSeed(saltLengthInBytes).let { Hex.encodeHexString(it) }
        val hash = DigestUtils.sha256Hex("$salt$value")
        return SaltedHash(hash, salt)
    }

    override fun verify(value: String, saltedHash: SaltedHash): Boolean {
        return DigestUtils
            .sha256Hex(saltedHash.salt + value) == saltedHash.hash
    }
}
