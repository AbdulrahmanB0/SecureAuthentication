package com.practise.secureauthentication.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.practise.secureauthentication.domain.model.User
import com.practise.secureauthentication.domain.repository.CacheRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CacheRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val userDataStore: DataStore<User?>
): CacheRepository {

    private object PreferenceKeys {
        val signIn = booleanPreferencesKey("sign_in")
    }
    override suspend fun saveSignedInState(signIn: Boolean) {
        dataStore.edit { prefs ->
            prefs[PreferenceKeys.signIn] = signIn
        }
    }

    override fun readSignedInState(): Flow<Boolean> {
        return dataStore.data.map {
            it[PreferenceKeys.signIn] ?: false
        }
    }

    override suspend fun getUserInfo(): Flow<User?> {
        return userDataStore.data
    }

    override suspend fun updateUserInfo(transform: (User?) -> User?) {
        userDataStore.updateData(transform)
    }
}