package com.practise.plugins

import ch.qos.logback.classic.Logger
import io.ktor.server.application.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.request.*
import org.slf4j.LoggerFactory
import org.slf4j.event.Level

fun Application.configureMonitoring() {
    install(CallLogging) {
        level = Level.DEBUG
        filter { call -> call.request.path().startsWith("/") }
    }

    install(MongoLogging) {
        level = ch.qos.logback.classic.Level.ERROR
    }
}

private class MongoLoggerConfiguration {
    var level: ch.qos.logback.classic.Level =
        ch.qos.logback.classic.Level.INFO
}

private val MongoLogging = createApplicationPlugin(
    name = "MongoLogging",
    createConfiguration = ::MongoLoggerConfiguration
) {
    (
        LoggerFactory
            .getLogger("org.mongodb.driver")
            as Logger
        ).level = pluginConfig.level
}
