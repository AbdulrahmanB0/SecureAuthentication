package com.practise.domain.repository

import com.practise.domain.model.user.GoogleUser
import com.practise.domain.model.user.User
import com.practise.domain.model.user.UserUpdate
import org.litote.kmongo.Id

interface UserDataSource {

    suspend fun addUser(user: User): Boolean

    suspend fun getUserByUsername(username: String): User?
    suspend fun getUserById(userId: Id<User>): User?

    suspend fun getUserBySubject(subject: String): User?

    suspend fun getUserOrAdd(user: GoogleUser): User?

    suspend fun deleteUser(userId: Id<User>): Boolean

    suspend fun updateUserInfo(userId: Id<User>, userUpdate: UserUpdate): Boolean
}