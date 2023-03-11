package com.practise.secureauthentication.data.datasource.user

import androidx.datastore.core.DataStore
import com.practise.secureauthentication.data.model.user.User
import javax.inject.Inject

class UserLocalDataSource @Inject constructor(
    private val dataStore: DataStore<User?>
) {
    fun getUserInfo() = dataStore.data

    suspend fun updateUserInfo(transform: suspend (User?) -> User?) = dataStore.updateData(transform)


}