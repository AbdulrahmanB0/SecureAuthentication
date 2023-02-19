package com.practise.domain.model.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

@Serializable
@SerialName("GoogleUser")
data class GoogleUser(
    val subjectId: String,
    val photoUrl: ProfilePhotoUrl,
    override val emailVerified: Boolean = true,
    override val name: String,
    override val emailAddress: EmailAddress
): User() {

    object Entity: Table() {
        val id = reference("id", User.Entity.id, onDelete = ReferenceOption.CASCADE)
        val subjectId = varchar("subjectId", 64).uniqueIndex()
        val photoUrl = varchar("photoUrl", 255)

        override val tableName = "google_user"

        fun toModel(row: ResultRow): GoogleUser {
            return GoogleUser(
                subjectId = row[subjectId],
                photoUrl = ProfilePhotoUrl(row[photoUrl]),
                emailVerified = true,
                name = row[User.Entity.name],
                emailAddress = EmailAddress(row[User.Entity.emailAddress])
            )
        }
    }
}
