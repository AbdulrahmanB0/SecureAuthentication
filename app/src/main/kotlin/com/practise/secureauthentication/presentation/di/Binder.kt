package com.practise.secureauthentication.presentation.di

import com.practise.secureauthentication.data.repository.KtorApiRepositoryImpl
import com.practise.secureauthentication.data.repository.SignInRepositoryImpl
import com.practise.secureauthentication.domain.repository.KtorApiRepository
import com.practise.secureauthentication.domain.repository.SignInRepository
import com.practise.secureauthentication.presentation.core.connectivity.ConnectivityObserver
import com.practise.secureauthentication.presentation.core.connectivity.NetworkConnectivityObserver
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
@Suppress("unused")
abstract class Binder {

    @Binds
    @Singleton
    abstract fun bindSignInRepository(impl: SignInRepositoryImpl): SignInRepository

    @Binds
    @Singleton
    abstract fun bindKtorApiRepository(impl: KtorApiRepositoryImpl): KtorApiRepository

    @Binds
    @Singleton
    abstract fun bindConnectivityObserver(impl: NetworkConnectivityObserver): ConnectivityObserver
}