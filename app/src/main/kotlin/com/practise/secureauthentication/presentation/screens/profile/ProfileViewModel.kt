package com.practise.secureauthentication.presentation.screens.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practise.secureauthentication.R
import com.practise.secureauthentication.data.model.UserUnauthorizedException
import com.practise.secureauthentication.data.model.user.User
import com.practise.secureauthentication.data.model.user.UserUpdate
import com.practise.secureauthentication.domain.repository.NetworkStatusRepository
import com.practise.secureauthentication.domain.repository.UserRepository
import com.practise.secureauthentication.presentation.model.UiResource
import com.practise.secureauthentication.presentation.model.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepo: UserRepository,
    private val networkObserver: NetworkStatusRepository
): ViewModel() {

    var uiState by mutableStateOf(UiState())
        private set

    private val _informMessages = Channel<UiText>()
    val informMessages = _informMessages.receiveAsFlow()

    init {
        viewModelScope.launch {
            userRepo.getUserInfo()
                .onStart { uiState = uiState.copy(userInfo = UiResource.Loading) }
                .map { result -> UiResource.from(result) }
                .collectLatest {
                setUserInfo(it)
                it.getOrNull()?.let { user ->
                    val (firstName, lastName) = splitFullName(user.name)
                    setFirstName(firstName)
                    setLastName(lastName)
                    uiState = uiState.copy(emailAddress = user.emailAddress.value)
                }
            }
        }

        viewModelScope.launch {
            networkObserver.observe().collectLatest {
                uiState = uiState.copy(connected = it == NetworkStatusRepository.Status.Available)
            }
        }
    }

    fun retryLoading() {
        viewModelScope.launch {
            userRepo.getUserInfo().onStart {
                uiState = uiState.copy(userInfo = UiResource.Loading)
            }.lastOrNull()?.let {
                setUserInfo(UiResource from it)
            }
        }
    }
    fun setMenuExpandedState(value: Boolean) {
        uiState = uiState.copy(topBarMenuExpanded = value)
    }

    fun setFirstName(value: String) {
        uiState = uiState.copy(firstNameText = value, firstNameError = value.isEmpty() || value.isBlank())
    }

    fun setLastName(value: String) {
        uiState = uiState.copy(lastNameText = value, lastNameError = value.isEmpty() || value.isBlank())
    }

    private fun setUserInfo(uiResource: UiResource<User>) {
        uiState = uiState.copy(userInfo = uiResource)
    }

    private suspend fun splitFullName(name: String): List<String> {
        return withContext(Dispatchers.Default) {
            name.split(" ", limit = 2)
        }
    }

    fun deleteAccount(onSuccess: () -> Unit) {
        viewModelScope.launch {
            UiResource.from { userRepo.deleteUser() }.collectLatest {
                uiState = uiState.copy(userDelete = it)
                if(it is UiResource.Success)
                    onSuccess()
            }
        }
    }

    fun updateUserInfo() {
        viewModelScope.launch {
            val user = uiState.userInfo.getOrNull() ?: return@launch
            if (uiState.firstNameError || uiState.lastNameError)
                _informMessages.send(UiText.StringResource(R.string.invalid_name))
            
            else if ("${uiState.firstNameText} ${uiState.lastNameText}" == user.name)
                _informMessages.send(UiText.StringResource(R.string.nothing_to_update))
            
            else {
                val userUpdate = UserUpdate(uiState.firstNameText, uiState.lastNameText)
                UiResource.from { userRepo.updateUserInfo(userUpdate) }.collectLatest {
                    uiState = uiState.copy(userUpdate = it)
                }
            }
        }
    }


    fun signOut(onSuccess: () -> Unit) {
        viewModelScope.launch {
            UiResource.from { userRepo.signOut() }.collectLatest {
                uiState = uiState.copy(userSignOut = it)
                if (it is UiResource.Success || it is UiResource.Failure && it.throwable is UserUnauthorizedException)
                    onSuccess()
            }
        }
    }

    data class UiState(
        val topBarMenuExpanded: Boolean = false,
        val firstNameText: String = "",
        val firstNameError: Boolean = false,
        val lastNameText: String = "",
        val lastNameError: Boolean = false,
        val emailAddress: String = "",
        val connected: Boolean = true,
        val userInfo: UiResource<User> = UiResource.Idle,
        val userUpdate: UiResource<Unit> = UiResource.Idle,
        val userSignOut: UiResource<Unit> = UiResource.Idle,
        val userDelete: UiResource<Unit> = UiResource.Idle
    )
}