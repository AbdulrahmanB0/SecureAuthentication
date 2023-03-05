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
import com.practise.secureauthentication.data.RemoteResource
import com.practise.secureauthentication.domain.repository.CacheRepository
import com.practise.secureauthentication.domain.usecases.BeginSignWithGoogleUseCase
import com.practise.secureauthentication.domain.usecases.VerifyUserTokenUseCase
import com.practise.secureauthentication.presentation.core.connectivity.ConnectivityObserver
import com.practise.secureauthentication.presentation.core.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import domain.model.TokenId
import io.ktor.resources.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.reflect.*

@Suppress("unused")
private const val TAG = "LoginViewModel"
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signInRepository: CacheRepository,
    val oneTapClient: SignInClient,
    private val verifyUserTokenUseCase: VerifyUserTokenUseCase,
    private val connectivityObserver: ConnectivityObserver,
    private val beginSignWithGoogleUseCase: BeginSignWithGoogleUseCase
): ViewModel() {

    var uiState by mutableStateOf(UiState())
        private set

    private val _messages = Channel<UiText>()
    val messages = _messages.receiveAsFlow()

    init {
        viewModelScope.launch {
            val isSignedIn = signInRepository.readSignedInState().lastOrNull() ?: false
            uiState = uiState.copy(signedIn = isSignedIn)
        }

        viewModelScope.launch {
            connectivityObserver.observe().collectLatest {
                uiState = uiState.copy(connected = it == ConnectivityObserver.Status.Available)
            }
        }
    }



    fun signInWithGoogle(
        launcher: ActivityResultLauncher<IntentSenderRequest>,
    ) {
        viewModelScope.launch {
            setSignInResource(RemoteResource.Loading)
            beginSignWithGoogleUseCase(oneTapClient)
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
    fun verifyTokenOnBackend(tokenId: TokenId) {
        viewModelScope.launch {
            verifyUserTokenUseCase(tokenId).collectLatest {
                setSignInResource(it)
            }
        }
    }

    fun setSignInResource(it: RemoteResource<Unit>) {
        uiState = uiState.copy(signInResource = it)
    }

    private suspend fun detectBlockedCaller(throwable: Throwable): Boolean {
        return withContext(Dispatchers.Default) {
            val errorCode = throwable.message?.substringBefore(':')?.toIntOrNull()
            errorCode == 10 || errorCode == 16
        }
    }

    data class UiState(
        val signedIn: Boolean = false,
        val connected: Boolean = false,
        val signInResource: RemoteResource<Unit> = RemoteResource.Idle,
    )
}

