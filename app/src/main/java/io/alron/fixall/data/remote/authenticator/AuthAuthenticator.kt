package io.alron.fixall.data.remote.authenticator

import io.alron.fixall.domain.AuthManager
import io.alron.fixall.domain.repository.AuthRepository
import io.alron.fixall.domain.repository.TokenStorageRepository
import jakarta.inject.Inject
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class AuthAuthenticator @Inject constructor(
    private val tokenStorage: TokenStorageRepository,
    private val authRepository: AuthRepository,
    private val authManager: AuthManager
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val refreshToken = runBlocking {
            tokenStorage.getRefreshToken()
        }

        if (refreshToken.isNullOrEmpty()) {
            runBlocking { authManager.logout() }
            return null
        }

        val newTokens = runBlocking {
            authRepository.refreshToken(refreshToken)
        }

        if (newTokens == null) {
            runBlocking { authManager.logout() }
            return null
        }

        return response.request.newBuilder()
            .header("Authorization", "Bearer ${newTokens.access}")
            .build()
    }
}