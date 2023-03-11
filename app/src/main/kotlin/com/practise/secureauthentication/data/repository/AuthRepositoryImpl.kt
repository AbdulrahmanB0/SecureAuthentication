package com.practise.secureauthentication.data.repository

import com.practise.secureauthentication.data.model.ResourceError
import com.practise.secureauthentication.data.network.EndPoint
import com.practise.secureauthentication.data.util.suspendRunCatching
import com.practise.secureauthentication.domain.repository.AuthRepository
import domain.model.TokenId
import io.ktor.client.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.request.*
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val client: HttpClient,
): AuthRepository {
    override suspend fun signInWithGoogle(tokenId: TokenId) = suspendRunCatching {
        client.post(EndPoint.OAuth.Google()) {
            setBody(tokenId)
        }
    }
        .recoverCatching { throw ResourceError.identifyError(it) }
        .mapCatching { }

}
