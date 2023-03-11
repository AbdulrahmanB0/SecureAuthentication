package com.practise.secureauthentication.domain.usecases

import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.auth.api.identity.SignInClient
import com.practise.secureauthentication.core.di.OneTapSignIn
import com.practise.secureauthentication.core.di.OneTapSignUp
import com.practise.secureauthentication.data.util.suspendRunCatching
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject


class BeginSignWithGoogleUseCase @Inject constructor(
    @OneTapSignIn private val signInRequest: BeginSignInRequest,
    @OneTapSignUp private val signUpRequest: BeginSignInRequest,
    private val oneTapClient: SignInClient
) {
    suspend operator fun invoke(): Result<BeginSignInResult> =
        withContext(Dispatchers.IO) {

        val illegalStateException =
            IllegalStateException("BeginSignInRequest returned null for unknown reason")

        suspendRunCatching {
            oneTapClient.beginSignIn(signInRequest).await() ?: throw illegalStateException
        }.fold(
            onSuccess = { Result.success(it) },
            onFailure = {
                suspendRunCatching {
                    oneTapClient.beginSignIn(signUpRequest).await() ?: throw illegalStateException
                }
            }
        )
    }

}