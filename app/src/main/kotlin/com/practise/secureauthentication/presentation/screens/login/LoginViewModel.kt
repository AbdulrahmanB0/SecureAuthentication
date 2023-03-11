package com.practise.secureauthentication.presentation.screens.login

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.identity.SignInClient
import com.practise.secureauthentication.R
import com.practise.secureauthentication.domain.repository.AuthRepository
import com.practise.secureauthentication.domain.repository.NetworkStatusRepository
import com.practise.secureauthentication.domain.usecases.BeginSignWithGoogleUseCase
import com.practise.secureauthentication.presentation.model.UiResource
import com.practise.secureauthentication.presentation.model.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import domain.model.TokenId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.reflect.*

@Suppress("unused")
private const val TAG = "LoginViewModel"
@HiltViewModel
class LoginViewModel @Inject constructor(
    val oneTapClient: SignInClient,
    private val authRepo: AuthRepository,
    private val connectivityObserver: NetworkStatusRepository,
    private val beginSignWithGoogleUseCase: BeginSignWithGoogleUseCase
): ViewModel() {

    var uiState by mutableStateOf(UiState())
        private set

    private val _messages = Channel<UiText>()
    val messages = _messages.receiveAsFlow()

    init {
        viewModelScope.launch {
            connectivityObserver.observe().collectLatest {
                uiState = uiState.copy(connected = it == NetworkStatusRepository.Status.Available)
            }
        }
    }



    fun launchSignInWithGoogleIntent(
        launcher: ActivityResultLauncher<IntentSenderRequest>,
    ) {
        viewModelScope.launch {
            setSignInUiResource(UiResource.Loading)
            beginSignWithGoogleUseCase()
                .onSuccess {
                    val intentSender = IntentSenderRequest.Builder(it.pendingIntent).build()
                    launcher.launch(intentSender)
                }.onFailure {
                    val isBlocked = detectBlockedCaller(it)
                    val messageId = if(isBlocked) {
                        R.string.caller_blocked
                    } else {
                        R.string.account_not_found
                    }
                    _messages.send(UiText.StringResource(messageId))
                }
        }
    }
    fun signInWithGoogle(tokenId: TokenId) {
        viewModelScope.launch {
            UiResource.from { authRepo.signInWithGoogle(tokenId) }.collect {
                setSignInUiResource(it)
            }
        }
    }

    fun setSignInUiResource(it: UiResource<Unit>) {
        uiState = uiState.copy(signInUiResource = it)
    }

    private suspend fun detectBlockedCaller(throwable: Throwable): Boolean {
        return withContext(Dispatchers.Default) {
            val errorCode = throwable.message?.substringBefore(':')?.toIntOrNull()
            errorCode == 10 || errorCode == 16
        }
    }

    data class UiState(
        val connected: Boolean = false,
        val signInUiResource: UiResource<Unit> = UiResource.Idle,
    )
}

