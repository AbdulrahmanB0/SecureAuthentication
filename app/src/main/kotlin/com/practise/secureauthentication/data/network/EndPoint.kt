package com.practise.secureauthentication.data.network

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Serializable @Resource("/")
@Suppress("unused")
class EndPoint {

    @Serializable @Resource("oauth")
    class OAuth(val parent: EndPoint = EndPoint()) {

        @Serializable @Resource("google")
        class Google(val parent: OAuth = OAuth())
    }

    @Serializable @Resource("user")
    class UserManipulation(val parent: EndPoint = EndPoint()) {

        @Serializable @Resource("signup")
        class SignUp(val parent: UserManipulation = UserManipulation())

        @Serializable @Resource("signin")
        class SignIn(val parent: UserManipulation = UserManipulation())
        @Serializable @Resource("signout")
        class SignOut(val parent: UserManipulation = UserManipulation())
    }
}