package com.practise.data.repository

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.practise.core.Constants
import com.practise.domain.repository.OAuthRepository

class OAuthRepositoryImpl : OAuthRepository {

    override suspend fun verifyGoogleTokenId(token: String): GoogleIdToken? {
        val verifier = GoogleIdTokenVerifier.Builder(NetHttpTransport(), GsonFactory())
            .setAudience(listOf(System.getenv("google_oauth_client")))
            .setIssuer(Constants.GOOGLE_ISSUER)
            .build()
        return verifier.runCatching {
            verify(token)
        }.getOrNull()
    }
}
