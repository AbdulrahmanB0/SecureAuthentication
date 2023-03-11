package com.practise.secureauthentication.data.datasource.user

import com.practise.secureauthentication.data.model.user.UserUpdate
import com.practise.secureauthentication.data.network.EndPoint
import io.ktor.client.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.request.*
import javax.inject.Inject

class UserRemoteDataSource @Inject constructor(
    private val client: HttpClient
) {

    suspend fun getUserInfo() = client.get(EndPoint.User())

    suspend fun updateUserInfo(userUpdate: UserUpdate) =
        client.patch(EndPoint.User()) {
            setBody(userUpdate)
        }

    suspend fun deleteUser() = client.delete(EndPoint.User())

    suspend fun signOutUser() = client.get(EndPoint.User.SignOut())
}