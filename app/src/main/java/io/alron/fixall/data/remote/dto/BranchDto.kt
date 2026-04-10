package io.alron.fixall.data.remote.dto

import com.google.gson.annotations.SerializedName

data class BranchDto(
    val id: String,
    val address: String,
    val phone: String,
    @SerializedName("opening_hours") val openingHours: String,
    @SerializedName("photo_url") val photoUrl: String,
    val services: List<ServiceDto>? = null,
    @SerializedName("working_hours") val workingHours: List<WorkingHourDto>? = null,
    @SerializedName("reviews_count") val reviewsCount: Int? = null,
    @SerializedName("average_rating") val averageRating: Double? = null,
    @SerializedName("latest_reviews") val latestReviews: List<ReviewDto>? = null,
    @SerializedName("is_open_now") val isOpenNow: Boolean? = null
)

data class ServiceDto(
    val id: String,
    val name: String,
    val description: String,
    val duration: Int,
    val price: String,
    @SerializedName("is_active") val isActive: Boolean
)

data class WorkingHourDto(
    val id: Int,
    @SerializedName("day_of_week") val dayOfWeek: Int,
    @SerializedName("day_of_week_display") val dayOfWeekDisplay: String,
    @SerializedName("start_time") val startTime: String,
    @SerializedName("end_time") val endTime: String,
    @SerializedName("lunch_start") val lunchStart: String?,
    @SerializedName("lunch_end") val lunchEnd: String?,
    @SerializedName("is_working") val isWorking: Boolean
)

data class ReviewDto(
    val id: String,
    @SerializedName("user_name") val userName: String,
    @SerializedName("user_avatar") val userAvatar: String?,
    val rating: Int,
    val comment: String,
    @SerializedName("admin_reply") val adminReply: String?,
    @SerializedName("created_at") val createdAt: String
)
