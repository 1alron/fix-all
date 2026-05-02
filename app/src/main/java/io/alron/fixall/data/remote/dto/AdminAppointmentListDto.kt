package io.alron.fixall.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AdminAppointmentListDto(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<AdminAppointmentListItemDto>
)

data class AdminAppointmentListItemDto(
    val id: String,
    @SerializedName("scheduled_date") val scheduledDate: String,
    @SerializedName("scheduled_time") val scheduledTime: String,
    @SerializedName("car_info") val carInfo: String,
    @SerializedName("client_name") val clientName: String?,
    @SerializedName("client_email") val clientEmail: String?,
    @SerializedName("service_name") val serviceName: String,
    @SerializedName("center_address") val centerAddress: String,
    val status: String,
    @SerializedName("status_display") val statusDisplay: String,
    val notes: String?
)
