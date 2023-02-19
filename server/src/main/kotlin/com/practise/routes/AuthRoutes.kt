package com.practise.routes

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.practise.data.repository.MongoUserDataSource
import com.practise.domain.model.MessagesResource
import com.practise.domain.model.TokenId
import com.practise.domain.model.UserSession
import com.practise.domain.model.api.ApiResponse
import com.practise.domain.model.api.EndPoint
import com.practise.domain.model.security.hashing.HashingService
import com.practise.domain.model.security.hashing.SaltedHash
import com.practise.domain.model.user.*
import com.practise.domain.repository.OAuthRepository
import com.practise.domain.repository.UserDataSource
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import org.koin.core.qualifier.StringQualifier
import org.koin.core.qualifier.TypeQualifier
import org.koin.ktor.ext.inject

fun Route.authRoutes() {

    val dataSource: UserDataSource by inject(TypeQualifier(MongoUserDataSource::class))
    val oAuthRepository: OAuthRepository by inject()
    val hashingService: HashingService by inject(StringQualifier("PBKDF2"))

    registerUserRoute(dataSource, hashingService)
    loginUserRoute(dataSource, hashingService)
    signInWithGoogleRoute(dataSource, oAuthRepository::verifyGoogleTokenId)


}

private fun Route.signInWithGoogleRoute(
    dataSource: UserDataSource,
    verifyGoogleTokenId: suspend (String) -> GoogleIdToken?
) {

    post<EndPoint.OAuth.Google> {
        val token = call.receive<TokenId>()
        val result = verifyGoogleTokenId(token.value)
        result?.payload?.let { payload ->
            val user = GoogleUser(
                subjectId = payload.subject,
                name = payload["name"].toString(),
                emailAddress = EmailAddress(payload.email),
                photoUrl = ProfilePhotoUrl(payload["picture"].toString())
            ).let { dataSource.getUserOrAdd(it) }
            if(user != null) {

                call.sessions.set(UserSession(user.id.toString()))
                application.log.info(MessagesResource.USER_SIGN_IN_SUCCESS.message)
                val response = ApiResponse<Unit>(message = "Welcome! ${user.name}")
                call.respond(response)
            }
            else call.respond(HttpStatusCode.InternalServerError)

        } ?: kotlin.run {
            application.log.info(MessagesResource.TOKEN_VERIFICATION_FAILED.message)
            call.respond(HttpStatusCode.Unauthorized)
        }
    }
}

private fun Route.registerUserRoute(
    userDataSource: UserDataSource,
    hashingService: HashingService,

    ) {
    post<EndPoint.UserManipulation.SignUp> {
        val user = call.receive<RegularUser>().let {
            val saltedHash = hashingService.generateSaltedHash(it.password)
            it.copy(password = saltedHash.hash, salt = saltedHash.salt)
        }

        val success = userDataSource.addUser(user)
        if(success) {
            call.sessions.set(UserSession(user.id.toString()))
            MessagesResource.USER_REGISTER_SUCCESS.message.let {
                call.respond(
                    HttpStatusCode.Created,
                    ApiResponse<Unit>(message = it)
                )
            }
        }
        else {
            MessagesResource.USER_REGISTER_FAIL.message.let {
                application.log.info(it)
                call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse<Unit>(success = false, message = it)
                )
            }
        }
    }
}

private fun Route.loginUserRoute(userDataSource: UserDataSource, hashingService: HashingService) {
    post<EndPoint.UserManipulation.SignIn> {
        val credentials = call.receive<UserCredentials>()
        val user = userDataSource.getUserByUsername(credentials.username) as? RegularUser?

        if(user != null) {
            val saltedHash = SaltedHash(user.password, user.salt)
            val passwordIsVerified = hashingService.verify(credentials.password, saltedHash)
            if (passwordIsVerified) {
                call.sessions.set(UserSession(user.id.toString()))
                call.respond(
                    ApiResponse<Unit>(message = MessagesResource.USER_LOGIN_SUCCESS.message + "| Welcome Back, ${user.name}!")
                )

            } else {
                call.respond(HttpStatusCode.Unauthorized)
            }
        }
        else {
            call.respond(HttpStatusCode.Unauthorized)
        }

    }
}