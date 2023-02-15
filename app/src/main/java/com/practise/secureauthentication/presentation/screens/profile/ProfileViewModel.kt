package com.practise.secureauthentication.presentation.screens.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practise.secureauthentication.domain.model.Resource
import com.practise.secureauthentication.domain.model.User
import com.practise.secureauthentication.domain.model.UserUpdate
import com.practise.secureauthentication.domain.repository.KtorApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val ktorApiRepository: KtorApiRepository
): ViewModel() {

    var uiState by mutableStateOf(ProfileUiState())
        private set

    fun setMenuExpandedState(value: Boolean) {
        uiState = uiState.copy(topBarMenuExpanded = value)
    }

    fun getUserInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                ktorApiRepository.getUserInfo()
            }.onSuccess {
                val user = it.data!!
                val (firstName, lastName) = splitFullName(user.name)
                setFirstName(firstName)
                setLastName(lastName)
            }.onFailure {

            }
        }
    }

    fun saveUserInfo() {
        viewModelScope.launch(Dispatchers.IO) {

        }
    }

    fun setFirstName(value: String) {
        uiState = uiState.copy(firstNameText = value)
    }

    fun setLastName(value: String) {
        uiState = uiState.copy(lastNameText = value)
    }

    fun setUserInfoRequestState(resource: Resource<User>) {
        uiState = uiState.copy(userInfoRequest = resource)
    }

    fun setUpdateRequestState(resource: Resource<UserUpdate>) {
        uiState = uiState.copy(updateUserInfoRequest = resource)
    }

    private suspend fun splitFullName(name: String): List<String> {
        return withContext(Dispatchers.Default) {
            name.split(" ", limit = 2)
        }
    }
}

data class ProfileUiState(
    val topBarMenuExpanded: Boolean = false,
    val firstNameText: String = "",
    val lastNameText: String = "",
    val error: Boolean = false,
    val userInfoRequest: Resource<User> = Resource.Idle,
    val updateUserInfoRequest: Resource<UserUpdate> = Resource.Idle
)