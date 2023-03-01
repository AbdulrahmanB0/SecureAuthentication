package com.practise.secureauthentication.data

@Suppress("unused")
sealed class Resource<out T> {
    object Idle: Resource<Nothing>()
    object Loading: Resource<Nothing>()
    data class Success<T>(val data: T): Resource<T>()
    data class Failure<T>(val throwable: Throwable): Resource<T>()
}
