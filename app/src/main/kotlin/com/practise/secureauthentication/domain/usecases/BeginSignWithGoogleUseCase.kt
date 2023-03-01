package com.practise.secureauthentication.domain.usecases

import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.auth.api.identity.SignInClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

class BeginSignWithGoogleUseCase @Inject constructor(
    @Named("auto")
    private val signInRequest: BeginSignInRequest,
    @Named("manual")
    private val signUpRequest: BeginSignInRequest,
) {
    suspend operator fun invoke(oneTapClient: SignInClient): Result<BeginSignInResult> =
        withContext(Dispatchers.IO) {

        val illegalStateException =
            IllegalStateException("BeginSignInRequest returned null for unknown reason")

        oneTapClient.runCatching {
            beginSignIn(signInRequest).await() ?: throw illegalStateException
        }.fold(
            onSuccess = { Result.success(it) },
            onFailure = {
                oneTapClient.runCatching {
                    beginSignIn(signUpRequest).await() ?: throw illegalStateException
                }
            }
        )
    }

}