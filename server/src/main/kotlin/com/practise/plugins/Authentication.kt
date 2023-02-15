package com.practise.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.practise.domain.model.MessagesResource
import com.practise.domain.model.UserSession
import com.practise.domain.model.security.token.TokenConfig
import com.practise.core.Constants
import io.ktor.http.*
import io.ktor.http.auth.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import org.koin.ktor.ext.inject

fun Application.configureAuthentication() {
    val jwtTokenConfig by inject<TokenConfig>()
    authentication {
        session<UserSession>(name = Constants.AUTH_SESSION) {
            validate { session ->
                session
            }
            challenge {
                call.application.log.error(MessagesResource.SESSION_FAIL.message)
                call.respond(HttpStatusCode.Unauthorized)
            }
        }


        jwt(Constants.AUTH_JWT) {

            authHeader { call ->
                call.request.queryParameters["t"]?.let {
                    HttpAuthHeader.Single(AuthScheme.Bearer, it)               //Verify JWT from URL query parameters if exists,
                } ?: call.request.parseAuthorizationHeader()    //Else, verify JWT from Authorization header
            }

            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtTokenConfig.secret))
                    .withAudience(jwtTokenConfig.audience)
                    .withIssuer(jwtTokenConfig.issuer)
                    .build()
            )

            validate { jwtCredential ->
                JWTPrincipal(jwtCredential.payload)
            }

        }
    }
}