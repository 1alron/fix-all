package io.alron.fixall.domain.model

data class AdminAppointmentListItem(
    val id: String,
    val scheduledDate: String,
    val scheduledTime: String,
    val carInfo: String,
    val clientName: String?,
    val clientEmail: String?,
    val serviceName: String,
    val centerAddress: String,
    val status: String,
    val statusDisplay: String,
    val notes: String?
)
