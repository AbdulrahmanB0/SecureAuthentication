package com.practise.secureauthentication.domain.usecases

import android.util.Log
import com.google.android.gms.auth.api.identity.SignInClient
import com.practise.secureauthentication.data.RemoteResource
import com.practise.secureauthentication.data.network.ApiErrors
import com.practise.secureauthentication.domain.repository.CacheRepository
import com.practise.secureauthentication.domain.repository.KtorApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val api: KtorApiRepository,
    private val cache: CacheRepository,
    private val oneTapClient: SignInClient
) {

    operator fun invoke() = flow {
        emit(RemoteResource.Loading)
        api.signOut()
        oneTapClient.signOut().await()
        cache.updateUserInfo { null }
        cache.saveSignedInState(false)
        emit((RemoteResource.Success(Unit)))
    }.catch {
        val error = ApiErrors.identifyError(it)
        emit(RemoteResource.Failure(error))
    }.flowOn(Dispatchers.IO)
}