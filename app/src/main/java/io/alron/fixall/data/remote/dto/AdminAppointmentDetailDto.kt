package io.alron.fixall.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AdminAppointmentDetailDto(
    val id: String,
    @SerializedName("scheduled_date") val scheduledDate: String,
    @SerializedName("scheduled_time") val scheduledTime: String,
    @SerializedName("end_time") val endTime: String,
    @SerializedName("car_info") val carInfo: String,
    @SerializedName("client_name") val clientName: String?,
    @SerializedName("client_email") val clientEmail: String?,
    @SerializedName("client_phone") val clientPhone: String?,
    @SerializedName("service_name") val serviceName: String,
    @SerializedName("service_duration") val serviceDuration: Int,
    @SerializedName("center_address") val centerAddress: String,
    val status: String,
    @SerializedName("status_display") val statusDisplay: String,
    val notes: String?,
    val vin: String?,
    @SerializedName("total_price") val totalPrice: Double,
    @SerializedName("is_paid") val isPaid: Boolean,
    @SerializedName("payment_status") val paymentStatus: String?,
    @SerializedName("payment_info") val paymentInfo: AdminPaymentInfoDto?,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)

data class AdminPaymentInfoDto(
    val status: String,
    @SerializedName("paid_at") val paidAt: String,
    val amount: Double
)

data class ChangeStatusRequestDto(
    val status: String
)

data class AddNoteRequestDto(
    val note: String
)

data class AddNoteResponseDto(
    val success: Boolean,
    val notes: String?
)
