package com.practise.routes

import com.practise.core.Constants
import com.practise.domain.model.MessagesResource
import com.practise.domain.model.UserSession
import com.practise.domain.model.api.EndPoint
import com.practise.domain.model.user.UserUpdate
import com.practise.domain.repository.UserDataSource
import domain.model.ApiResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.patch
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import org.bson.types.ObjectId
import org.koin.core.qualifier.TypeQualifier
import org.koin.ktor.ext.inject
import org.litote.kmongo.id.toId
import com.practise.data.repository.MongoUserDataSource as Mongo

/** This function aggregates all the defined user routes with proper authentication */
fun Route.userRoutes() {
    val userDataSource: UserDataSource by inject(TypeQualifier(Mongo::class))

    authenticate(Constants.AUTH_SESSION) {
        getUserInfoRoute(userDataSource)
        updateUserInfoRoute(userDataSource)
        deleteUserRoute(userDataSource)
        signOutUserRoute()
    }
}

/**
 * This function returns the user info from the database when requested
 */
private fun Route.getUserInfoRoute(dataSource: UserDataSource) {
    get<EndPoint.User> {
        val userSession = call.principal<UserSession>()!!
        val user = dataSource.getUserById(ObjectId(userSession.id).toId())
        if (user != null) {
            MessagesResource.USER_INFO_RETRIEVAL_SUCCESS.message.let { message ->
                application.log.info(message)
                call.respond(ApiResponse(data = user, message = message))
            }
        } else {
            MessagesResource.USER_INFO_RETRIEVAL_FAILED.message.let { message ->
                application.log.error(message)
                call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(false, message = message))
            }
        }
    }
}

/**
 * This function is used to update the user info in the database when requested
 */
private fun Route.updateUserInfoRoute(dataSource: UserDataSource) {
    patch<EndPoint.User> {
        val userSession = call.principal<UserSession>()!!

        val userUpdate = call.receive<UserUpdate>()
        val isSuccess = dataSource.updateUserInfo(ObjectId(userSession.id).toId(), userUpdate)

        if (isSuccess) {
            application.log.info(MessagesResource.USER_INFO_UPDATE_SUCCESS.message)
            call.respond(status = HttpStatusCode.OK, ApiResponse<Unit>(message = MessagesResource.USER_INFO_UPDATE_SUCCESS.message))
        } else {
            MessagesResource.USER_INFO_UPDATE_FAILED.message.let { message ->
                application.log.error(message)
                call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(false, message = message))
            }
        }
    }
}

/**
 * This route is used to permanently delete the user from the database when requested
 */
private fun Route.deleteUserRoute(dataSource: UserDataSource) {
    delete<EndPoint.User> {
        val userSession = call.principal<UserSession>()!!
        val isSuccess = dataSource.deleteUser(ObjectId(userSession.id).toId())

        if (isSuccess) {
            application.log.info(MessagesResource.USER_DELETE_SUCCESS.message)
            call.sessions.clear<UserSession>()
            call.respond(HttpStatusCode.OK, ApiResponse<Unit>(message = MessagesResource.USER_DELETE_SUCCESS.message))
        } else {
            MessagesResource.USER_DELETE_FAILED.message.let { message ->
                application.log.error(message)
                call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(false, message = message))
            }
        }
    }
}

/**
 * This route is used sign out the user by clearing his session when requested
 */
private fun Route.signOutUserRoute() {
    get<EndPoint.User.SignOut> {
        call.sessions.clear<UserSession>()
        call.respond(HttpStatusCode.OK, ApiResponse<Unit>(message = MessagesResource.SIGNED_OUT.message))
    }
}
