package com.practise.secureauthentication.domain.model

sealed class Resource<out T> {
    object Idle: Resource<Nothing>()
    object Loading: Resource<Nothing>()
    data class Success<T>(val data: T): Resource<T>()
    data class Failure<T>(val throwable: Throwable): Resource<T>()
}
