package com.practise.secureauthentication.core.di

import com.practise.secureauthentication.data.repository.AuthRepositoryImpl
import com.practise.secureauthentication.data.repository.NetworkStatusRepositoryImpl
import com.practise.secureauthentication.data.repository.UserRepositoryImpl
import com.practise.secureauthentication.data.model.crypto.AndroidCryptoService
import com.practise.secureauthentication.data.model.crypto.CryptoService
import com.practise.secureauthentication.domain.repository.AuthRepository
import com.practise.secureauthentication.domain.repository.NetworkStatusRepository
import com.practise.secureauthentication.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
@Suppress("unused")
abstract class BinderModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindConnectivityObserver(impl: NetworkStatusRepositoryImpl): NetworkStatusRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindAndroidCryptoService(impl: AndroidCryptoService): CryptoService
}