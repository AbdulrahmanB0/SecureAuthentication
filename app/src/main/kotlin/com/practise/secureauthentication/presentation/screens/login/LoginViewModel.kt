package com.practise.secureauthentication.presentation.screens.login

import android.util.Log
import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.identity.SignInClient
import com.practise.secureauthentication.domain.model.Resource
import com.practise.secureauthentication.domain.model.TokenId
import com.practise.secureauthentication.domain.repository.KtorApiRepository
import com.practise.secureauthentication.domain.repository.OAuthRepository
import com.practise.secureauthentication.domain.repository.SignInRepository
import com.practise.secureauthentication.util.OneTapSignInResource
import com.practise.secureauthentication.util.TokenVerificationResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "LoginViewModel"
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signInRepository: SignInRepository,
    val oneTapClient: SignInClient,
    private val ktorApi: KtorApiRepository,
    private val authRepository: OAuthRepository
): ViewModel() {

    var uiState by mutableStateOf(UiState())
        private set

    val messageFlow = MutableSharedFlow<String>()


    init {
        viewModelScope.launch {
            val isSignedIn = signInRepository.readSignedInState().lastOrNull() ?: false
            uiState = uiState.copy(signedIn = isSignedIn)
        }
    }

    fun setEmailValue(value: String) {
        uiState = uiState.copy(email = value)
    }

    fun isEmail(value: String): Boolean = value.matches(Regex(Patterns.EMAIL_ADDRESS.pattern()))

    fun setPasswordValue(value: String) {
        uiState = uiState.copy(password = value)
    }

    fun saveSignedInState(signedIn: Boolean) {
        viewModelScope.launch {
            signInRepository.saveSignedInState(signedIn)
        }
    }

    fun emitMessage(message: String) {
        viewModelScope.launch { messageFlow.emit(message) }
    }

    fun setErrorState(isError: Boolean) {
        uiState = uiState.copy(error = isError)
    }

    fun onErrorOccurred(message: String) {
        setErrorState(true)
        resetOneTapState()
        emitMessage(message)
    }

    fun signInWithGoogle() {
        viewModelScope.launch {
            authRepository.signInWithGoogle().collectLatest {
                setOneTapSignInState(it)
            }
        }
    }

    fun resetOneTapState() = setOneTapSignInState(Resource.Idle)

    private fun setOneTapSignInState(oneTapSignInResource: OneTapSignInResource) {
        uiState = uiState.copy(oneTapSignInResource = oneTapSignInResource)
        Log.d(TAG, "setOneTapSignInState: NEW STATE - ${oneTapSignInResource::class.simpleName}")
    }

    fun verifyTokenOnBackend(tokenId: TokenId) {
        fun setTokenVerifiedState(resource: Resource<Unit>) {
            uiState = uiState.copy(tokenVerificationResource = resource)
        }
        setTokenVerifiedState(Resource.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                ktorApi.verifyToken(tokenId)
            }.onSuccess {
                setTokenVerifiedState(Resource.Success(Unit))
            }.onFailure {
                setTokenVerifiedState(Resource.Failure(it))
            }
        }
    }

    suspend fun detectBlockedCaller(throwable: Throwable): Boolean {
        return withContext(Dispatchers.Default) {
            val errorCode = throwable.message?.substringBefore(':')?.toIntOrNull()
            errorCode == 10 || errorCode == 16
        }
    }

    data class UiState(
        val email: String = "",
        val password: String = "",
        val signedIn: Boolean = false,
        val error: Boolean = false,
        val oneTapSignInResource: OneTapSignInResource = Resource.Idle,
        val tokenVerificationResource: TokenVerificationResource = Resource.Idle
    )
}

