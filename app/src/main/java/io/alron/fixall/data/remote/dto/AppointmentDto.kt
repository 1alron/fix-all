package io.alron.fixall.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AppointmentDto(
    val id: String? = null,
    val car: AppointmentCarDto? = null,
    @SerializedName("service_type") val serviceType: AppointmentServiceDto? = null,
    @SerializedName("service_center") val serviceCenter: AppointmentServiceCenterDto? = null,
    @SerializedName("scheduled_date") val scheduledDate: String? = null,
    @SerializedName("scheduled_time") val scheduledTime: String? = null,
    @SerializedName("end_time") val endTime: String? = null,
    val status: String? = null,
    @SerializedName("status_display") val statusDisplay: String? = null,
    val notes: String? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null,
    @SerializedName("can_cancel") val canCancel: Boolean? = null,
    @SerializedName("total_price") val totalPrice: Any? = null,
    @SerializedName("payment_url") val paymentUrl: String? = null,
    @SerializedName("is_paid") val isPaid: Boolean? = null,
    @SerializedName("paid_at") val paidAt: String? = null,
    @SerializedName("payment_status") val paymentStatus: String? = null
)

data class AppointmentCarDto(
    val id: String? = null,
    @SerializedName("model_name") val modelName: String? = null,
    @SerializedName("brand_name") val brandName: String? = null,
    val year: Int? = null,
    @SerializedName("license_plate") val licensePlate: String? = null,
    val vin: String? = null,
    @SerializedName("photo_url") val photoUrl: String? = null
)

data class AppointmentServiceDto(
    val id: String? = null,
    val name: String? = null,
    val description: String? = null,
    val duration: Int? = null,
    val price: String? = null
)

data class AppointmentServiceCenterDto(
    val id: String? = null,
    val address: String? = null,
    val phone: String? = null,
    @SerializedName("opening_hours") val openingHours: String? = null,
    @SerializedName("photo_url") val photoUrl: String? = null
)

data class CreateAppointmentRequestDto(
    @SerializedName("car_id") val carId: String,
    @SerializedName("service_type_id") val serviceTypeId: String,
    @SerializedName("service_center_id") val serviceCenterId: String,
    @SerializedName("scheduled_date") val scheduledDate: String,
    @SerializedName("scheduled_time") val scheduledTime: String,
    val notes: String? = null
)

data class AvailableTimeSlotsDto(
    val date: String? = null,
    val slots: List<String>? = null,
    @SerializedName("working_hours") val workingHours: WorkingHoursRangeDto? = null
)

data class WorkingHoursRangeDto(
    val start: String? = null,
    val end: String? = null,
    @SerializedName("lunch_start") val lunchStart: String? = null,
    @SerializedName("lunch_end") val lunchEnd: String? = null
)

data class CancelAppointmentResponseDto(
    val success: Boolean? = null,
    val message: String? = null,
    val error: String? = null
)

data class PaymentRequestDto(
    @SerializedName("bonus_amount") val bonusAmount: Double
)

data class PaymentResponseDto(
    @SerializedName("payment_id") val paymentId: String? = null,
    @SerializedName("payment_url") val paymentUrl: String? = null,
    val amount: Double? = null,
    val status: String? = null
)

data class PaymentStatusDto(
    @SerializedName("payment_id") val paymentId: String? = null,
    val status: String? = null,
    @SerializedName("status_display") val statusDisplay: String? = null,
    @SerializedName("paid_at") val paidAt: String? = null,
    val amount: Double? = null,
    @SerializedName("appointment_status") val appointmentStatus: String? = null,
    @SerializedName("payment_url") val paymentUrl: String? = null,
    val message: String? = null
)

data class SyncPaymentStatusResponseDto(
    @SerializedName("is_paid") val isPaid: Boolean,
    @SerializedName("payment_status") val paymentStatus: String?
)
