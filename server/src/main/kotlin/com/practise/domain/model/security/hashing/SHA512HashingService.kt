package com.practise.domain.model.security.hashing

import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils
import java.security.SecureRandom

class SHA512HashingService : HashingService {
    override val saltLengthInBytes: Int = 64

    override fun generateSaltedHash(value: String): SaltedHash {
        val salt = SecureRandom().generateSeed(saltLengthInBytes).let { Hex.encodeHexString(it) }
        val hash = DigestUtils.sha512Hex("$salt$value")
        return SaltedHash(hash, salt)
    }

    override fun verify(value: String, saltedHash: SaltedHash): Boolean {
        return DigestUtils
            .sha512Hex(saltedHash.salt + value) == saltedHash.hash
    }
}
