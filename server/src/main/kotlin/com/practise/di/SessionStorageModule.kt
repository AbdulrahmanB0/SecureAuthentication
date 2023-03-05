package com.practise.di

import com.practise.data.sessionstorage.MongoSessionStorage
import com.practise.data.sessionstorage.RedisSessionStorage
import org.koin.dsl.module

val sessionStorageModule = module {

    single {
        MongoSessionStorage(get())
    }

    single {
        RedisSessionStorage(client = get())
    }
}
