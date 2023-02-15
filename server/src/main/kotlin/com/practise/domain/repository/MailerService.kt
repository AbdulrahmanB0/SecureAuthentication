package com.practise.domain.repository

import com.practise.domain.model.user.EmailAddress
import io.ktor.server.freemarker.*

interface MailerService {

    suspend fun sendEmail(
        to: EmailAddress,
        subject: String,
        text: String
    )

    suspend fun sendEmail(
        to: EmailAddress,
        subject: String,
        content: FreeMarkerContent
    )
}