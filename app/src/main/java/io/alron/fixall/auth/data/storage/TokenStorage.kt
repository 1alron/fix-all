package io.alron.fixall.auth.data.storage

import io.alron.fixall.auth.domain.model.AuthTokens

interface TokenStorage {
    suspend fun saveTokens(tokens: AuthTokens)
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun clear()
}