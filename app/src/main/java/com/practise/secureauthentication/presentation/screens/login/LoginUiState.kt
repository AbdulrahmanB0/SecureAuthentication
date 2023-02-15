package com.practise.secureauthentication.presentation.screens.login

import com.practise.secureauthentication.domain.model.Resource
import com.practise.secureauthentication.util.OneTapSignInResource
import com.practise.secureauthentication.util.TokenVerificationResource

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val signedIn: Boolean = false,
    val error: Boolean = false,
    val oneTapSignInResource: OneTapSignInResource = Resource.Idle,
    val tokenVerificationResource: TokenVerificationResource = Resource.Idle
)