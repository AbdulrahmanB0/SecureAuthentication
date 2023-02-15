package com.practise.domain.model.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow

@Serializable
@SerialName("GoogleUser")
data class GoogleUser(
    val subjectId: String,
    val photoUrl: ProfilePhotoUrl,
    override val emailVerified: Boolean = true,
    override val name: String,
    override val emailAddress: EmailAddress
): User() {

    object Table: org.jetbrains.exposed.sql.Table() {
        val id = reference("id", User.Table.id, onDelete = ReferenceOption.CASCADE)
        val subjectId = varchar("subjectId", 64).uniqueIndex()
        val photoUrl = varchar("photoUrl", 255)

        fun toModel(row: ResultRow): GoogleUser {
            return GoogleUser(
                subjectId = row[subjectId],
                photoUrl = ProfilePhotoUrl(row[photoUrl]),
                emailVerified = true,
                name = row[User.Table.name],
                emailAddress = EmailAddress(row[User.Table.emailAddress])
            )
        }
    }
}
