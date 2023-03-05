package com.practise.secureauthentication.presentation.screens.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practise.secureauthentication.R
import com.practise.secureauthentication.data.RemoteResource
import com.practise.secureauthentication.data.network.ApiErrors
import com.practise.secureauthentication.domain.model.User
import com.practise.secureauthentication.domain.model.UserUpdate
import com.practise.secureauthentication.domain.usecases.UserUseCases
import com.practise.secureauthentication.presentation.core.connectivity.ConnectivityObserver
import com.practise.secureauthentication.presentation.core.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userUseCases: UserUseCases,
    private val networkObserver: ConnectivityObserver
): ViewModel() {

    var uiState by mutableStateOf(UiState())
        private set

    private val _informMessages = Channel<UiText>()
    val informMessages = _informMessages.receiveAsFlow()

    init {
        viewModelScope.launch {
            userUseCases.getUserInfoUseCase().collectLatest {
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
                uiState = uiState.copy(connected = it == ConnectivityObserver.Status.Available)
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            userUseCases.getUserInfoUseCase().lastOrNull()?.let {
                setUserInfo(it)
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

    private fun setUserInfo(resource: RemoteResource<User>) {
        uiState = uiState.copy(userInfo = resource)
    }

    private suspend fun splitFullName(name: String): List<String> {
        return withContext(Dispatchers.Default) {
            name.split(" ", limit = 2)
        }
    }

    fun deleteAccount(onSuccess: () -> Unit) {
        viewModelScope.launch {
            userUseCases.deleteUserUseCase().collectLatest {
                uiState = uiState.copy(userDelete = it)
                if(it is RemoteResource.Success)
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
                userUseCases.updateUserInfoUseCase(userUpdate).collectLatest {
                    uiState = uiState.copy(userUpdate = it)
                }
            }
        }
    }


    fun signOut(onSuccess: () -> Unit) {
        viewModelScope.launch {
            userUseCases.signOutUseCase().collectLatest {
                uiState = uiState.copy(userSignOut = it)
                if(it is RemoteResource.Success || it is RemoteResource.Failure && it.error == ApiErrors.UNAUTHORIZED)
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
        val userInfo: RemoteResource<User> = RemoteResource.Idle,
        val userUpdate: RemoteResource<Unit> = RemoteResource.Idle,
        val userSignOut: RemoteResource<Unit> = RemoteResource.Idle,
        val userDelete: RemoteResource<Unit> = RemoteResource.Idle
    )
}