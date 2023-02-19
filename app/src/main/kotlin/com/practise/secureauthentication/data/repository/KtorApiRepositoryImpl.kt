package com.practise.secureauthentication.data.repository

import com.practise.secureauthentication.data.api.EndPoint
import com.practise.secureauthentication.domain.model.ApiResponse
import com.practise.secureauthentication.domain.model.TokenId
import com.practise.secureauthentication.domain.model.User
import com.practise.secureauthentication.domain.model.UserUpdate
import com.practise.secureauthentication.domain.repository.KtorApiRepository
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.request.*
import javax.inject.Inject

class KtorApiRepositoryImpl @Inject constructor(
    private val client: HttpClient,
): KtorApiRepository {
    override suspend fun verifyToken(tokenId: TokenId): ApiResponse<Unit> {
        return client.post(EndPoint.OAuth.Google()) {
            setBody(tokenId)
        }.body()
    }

    override suspend fun getUserInfo(): ApiResponse<User> {
        return client.get(EndPoint.UserManipulation()).body()
    }

    override suspend fun updateUserInfo(userUpdate: UserUpdate): ApiResponse<Unit> {
        return client.put(EndPoint.UserManipulation()) {
            setBody(userUpdate)
        }.body()
    }

    override suspend fun deleteUser(): ApiResponse<Unit> {
        return client.delete(EndPoint.UserManipulation()).body()
    }

    override suspend fun signOut(): ApiResponse<Unit> {
        return client.get(EndPoint.UserManipulation.SignOut()).body()
    }

}