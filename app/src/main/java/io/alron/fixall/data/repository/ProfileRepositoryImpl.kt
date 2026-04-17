package io.alron.fixall.data.repository

import io.alron.fixall.data.remote.api.ProfileApi
import io.alron.fixall.data.remote.mappers.toDomain
import io.alron.fixall.domain.model.LoyaltyInfo
import io.alron.fixall.domain.model.User
import io.alron.fixall.domain.model.UserStats
import io.alron.fixall.domain.repository.ProfileRepository
import okhttp3.MultipartBody
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val api: ProfileApi
) : ProfileRepository {

    override suspend fun getMe(): Result<User> {
        return try {
            val userDto = api.getMe()
            Result.success(userDto.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateProfile(
        username: String?,
        firstName: String?,
        lastName: String?,
        email: String?,
        phone: String?,
        address: String?
    ): Result<User> {
        return try {
            val updateMap = mutableMapOf<String, String?>()
            username?.let { updateMap["username"] = it }
            firstName?.let { updateMap["first_name"] = it }
            lastName?.let { updateMap["last_name"] = it }
            email?.let { updateMap["email"] = it }
            phone?.let { updateMap["phone"] = it }
            address?.let { updateMap["address"] = it }
            
            val response = api.updateProfile(updateMap)
            if (response.success && response.data != null) {
                Result.success(response.data.toDomain())
            } else {
                Result.failure(Exception(response.message ?: "Failed to update profile"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadAvatar(avatar: MultipartBody.Part): Result<String> {
        return try {
            val response = api.uploadAvatar(avatar)
            if (response.success && response.avatarUrl != null) {
                Result.success(response.avatarUrl)
            } else {
                Result.failure(Exception(response.message ?: "Failed to upload avatar"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteAvatar(): Result<Unit> {
        return try {
            api.deleteAvatar()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getStats(): Result<UserStats> {
        return try {
            val statsDto = api.getStats()
            Result.success(statsDto.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getLoyalty(): Result<LoyaltyInfo> {
        return try {
            val loyaltyDto = api.getLoyalty()
            Result.success(loyaltyDto.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
