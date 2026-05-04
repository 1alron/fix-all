package io.alron.fixall.data.remote.api

import io.alron.fixall.data.remote.dto.*
import okhttp3.MultipartBody
import retrofit2.http.*

interface ProfileApi {
    @GET("/api/profile/me/")
    suspend fun getMe(): UserDto

    @PATCH("/api/profile/update_profile/")
    suspend fun updateProfile(@Body profile: Map<String, String?>): UpdateProfileResponseDto

    @Multipart
    @POST("/api/profile/upload_avatar/")
    suspend fun uploadAvatar(@Part avatar: MultipartBody.Part): UploadAvatarResponseDto

    @DELETE("/api/profile/delete_avatar/")
    suspend fun deleteAvatar()

    @GET("/api/profile/stats/")
    suspend fun getStats(): UserStatsDto

    @GET("/api/profile/loyalty/")
    suspend fun getLoyalty(): LoyaltyInfoDto
}
