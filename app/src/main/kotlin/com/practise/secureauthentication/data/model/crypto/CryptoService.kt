package com.practise.secureauthentication.data.model.crypto

interface CryptoService {

    fun encrypt(bytes: ByteArray): ByteArray

    fun decrypt(bytes: ByteArray): ByteArray
}