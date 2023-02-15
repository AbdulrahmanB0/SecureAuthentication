package com.practise.data.repository

import com.practise.domain.model.user.EmailAddress
import com.practise.domain.model.templating.FreeMarkerProcessorService
import com.practise.domain.repository.MailerService
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.server.freemarker.*

class MailerServiceImpl(
    private val client: HttpClient,
    private val freeMarkerProcessor: FreeMarkerProcessorService
): MailerService {

    override suspend fun sendEmail(to: EmailAddress, subject: String, text: String) {
        client.post {
            parameter("to", to.value)
            parameter("subject", subject)
            parameter("text", text)
        }
    }

    override suspend fun sendEmail(to: EmailAddress, subject: String, content: FreeMarkerContent) {
        val html = freeMarkerProcessor.process(content)
        client.post {
            parameter("to", to.value)
            parameter("subject", subject)
            parameter("html", html)
        }
    }

}