package io.alron.fixall.domain.repository

import io.alron.fixall.domain.model.AuthTokens
import io.alron.fixall.domain.model.LoginResult
import io.alron.fixall.domain.model.RegisterResult

interface AuthRepository {
    suspend fun login(username: String, password: String): LoginResult
    suspend fun register(
        username: String,
        firstName: String,
        lastName: String,
        email: String,
        password1: String,
        password2: String
    ): RegisterResult
    suspend fun refreshToken(refreshToken: String): AuthTokens?
}