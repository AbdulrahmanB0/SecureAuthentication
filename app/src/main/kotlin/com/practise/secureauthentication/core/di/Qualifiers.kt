package com.practise.secureauthentication.core.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OneTapSignIn

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OneTapSignUp