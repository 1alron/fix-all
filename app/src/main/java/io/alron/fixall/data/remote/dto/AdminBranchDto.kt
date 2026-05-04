package io.alron.fixall.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AdminBranchListItemDto(
    val id: String,
    val address: String,
    val phone: String,
    @SerializedName("opening_hours") val openingHours: String,
    @SerializedName("photo_url") val photoUrl: String?,
    val photo: String?,
    @SerializedName("services_count") val servicesCount: Int,
    @SerializedName("working_hours") val workingHours: List<AdminWorkingHourDto>? = null
)

data class AdminWorkingHourDto(
    val id: Int? = null,
    @SerializedName("day_of_week") val dayOfWeek: Int,
    @SerializedName("day_display") val dayDisplay: String? = null,
    @SerializedName("start_time") val startTime: String,
    @SerializedName("end_time") val endTime: String,
    @SerializedName("lunch_start") val lunchStart: String?,
    @SerializedName("lunch_end") val lunchEnd: String?,
    @SerializedName("is_working") val isWorking: Boolean
)

data class CreateUpdateBranchRequestDto(
    val address: String,
    val phone: String,
    @SerializedName("opening_hours") val openingHours: String
)

data class UpdatePhotoResponseDto(
    val success: Boolean,
    @SerializedName("photo_url") val photoUrl: String
)

data class SetWorkingHoursRequestDto(
    @SerializedName("day_of_week") val dayOfWeek: Int,
    @SerializedName("start_time") val startTime: String,
    @SerializedName("end_time") val endTime: String,
    @SerializedName("lunch_start") val lunchStart: String?,
    @SerializedName("lunch_end") val lunchEnd: String?,
    @SerializedName("is_working") val isWorking: Boolean
)

data class SetWorkingHoursResponseDto(
    val success: Boolean,
    val message: String?,
    val data: AdminWorkingHourDto?
)
