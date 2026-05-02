package io.alron.fixall.domain.model

data class AdminAppointmentDetail(
    val id: String,
    val scheduledDate: String,
    val scheduledTime: String,
    val endTime: String,
    val carInfo: String,
    val clientName: String?,
    val clientEmail: String?,
    val clientPhone: String?,
    val serviceName: String,
    val serviceDuration: Int,
    val centerAddress: String,
    val status: String,
    val statusDisplay: String,
    val notes: String?,
    val vin: String?,
    val totalPrice: Double,
    val isPaid: Boolean,
    val paymentStatus: String? = null,
    val paymentInfo: AdminPaymentInfo?,
    val createdAt: String,
    val updatedAt: String
)

data class AdminPaymentInfo(
    val status: String,
    val paidAt: String,
    val amount: Double
)

data class AddNoteResponse(
    val success: Boolean,
    val notes: String?
)
