package com.practise

import com.practise.plugins.*
import io.ktor.server.application.*

fun main(args: Array<String>): Unit = io.ktor.server.cio.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    configureKoin() // Should be first
    configureAuthentication() // Should come before 'configureRouting()' always
    configureRouting()
    configureSessions()
    configureRequestValidation()
    configureStatusPages()
    configureSerialization()
    configureMonitoring()
}
