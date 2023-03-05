package com.practise.data.repository

import com.practise.domain.model.user.GoogleUser
import com.practise.domain.model.user.RegularUser
import com.practise.domain.model.user.User
import com.practise.domain.model.user.UserUpdate
import com.practise.domain.repository.UserDataSource
import org.litote.kmongo.Id
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.setValue

class MongoUserDataSource(
    database: CoroutineDatabase
) : UserDataSource {

    private val userCollection = database.getCollection<User>()
    override suspend fun addUser(user: User): Boolean {
        return userCollection.insertOne(user).wasAcknowledged()
    }

    override suspend fun getUserByUsername(username: String): User? {
        return userCollection.findOne(RegularUser::username eq username)
    }

    override suspend fun getUserById(userId: Id<User>): User? {
        return userCollection.findOneById(userId)
    }

    override suspend fun getUserBySubject(subject: String): User? {
        return userCollection.findOne(GoogleUser::subjectId eq subject)
    }

    override suspend fun getUserOrAdd(user: GoogleUser): User? {
        return getUserBySubject(user.subjectId)
            ?: user.takeIf { userCollection.insertOne(it).wasAcknowledged() }
    }

    override suspend fun deleteUser(userId: Id<User>): Boolean {
        return userCollection.deleteOne(filter = User::id eq userId).wasAcknowledged()
    }

    override suspend fun updateUserInfo(userId: Id<User>, userUpdate: UserUpdate): Boolean {
        val (firstName, lastName) = userUpdate
        return userCollection.updateOne(
            filter = User::id eq userId,
            update = setValue(
                property = User::name,
                value = "$firstName $lastName"
            )
        ).wasAcknowledged()
    }
}
