package io.alron.fixall.domain.repository

import io.alron.fixall.domain.model.AuthTokens

interface TokenStorageRepository {
    fun saveTokens(tokens: AuthTokens)
    fun getAccessToken(): String?
    fun getRefreshToken(): String?
    fun clear()
}