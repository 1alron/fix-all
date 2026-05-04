package io.alron.fixall.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AdminClientListDto(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<AdminClientListItemDto>
)

data class AdminClientListItemDto(
    val id: Int,
    val username: String,
    @SerializedName("full_name") val fullName: String,
    val email: String,
    val phone: String?,
    val address: String?,
    @SerializedName("cars_count") val carsCount: Int,
    @SerializedName("appointments_count") val appointmentsCount: Int,
    @SerializedName("active_appointments_count") val activeAppointmentsCount: Int,
    @SerializedName("date_joined") val dateJoined: String,
    @SerializedName("is_staff") val isStaff: Boolean
)

data class AdminClientDetailDto(
    val id: Int,
    val username: String,
    @SerializedName("full_name") val fullName: String,
    val email: String,
    val phone: String?,
    val address: String?,
    @SerializedName("date_joined") val dateJoined: String,
    @SerializedName("is_staff") val isStaff: Boolean,
    @SerializedName("cars_count") val carsCount: Int,
    @SerializedName("appointments_count") val appointmentsCount: Int,
    @SerializedName("total_paid") val totalPaid: Double,
    val cars: List<AdminClientCarDto>,
    @SerializedName("recent_appointments") val recentAppointments: List<AdminClientAppointmentDto>,
    @SerializedName("top_services") val topServices: List<AdminClientStatItemDto>,
    @SerializedName("top_centers") val topCenters: List<AdminClientStatItemDto>,
    @SerializedName("weekday_counts") val weekdayCounts: List<Int>,
    @SerializedName("hour_counts") val hourCounts: List<Int>
)

data class AdminClientCarDto(
    val id: String,
    val name: String,
    @SerializedName("license_plate") val licensePlate: String,
    val year: Int
)

data class AdminClientAppointmentDto(
    val id: String,
    @SerializedName("scheduled_date") val scheduledDate: String,
    @SerializedName("scheduled_time") val scheduledTime: String,
    @SerializedName("service_name") val serviceName: String,
    @SerializedName("center_address") val centerAddress: String,
    val status: String,
    @SerializedName("status_display") val statusDisplay: String
)

data class AdminClientStatItemDto(
    val name: String,
    val count: Int
)

data class CreateClientRequestDto(
    val username: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    val email: String,
    val password: String?,
    @SerializedName("password_confirm") val passwordConfirm: String?,
    val phone: String?,
    val address: String?,
    @SerializedName("is_staff") val isStaff: Boolean
)

data class UpdateClientRequestDto(
    val username: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    val email: String,
    val phone: String?,
    val address: String?,
    @SerializedName("is_staff") val isStaff: Boolean
)

data class SuccessResponseDto(
    val success: Boolean,
    val message: String? = null
)

data class AdminClientErrorResponseDto(
    val errors: Map<String, String>
)
