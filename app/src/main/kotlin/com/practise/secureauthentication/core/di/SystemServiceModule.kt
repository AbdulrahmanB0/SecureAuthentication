package com.practise.secureauthentication.core.di

import android.content.Context
import android.net.ConnectivityManager
import com.practise.secureauthentication.data.util.crypto.AndroidCryptoService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SystemServiceModule {

    @Provides
    @Singleton
    fun provideConnectivityManager(@ApplicationContext context: Context): ConnectivityManager =
        context.getSystemService(ConnectivityManager::class.java)

    @Provides
    @Singleton
    fun provideAndroidCryptoService(): AndroidCryptoService = AndroidCryptoService()

}