package com.practise.secureauthentication.data.repository

import com.practise.secureauthentication.data.model.DataError
import com.practise.secureauthentication.data.model.network.EndPoint
import com.practise.secureauthentication.data.datasource.cookies.CookiesLocalDataSource
import com.practise.secureauthentication.data.util.suspendRunCatching
import com.practise.secureauthentication.domain.repository.AuthRepository
import domain.model.TokenId
import io.ktor.client.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.request.*
import io.ktor.http.*
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val client: HttpClient,
    private val cookies: CookiesLocalDataSource
): AuthRepository {
    override suspend fun signInWithGoogle(tokenId: TokenId) = suspendRunCatching {
        client.post(EndPoint.OAuth.Google()) {
            setBody(tokenId)
        }
    }
        .recoverCatching { throw DataError.identifyError(it) }
        .mapCatching { }

    override suspend fun isLoggedIn() = suspendRunCatching {
        val url = Url(client.href(EndPoint.User()))
        cookies.get(url).isNotEmpty()
    }

}
