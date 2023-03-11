package com.practise.secureauthentication.presentation.model

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.flow

@Suppress("unused")
sealed class UiResource<out T> {
    object Idle: UiResource<Nothing>()
    object Loading: UiResource<Nothing>()
    data class Success<T>(val data: T): UiResource<T>()
    data class Failure<T>(val throwable: Throwable): UiResource<T>()

    @Composable
    fun Fold(
        onIdle: @Composable () -> Unit = { },
        onLoading: @Composable () -> Unit,
        onSuccess: @Composable (data: T) -> Unit,
        onFailure: @Composable (Throwable) -> Unit
    ) {
        when(this) {
            is Idle -> onIdle()
            is Loading -> onLoading()
            is Success -> onSuccess(data)
            is Failure -> onFailure(throwable)
        }
    }

    fun getOrNull() = (this as? Success)?.data

    companion object {
        suspend fun <T> from(produceResult: suspend () -> Result<T>) = flow {
            emit(Loading)
            produceResult().fold(
                onSuccess = { Success(it) },
                onFailure = { Failure(it) }
            ).let {
                emit(it)
            }
        }

        infix fun <T> from(result: Result<T>) = result.fold(
            onSuccess = { Success(it) },
            onFailure = { Failure(it) }
        )
    }
}