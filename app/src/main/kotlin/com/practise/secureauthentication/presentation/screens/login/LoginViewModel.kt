package com.practise.secureauthentication.presentation.screens.login

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.identity.SignInClient
import com.practise.secureauthentication.domain.repository.KtorApiRepository
import com.practise.secureauthentication.domain.repository.SignInRepository
import com.practise.secureauthentication.domain.usecases.BeginSignWithGoogleUseCase
import com.practise.secureauthentication.presentation.core.connectivity.ConnectivityObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import domain.model.TokenId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@Suppress("unused")
private const val TAG = "LoginViewModel"
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signInRepository: SignInRepository,
    val oneTapClient: SignInClient,
    private val ktorApi: KtorApiRepository,
    private val connectivityObserver: ConnectivityObserver,
    private val beginSignWithGoogleUseCase: BeginSignWithGoogleUseCase
): ViewModel() {

    var uiState by mutableStateOf(UiState())
        private set

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

    fun saveSignedInState(signedIn: Boolean) {
        viewModelScope.launch {
            signInRepository.saveSignedInState(signedIn)
        }
    }

    fun signInWithGoogle(
        launcher: ActivityResultLauncher<IntentSenderRequest>,
        onCallerBlocked: () -> Unit,
        onNoCredentials: () -> Unit,
    ) {
        viewModelScope.launch {
            uiState = uiState.copy(loading = true)
            beginSignWithGoogleUseCase(oneTapClient)
                .onSuccess {
                    val intentSender = IntentSenderRequest.Builder(it.pendingIntent).build()
                    launcher.launch(intentSender)
                }.onFailure {
                    resetStates()
                    val isBlocked = detectBlockedCaller(it)
                    if(isBlocked) {
                        onCallerBlocked()
                    } else {
                        onNoCredentials()
                    }
                }
        }
    }

    fun resetStates() {
        uiState = UiState(connected = uiState.connected, signedIn = uiState.signedIn)
    }

    fun verifyTokenOnBackend(
        tokenId: TokenId,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                ktorApi.verifyToken(tokenId)
            }.onSuccess {
                onSuccess()
            }.onFailure {
                onFailure(it)
            }
        }
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
        val loading: Boolean = false,
    )
}

