package io.alron.fixall.auth.domain.repository

import io.alron.fixall.auth.domain.model.AuthTokens
import io.alron.fixall.auth.domain.model.LoginResult

interface LoginRepository {
    suspend fun login(username: String, password: String): LoginResult
    suspend fun refreshToken(refreshToken: String): AuthTokens
}