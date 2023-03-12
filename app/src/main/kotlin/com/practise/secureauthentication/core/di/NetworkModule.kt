package com.practise.secureauthentication.core.di

import androidx.datastore.core.DataStore
import com.practise.secureauthentication.data.datasource.cookies.CookiesLocalDataSource
import com.practise.secureauthentication.data.model.network.Cookies
import com.practise.secureauthentication.data.network.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.resources.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun providePersistentCookieStorage(datastore: DataStore<Cookies>): CookiesLocalDataSource =
        CookiesLocalDataSource(datastore)

    @Provides
    @Singleton
    fun provideHttpClient(cookieStorage: CookiesLocalDataSource): HttpClient {
        return HttpClient(CIO) {
            expectSuccess = true
            configureSerialization()
            configureTypeSafeRequests()
            configureCookies(cookieStorage)
            configureLogging()
            configureDefaultRequest()
            configureRetryPolicy()
        }
    }

}