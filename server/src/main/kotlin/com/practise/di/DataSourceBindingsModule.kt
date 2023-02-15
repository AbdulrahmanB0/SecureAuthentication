package com.practise.di

import com.practise.data.repository.MongoUserDataSource
import com.practise.data.repository.MySqlUserDataSource
import com.practise.domain.repository.UserDataSource
import org.koin.core.qualifier.TypeQualifier
import org.koin.dsl.module

val dataSourceBindingsModule = module {

    single<UserDataSource>(TypeQualifier(MongoUserDataSource::class)) {
        MongoUserDataSource(
            database = get()
        )
    }

    single<UserDataSource>(TypeQualifier(MySqlUserDataSource::class)) {
        MySqlUserDataSource(get())
    }

}