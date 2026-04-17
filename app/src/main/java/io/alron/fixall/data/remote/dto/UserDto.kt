package io.alron.fixall.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UserDto(
    val id: Int,
    val username: String,
    val email: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("full_name") val fullName: String,
    val profile: UserProfileDto
)

data class UserProfileDto(
    val phone: String?,
    val address: String?,
    @SerializedName("avatar_url") val avatarUrl: String?
)

data class UpdateProfileResponseDto(
    val success: Boolean,
    val message: String?,
    val data: UserDto?,
    val errors: Map<String, List<String>>?
)

data class UploadAvatarResponseDto(
    val success: Boolean,
    val message: String?,
    @SerializedName("avatar_url") val avatarUrl: String?,
    val errors: Map<String, List<String>>?
)

data class UserStatsDto(
    @SerializedName("total_appointments") val totalAppointments: Int,
    @SerializedName("completed_appointments") val completedAppointments: Int,
    @SerializedName("cancelled_appointments") val cancelledAppointments: Int,
    @SerializedName("total_spent") val totalSpent: Double,
    @SerializedName("average_check") val averageCheck: Double,
    @SerializedName("first_visit") val firstVisit: String?,
    @SerializedName("last_visit") val lastVisit: String?,
    @SerializedName("visits_by_month") val visitsByMonth: Map<String, Int>,
    @SerializedName("top_services") val topServices: List<StatItemDto>,
    @SerializedName("top_centers") val topCenters: List<StatItemDto>,
    @SerializedName("by_weekday") val byWeekday: List<Int>,
    @SerializedName("by_hour") val byHour: List<Int>
)

data class StatItemDto(
    val name: String,
    val count: Int
)

data class LoyaltyInfoDto(
    val status: String,
    @SerializedName("status_display") val statusDisplay: String,
    @SerializedName("bonus_balance") val bonusBalance: Double,
    @SerializedName("total_spent") val totalSpent: Double,
    @SerializedName("next_status") val nextStatus: String?,
    @SerializedName("next_status_progress") val nextStatusProgress: Double,
    @SerializedName("personal_discount") val personalDiscount: Double,
    @SerializedName("status_discount") val statusDiscount: Double = 0.0,
    @SerializedName("total_discount") val totalDiscount: Double = 0.0
)
