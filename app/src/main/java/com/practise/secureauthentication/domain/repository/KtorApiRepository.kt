package com.practise.secureauthentication.domain.repository

import com.practise.secureauthentication.domain.model.ApiResponse
import com.practise.secureauthentication.domain.model.TokenId
import com.practise.secureauthentication.domain.model.User
import com.practise.secureauthentication.domain.model.UserUpdate

interface KtorApiRepository {

    suspend fun verifyToken(tokenId: TokenId): ApiResponse<Unit>

    suspend fun getUserInfo(): ApiResponse<User>

    suspend fun updateUserInfo(userUpdate: UserUpdate): ApiResponse<Unit>

    suspend fun deleteUser(): ApiResponse<Unit>

    suspend fun signOut(): ApiResponse<Unit>
}