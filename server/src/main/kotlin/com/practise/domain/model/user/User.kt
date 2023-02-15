package com.practise.domain.model.user

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id
import org.litote.kmongo.newId
import java.time.Instant

@Serializable
sealed class User {
    @Contextual @SerialName("_id") val id: Id<User> = newId()
    abstract val name: String
    abstract val emailAddress: EmailAddress
    val createdAt: Long = Instant.now().epochSecond
    abstract val emailVerified: Boolean



    object Table: org.jetbrains.exposed.sql.Table() {
        val id = varchar("id", 24)
        val name = varchar("name", 50)
        val emailAddress = varchar("emailAddress", 255)
        val createdAt = long("createdAt")
        val emailVerified = bool("emailVerified")
        val type = varchar("type", 50)

        override val primaryKey = PrimaryKey(id)
    }
}
