package com.practise.secureauthentication.domain.repository

import kotlinx.coroutines.flow.Flow

interface NetworkStatusRepository {

    fun observe(): Flow<Status>

    enum class Status {
        Available, Unavailable, Losing, Lost
    }
}