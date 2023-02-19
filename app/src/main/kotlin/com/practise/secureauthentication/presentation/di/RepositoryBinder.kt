package com.practise.secureauthentication.presentation.di

import com.practise.secureauthentication.data.repository.OAuthRepositoryImpl
import com.practise.secureauthentication.data.repository.KtorApiRepositoryImpl
import com.practise.secureauthentication.data.repository.SignInRepositoryImpl
import com.practise.secureauthentication.domain.repository.OAuthRepository
import com.practise.secureauthentication.domain.repository.KtorApiRepository
import com.practise.secureauthentication.domain.repository.SignInRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryBinder {

    @Binds
    @Singleton
    abstract fun bindSignInRepository(impl: SignInRepositoryImpl): SignInRepository

    @Binds
    @Singleton
    abstract fun bindKtorApiRepository(impl: KtorApiRepositoryImpl): KtorApiRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: OAuthRepositoryImpl): OAuthRepository
}