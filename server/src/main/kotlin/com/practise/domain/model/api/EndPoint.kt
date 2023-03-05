package com.practise.domain.model.api

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Serializable
@Resource("/")
@Suppress("unused")
class EndPoint {

    @Serializable
    @Resource("oauth")
    class OAuth(val parent: EndPoint = EndPoint()) {

        @Serializable
        @Resource("google")
        class Google(val parent: OAuth = OAuth())
    }

    @Serializable
    @Resource("user")
    class User(val parent: EndPoint = EndPoint()) {

        @Serializable
        @Resource("signup")
        class SignUp(val parent: User = User())

        @Serializable
        @Resource("signin")
        class SignIn(val parent: User = User())

        @Serializable
        @Resource("signout")
        class SignOut(val parent: User = User())
    }

    @Serializable
    @Resource("swagger")
    class Swagger(val parent: EndPoint = EndPoint())
}
