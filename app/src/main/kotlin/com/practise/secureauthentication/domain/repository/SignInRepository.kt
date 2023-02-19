package com.practise.secureauthentication.domain.repository

import kotlinx.coroutines.flow.Flow

interface SignInRepository {
    suspend fun saveSignedInState(signIn: Boolean)
    fun readSignedInState(): Flow<Boolean>
}