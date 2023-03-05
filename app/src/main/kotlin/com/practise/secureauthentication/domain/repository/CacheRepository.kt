package com.practise.secureauthentication.domain.repository

import com.practise.secureauthentication.domain.model.User
import kotlinx.coroutines.flow.Flow

interface CacheRepository {
    suspend fun saveSignedInState(signIn: Boolean)
    fun readSignedInState(): Flow<Boolean>

    suspend fun getUserInfo(): Flow<User?>
    suspend fun updateUserInfo(transform: (User?) -> User?)
}