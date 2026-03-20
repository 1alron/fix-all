package io.alron.fixall.domain.repository

import io.alron.fixall.domain.model.AuthTokens

interface TokenStorageRepository {
    suspend fun saveTokens(tokens: AuthTokens)
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun clear()
}