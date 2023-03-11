package com.practise.secureauthentication.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStoreFile
import com.practise.secureauthentication.data.model.network.Cookies
import com.practise.secureauthentication.data.model.user.User
import com.practise.secureauthentication.data.util.CookieSerializer
import com.practise.secureauthentication.data.util.UserSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.http.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PersistenceModule {

    @Singleton
    @Provides
    fun provideUserDataStore(@ApplicationContext context: Context): DataStore<User?> {
        return DataStoreFactory.create(
            serializer = UserSerializer,
            corruptionHandler = ReplaceFileCorruptionHandler { UserSerializer.defaultValue }
        ) { context.dataStoreFile("user_info.pb") }
    }

    @Singleton
    @Provides
    fun provideCookieDataStore(@ApplicationContext context: Context): DataStore<Cookies> {
        return DataStoreFactory.create(
            serializer = CookieSerializer,
            corruptionHandler = ReplaceFileCorruptionHandler { CookieSerializer.defaultValue }
        ) { context.dataStoreFile("cookies.pb") }
    }


}