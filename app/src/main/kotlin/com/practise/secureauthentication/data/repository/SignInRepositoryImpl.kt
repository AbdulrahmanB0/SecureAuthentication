package com.practise.secureauthentication.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.practise.secureauthentication.domain.repository.SignInRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SignInRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
): SignInRepository {

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
}