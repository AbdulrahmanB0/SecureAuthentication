package com.practise.domain.repository

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken

interface OAuthRepository {

    suspend fun verifyGoogleTokenId(token: String): GoogleIdToken?
}
