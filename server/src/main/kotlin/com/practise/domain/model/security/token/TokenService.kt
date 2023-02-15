package com.practise.domain.model.security.token

interface TokenService {

    fun generateToken(
        config: TokenConfig,
        vararg claims: TokenClaim
    ): String
}