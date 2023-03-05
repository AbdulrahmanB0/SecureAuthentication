package com.practise.domain.model.security.hashing

import org.apache.commons.codec.binary.Hex
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

class PBKDF2WithHmacSHA512HashingService(
    private val iterationsCount: Int = 10000,
    private val keyLengthInBits: Int = 256
) : HashingService {

    private val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512")
    override val saltLengthInBytes = keyLengthInBits / 8

    override fun generateSaltedHash(value: String): SaltedHash {
        val salt = SecureRandom().generateSeed(saltLengthInBytes) // Convert to bytes
        val pbeKeySpec = PBEKeySpec(value.toCharArray(), salt, iterationsCount, keyLengthInBits)
        val hashAsHex = factory.generateSecret(pbeKeySpec).encoded.let { Hex.encodeHexString(it) }
        val saltAsHex = Hex.encodeHexString(salt)

        return SaltedHash(hashAsHex, saltAsHex)
    }

    override fun verify(value: String, saltedHash: SaltedHash): Boolean {
        val pbeKeySpec = PBEKeySpec(value.toCharArray(), Hex.decodeHex(saltedHash.salt), iterationsCount, keyLengthInBits)
        val receivedHash = factory.generateSecret(pbeKeySpec).encoded.let { Hex.encodeHexString(it) }
        return receivedHash == saltedHash.hash
    }
}
