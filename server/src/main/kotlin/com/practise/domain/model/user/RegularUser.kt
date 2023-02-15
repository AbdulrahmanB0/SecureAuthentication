package com.practise.domain.model.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow

@Serializable
@SerialName("RegularUser")
data class RegularUser(
    val username: String,
    val password: String,
    val salt: String = "",
    override val emailVerified: Boolean = false,
    override val name: String,
    override val emailAddress: EmailAddress
): User() {

    object Table: org.jetbrains.exposed.sql.Table() {
        val id = reference("id", User.Table.id, onDelete = ReferenceOption.CASCADE)
        val username = varchar("username", 50).uniqueIndex()
        val password = varchar("password", 128)
        val salt = varchar("salt", 128)

        override val primaryKey = PrimaryKey(id)

        fun toModel(row: ResultRow): RegularUser {
            return RegularUser(
                username = row[username],
                password = row[password],
                salt = row[salt],
                emailVerified = row[User.Table.emailVerified],
                name = row[User.Table.name],
                emailAddress = EmailAddress(row[User.Table.emailAddress])
            )
        }

    }
}