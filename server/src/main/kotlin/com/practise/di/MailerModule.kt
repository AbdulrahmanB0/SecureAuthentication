package com.practise.di

import com.practise.data.repository.MailerServiceImpl
import com.practise.domain.model.templating.FreeMarkerProcessorService
import com.practise.domain.repository.MailerService
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.server.application.*
import org.koin.dsl.module

context(Application)
val mailerModule get() = module {
    val key = environment.config.property("mailgun.API_KEY").getString()
    val endpoint = environment.config.property("mailgun.endpoint").getString()
    val from = environment.config.property("mailgun.from").getString()

    single {
        HttpClient(CIO) {
            Auth {
                basic {
                    credentials {
                        BasicAuthCredentials("api", key)
                    }
                }
            }

            defaultRequest {
                url {
                    url(endpoint)
                    parameters.append("from", from)
                }
            }
        }
    }

    single {
        FreeMarkerProcessorService()
    }

    single<MailerService> {
        MailerServiceImpl(
            client = get(),
            freeMarkerProcessor = get()
        )
    }
}
