package io.alron.fixall.domain.repository

import io.alron.fixall.domain.model.LoyaltyInfo
import io.alron.fixall.domain.model.User
import io.alron.fixall.domain.model.UserStats
import kotlinx.coroutines.flow.StateFlow
import okhttp3.MultipartBody

interface ProfileRepository {
    val currentUser: StateFlow<User?>
    
    suspend fun getMe(): Result<User>
    suspend fun updateProfile(
        username: String? = null,
        firstName: String? = null,
        lastName: String? = null,
        email: String? = null,
        phone: String? = null,
        address: String? = null
    ): Result<User>
    suspend fun uploadAvatar(avatar: MultipartBody.Part): Result<String>
    suspend fun deleteAvatar(): Result<Unit>
    suspend fun getStats(): Result<UserStats>
    suspend fun getLoyalty(): Result<LoyaltyInfo>
    
    fun clearCache()
}
