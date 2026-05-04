package io.alron.fixall.data.remote.authenticator

import io.alron.fixall.domain.AuthManager
import io.alron.fixall.domain.model.AuthTokens
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
        if (responseCount(response) >= 2) return null

        val refreshToken = tokenStorage.getRefreshToken()

        if (refreshToken.isNullOrEmpty()) {
            authManager.logout()
            return null
        }

        val newTokens = runBlocking {
            authRepository.refreshToken(refreshToken)
        } ?: run {
            authManager.logout()
            return null
        }

        tokenStorage.saveTokens(AuthTokens(newTokens.access, newTokens.refresh))

        return response.request.newBuilder()
            .header("Authorization", "Bearer ${newTokens.access}")
            .build()
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}