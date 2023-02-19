package com.practise.secureauthentication.data.repository

import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.practise.secureauthentication.domain.model.Resource
import com.practise.secureauthentication.domain.repository.OAuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Named

class OAuthRepositoryImpl @Inject constructor(
    private val oneTapClient: SignInClient,
    @Named("auto")
    private val signInRequest: BeginSignInRequest,
    @Named("manual")
    private val signUpRequest: BeginSignInRequest,
): OAuthRepository {


    override fun signInWithGoogle() = flow {
        emit(Resource.Loading)
        val result = oneTapClient.beginSignIn(signInRequest).await()
        emit(Resource.Success(result))

    }.catch {
        val result = oneTapClient.beginSignIn(signUpRequest).await()
        emit(Resource.Success(result))

    }.catch {
        emit(Resource.Failure(it))
    }.flowOn(Dispatchers.IO)

}