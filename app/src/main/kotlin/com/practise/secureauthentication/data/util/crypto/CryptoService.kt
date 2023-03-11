package com.practise.secureauthentication.data.util.crypto

interface CryptoService {

    fun encrypt(bytes: ByteArray): ByteArray

    fun decrypt(bytes: ByteArray): ByteArray
}