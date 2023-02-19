package com.practise.di

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import redis.clients.jedis.JedisPooled as RedisClient

context(Application)
val databaseModule get() = module {
    environment
    single {
        Database.connect(
            url = environment.config.property("database.exposed.url").getString(),
            driver = environment.config.property("database.exposed.driver").getString(),
            user = environment.config.property("database.exposed.username").getString(),
            password = environment.config.property("database.exposed.password").getString()
        )
    }


    single {
        val connection = environment.config.property("database.mongo.connection").getString()
        val databaseName = environment.config.property("database.mongo.databaseName").getString()
        KMongo
            .createClient(connection)
            .coroutine
            .getDatabase(databaseName)
    }


    single {
        RedisClient(
            environment.config.property("database.redis.host").getString(),
            environment.config.property("database.redis.port").getString().toInt(),
            environment.config.property("database.redis.username").getString(),
            environment.config.property("database.redis.password").getString(),
        )
    }
}