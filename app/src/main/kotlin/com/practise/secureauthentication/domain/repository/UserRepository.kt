package com.practise.secureauthentication.domain.repository

import com.practise.secureauthentication.data.model.user.User
import com.practise.secureauthentication.data.model.user.UserUpdate
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun getUserInfo(): Flow<Result<User>>
    suspend fun updateUserInfo(userUpdate: UserUpdate): Result<Unit>
    suspend fun deleteUser(): Result<Unit>
    suspend fun signOut(): Result<Unit>
}