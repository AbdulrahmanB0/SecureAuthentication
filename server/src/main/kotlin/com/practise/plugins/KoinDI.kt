package com.practise.plugins

import com.practise.di.*
import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin

fun Application.configureKoin() {
    install(Koin) {
        modules(
            databaseModule +
                hashingModule +
                mailerModule +
                authModule +
                sessionStorageModule +
                dataSourceBindingsModule
        )
    }
}
