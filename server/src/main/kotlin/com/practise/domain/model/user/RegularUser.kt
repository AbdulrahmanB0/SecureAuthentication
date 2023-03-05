package com.practise.domain.model.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

@Serializable
@SerialName("RegularUser")
data class RegularUser(
    val username: String,
    val password: String,
    val salt: String = "",
    override val emailVerified: Boolean = false,
    override val name: String,
    override val emailAddress: EmailAddress
) : User() {

    object Entity : Table() {
        val id = reference("id", User.Entity.id, onDelete = ReferenceOption.CASCADE)
        val username = varchar("username", 50).uniqueIndex()
        val password = varchar("password", 128)
        val salt = varchar("salt", 128)

        override val tableName = "regular_user"

        fun toModel(row: ResultRow): RegularUser {
            return RegularUser(
                username = row[username],
                password = row[password],
                salt = row[salt],
                emailVerified = row[User.Entity.emailVerified],
                name = row[User.Entity.name],
                emailAddress = EmailAddress(row[User.Entity.emailAddress])
            )
        }
    }
}
