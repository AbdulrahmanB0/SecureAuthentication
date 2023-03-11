package com.practise.secureauthentication.data.util

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

suspend fun <R> suspendRunCatching(context: CoroutineContext = Dispatchers.IO, block: suspend () -> R): Result<R> =
    withContext(context) {
        try {
            Result.success(block())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

