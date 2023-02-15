package com.practise.domain.model.security.token

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

class JwtTokenService: TokenService {
    override fun generateToken(config: TokenConfig, vararg claims: TokenClaim): String {

        var token = JWT.create()
            .withIssuer(config.issuer)
            .withAudience(config.audience)
            .withIssuedAt(Date(System.currentTimeMillis()))
            .withExpiresAt(
                Date(System.currentTimeMillis() + config.expiresIn)
            )

        claims.forEach {
            token = token.withClaim(it.name, it.value)
        }

        return token.sign(Algorithm.HMAC256(config.secret))
    }

}