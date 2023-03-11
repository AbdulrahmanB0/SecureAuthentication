package com.practise.secureauthentication.domain.repository

import domain.model.TokenId

interface AuthRepository {

    suspend fun signInWithGoogle(tokenId: TokenId): Result<Unit>
}