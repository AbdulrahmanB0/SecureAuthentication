package com.practise.secureauthentication.data

import com.practise.secureauthentication.data.network.ApiErrors

@Suppress("unused")
sealed class RemoteResource<out T> {
    object Idle: RemoteResource<Nothing>()
    object Loading: RemoteResource<Nothing>()
    data class Success<T>(val data: T): RemoteResource<T>()
    data class Failure<T>(val error: ApiErrors, val data: T? = null): RemoteResource<T>()


    fun getOrNull() = (this as? Success)?.data

}
