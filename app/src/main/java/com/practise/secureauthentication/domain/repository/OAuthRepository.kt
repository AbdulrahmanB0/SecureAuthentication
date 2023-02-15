package com.practise.secureauthentication.domain.repository

import com.practise.secureauthentication.util.OneTapSignInResource
import kotlinx.coroutines.flow.Flow


interface OAuthRepository {

    fun signInWithGoogle(): Flow<OneTapSignInResource>

}