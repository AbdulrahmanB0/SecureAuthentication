package com.practise.di

import com.practise.domain.model.security.hashing.HashingService
import com.practise.domain.model.security.hashing.PBKDF2WithHmacSHA512HashingService
import com.practise.domain.model.security.hashing.SHA256HashingService
import com.practise.domain.model.security.hashing.SHA512HashingService
import org.koin.core.qualifier.StringQualifier
import org.koin.dsl.module

val hashingModule = module {

    single<HashingService>(qualifier = StringQualifier("SHA256")) {
        SHA256HashingService()
    }

    single<HashingService>(qualifier = StringQualifier("PBKDF2")) {
        PBKDF2WithHmacSHA512HashingService()
    }

    single<HashingService>(qualifier = StringQualifier("SHA512")) {
        SHA512HashingService()
    }
}