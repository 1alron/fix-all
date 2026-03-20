package io.alron.fixall.data.repository

import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import io.alron.fixall.domain.model.AuthTokens
import io.alron.fixall.domain.repository.TokenStorageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TokenStorageRepositoryImpl(private val context: Context) : TokenStorageRepository {
    private val sharedPrefs by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        EncryptedSharedPreferences.create(
            context,
            "auth_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences
                .PrefValueEncryptionScheme.AES256_GCM
        )
    }

    override suspend fun saveTokens(tokens: AuthTokens) {
        sharedPrefs.edit {
            putString("access_token", tokens.access)
            putString("refresh_token", tokens.refresh)
        }
    }

    override suspend fun getAccessToken(): String? =
        sharedPrefs.getString("access_token", null)


    override suspend fun getRefreshToken(): String? =
        sharedPrefs.getString("refresh_token", null)

    override suspend fun clear() {
        sharedPrefs.edit { clear() }
    }
}