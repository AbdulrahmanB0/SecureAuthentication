package com.practise.secureauthentication.domain.usecases

import com.google.android.gms.auth.api.identity.SignInClient
import com.practise.secureauthentication.data.RemoteResource
import com.practise.secureauthentication.data.network.ApiErrors
import com.practise.secureauthentication.domain.repository.CacheRepository
import com.practise.secureauthentication.domain.repository.KtorApiRepository
import domain.model.TokenId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class VerifyUserTokenUseCase @Inject constructor(
    private val api: KtorApiRepository,
    private val cache: CacheRepository,
    private val oneTapClient: SignInClient

) {

    operator fun invoke(tokenId: TokenId) = flow {
        emit(RemoteResource.Loading)
        api.verifyToken(tokenId)
        cache.saveSignedInState(true)
        emit(RemoteResource.Success(Unit))
    }.catch {
        oneTapClient.signOut().await()
        val error = ApiErrors.identifyError(it)
        emit(RemoteResource.Failure(error))
    }.flowOn(Dispatchers.IO)
}