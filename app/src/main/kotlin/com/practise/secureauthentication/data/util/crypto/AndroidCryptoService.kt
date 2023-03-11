package com.practise.secureauthentication.data.util.crypto

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

@Suppress("unused")
private const val TAG = "AndroidCryptoService"
class AndroidCryptoService(
    private val algorithm: String = KeyProperties.KEY_ALGORITHM_AES,
    private val blockMode: String = KeyProperties.BLOCK_MODE_CBC,
    private val padding: String = KeyProperties.ENCRYPTION_PADDING_PKCS7,
) : CryptoService {

    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }

    private val cipher = Cipher.getInstance("$algorithm/$blockMode/$padding")

    override fun encrypt(bytes: ByteArray): ByteArray {
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKeyOrCreate())
        val encryptedBytes = cipher.doFinal(bytes)
        return (encryptedBytes + cipher.iv + cipher.iv.size.toByte())
    }

    override fun decrypt(bytes: ByteArray): ByteArray {

        val ivSize = bytes.last()
        val iv = bytes.takeLast(ivSize + 1).dropLast(1).toByteArray()
        cipher.init(Cipher.DECRYPT_MODE, getSecretKeyOrCreate(), IvParameterSpec(iv))
        val encryptedBytes = bytes.dropLast(ivSize + 1).toByteArray()
        return cipher.doFinal(encryptedBytes)
    }

    private fun getSecretKeyOrCreate(): SecretKey {
        val existingKey = keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
        return existingKey?.secretKey ?: generateKey()
    }

    private fun generateKey(): SecretKey {
        return KeyGenerator.getInstance(algorithm).apply {
            init(
                KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
                )
                .setBlockModes(blockMode)
                .setEncryptionPaddings(padding)
                .setKeySize(256)
                .setUserAuthenticationRequired(false)
                .setRandomizedEncryptionRequired(true)
                .build()
            )
        }.generateKey()
    }

    companion object {
        private const val KEY_ALIAS = "secure_authentication_key"
    }
}