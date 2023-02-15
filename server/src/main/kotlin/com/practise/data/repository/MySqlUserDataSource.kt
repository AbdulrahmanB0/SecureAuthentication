package com.practise.data.repository

import com.practise.domain.model.user.GoogleUser
import com.practise.domain.model.user.RegularUser
import com.practise.domain.model.user.User
import com.practise.domain.model.user.UserUpdate
import com.practise.domain.repository.UserDataSource
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import org.jetbrains.exposed.sql.transactions.transaction
import org.litote.kmongo.Id

class MySqlUserDataSource(
    private val db: Database
): UserDataSource {

    init {
        transaction {
            SchemaUtils.createMissingTablesAndColumns(
                User.Table,
                RegularUser.Table,
                GoogleUser.Table
            )
        }
    }
    override suspend fun addUser(user: User): Boolean {
        return suspendedTransactionAsync(Dispatchers.IO, db) {
            var wasAcknowledged = User.Table.insert {
                it[id] = user.id.toString()
                it[name] = user.name
                it[emailAddress] = user.emailAddress.value
                it[createdAt] = user.createdAt
                it[emailVerified] = user.emailVerified
                it[type] = user::class.simpleName!!
            }.insertedCount == 1
            if (wasAcknowledged) {
                wasAcknowledged = when (user) {
                    is RegularUser -> insertRegularUser(user)
                    is GoogleUser ->  insertGoogleUser(user)
                }
            }
            if(!wasAcknowledged)
                rollback()
            wasAcknowledged
        }.await()


    }

    override suspend fun getUserByUsername(username: String): User? {
        return suspendedTransactionAsync(Dispatchers.IO, db) {
            (RegularUser.Table innerJoin User.Table)
                        .select { RegularUser.Table.username eq username }.limit(1)
                        .singleOrNull()
                        ?.let(RegularUser.Table::toModel)
        }.await()
    }

    override suspend fun getUserById(userId: Id<User>): User? {
        return suspendedTransactionAsync(Dispatchers.IO, db) {
            User.Table.select { User.Table.id eq userId.toString() }.singleOrNull()?.let {
                when(it[User.Table.type]) {
                    RegularUser::class.simpleName -> RegularUser.Table.toModel(it)
                    GoogleUser::class.simpleName -> GoogleUser.Table.toModel(it)
                    else -> null
                }
            }
        }.await()
    }

    override suspend fun getUserBySubject(subject: String): User? {
        return suspendedTransactionAsync(Dispatchers.IO, db) {
            (GoogleUser.Table innerJoin User.Table)
                        .select { GoogleUser.Table.subjectId eq subject }.limit(1)
                        .singleOrNull()
                        ?.let(GoogleUser.Table::toModel)
        }.await()
    }

    override suspend fun getUserOrAdd(user: GoogleUser): User? {
        return suspendedTransactionAsync(Dispatchers.IO, db) {
            getUserBySubject(user.subjectId) ?: user.takeIf { addUser(it) }
        }.await()
    }

    override suspend fun deleteUser(userId: Id<User>): Boolean {
        return suspendedTransactionAsync(Dispatchers.IO, db) {
            User.Table.deleteWhere { id eq userId.toString() } == 1
        }.await()
    }

    override suspend fun updateUserInfo(userId: Id<User>, userUpdate: UserUpdate): Boolean {
        return suspendedTransactionAsync(Dispatchers.IO, db) {
            User.Table.update(
                where = { User.Table.id eq userId.toString() },
                limit = 1
            ) {
                it[name] = "${userUpdate.firstName} ${userUpdate.lastName}"
            } == 1
        }.await()
    }

    private fun insertGoogleUser(user: GoogleUser) = GoogleUser.Table.insert {
        it[id] = user.id.toString()
        it[subjectId] = user.subjectId
    }.insertedCount == 1

    private fun insertRegularUser(user: RegularUser) = RegularUser.Table.insert {
        it[id] = user.id.toString()
        it[username] = user.username
        it[password] = user.password
        it[salt] = user.salt
    }.insertedCount == 1
}