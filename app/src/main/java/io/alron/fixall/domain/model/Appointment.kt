package io.alron.fixall.domain.model

data class Appointment(
    val id: String,
    val car: AppointmentCar,
    val serviceType: AppointmentService,
    val serviceCenter: AppointmentServiceCenter,
    val scheduledDate: String,
    val scheduledTime: String,
    val endTime: String? = null,
    val status: String,
    val statusDisplay: String,
    val notes: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val canCancel: Boolean = false,
    val totalPrice: String
)

data class AppointmentCar(
    val id: String,
    val modelName: String,
    val brandName: String,
    val year: Int? = null,
    val licensePlate: String,
    val vin: String? = null,
    val photoUrl: String? = null
)

data class AppointmentService(
    val id: String,
    val name: String,
    val description: String? = null,
    val duration: Int,
    val price: String
)

data class AppointmentServiceCenter(
    val id: String,
    val address: String,
    val phone: String? = null,
    val openingHours: String? = null,
    val photoUrl: String? = null
)

data class TimeSlots(
    val date: String,
    val slots: List<String>,
    val workingHours: WorkingHoursRange
)

data class WorkingHoursRange(
    val start: String,
    val end: String,
    val lunchStart: String?,
    val lunchEnd: String?
)
