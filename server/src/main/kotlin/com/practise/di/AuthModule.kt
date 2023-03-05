package com.practise.di

import com.practise.data.repository.OAuthRepositoryImpl
import com.practise.domain.model.security.token.JwtTokenService
import com.practise.domain.model.security.token.TokenConfig
import com.practise.domain.model.security.token.TokenService
import com.practise.domain.repository.OAuthRepository
import io.ktor.server.application.*
import org.koin.core.qualifier.TypeQualifier
import org.koin.dsl.module
import kotlin.time.Duration.Companion.minutes

context(Application)
val authModule get() = module {
    single {
        TokenConfig(
            audience = environment.config.property("jwt.audience").getString(),
            issuer = environment.config.property("jwt.issuer").getString(),
            expiresIn = 10.minutes.inWholeMilliseconds,
            secret = environment.config.property("jwt.secret").getString()
        )
    }

    single<TokenService>(TypeQualifier(JwtTokenService::class)) {
        JwtTokenService()
    }

    single<OAuthRepository> { OAuthRepositoryImpl() }
}
