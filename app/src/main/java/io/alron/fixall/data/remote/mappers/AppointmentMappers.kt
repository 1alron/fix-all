package io.alron.fixall.data.remote.mappers

import io.alron.fixall.data.remote.dto.*
import io.alron.fixall.domain.model.*

fun AppointmentDto.toDomain() = Appointment(
    id = id ?: "",
    car = car?.toDomain() ?: AppointmentCar(
        id = "",
        modelName = "",
        brandName = "",
        licensePlate = ""
    ),
    serviceType = serviceType?.toDomain() ?: AppointmentService(
        id = "",
        name = "",
        duration = 0,
        price = "0"
    ),
    serviceCenter = serviceCenter?.toDomain() ?: AppointmentServiceCenter(id = "", address = ""),
    scheduledDate = formatDate(scheduledDate ?: ""),
    scheduledTime = formatTime(scheduledTime ?: ""),
    endTime = endTime,
    status = status ?: "",
    statusDisplay = statusDisplay ?: "",
    notes = notes,
    createdAt = createdAt,
    updatedAt = updatedAt,
    canCancel = canCancel ?: false,
    totalPrice = totalPrice?.toString() ?: "0",
    paymentUrl = paymentUrl,
    isPaid = isPaid ?: (paymentStatus == "succeeded"),
    paymentDate = paidAt
)

fun AppointmentCarDto.toDomain() = AppointmentCar(
    id = id ?: "",
    modelName = modelName ?: "",
    brandName = brandName ?: "",
    year = year,
    licensePlate = licensePlate ?: "",
    vin = vin,
    photoUrl = photoUrl
)

fun AppointmentServiceDto.toDomain() = AppointmentService(
    id = id ?: "",
    name = name ?: "",
    description = description,
    duration = duration ?: 0,
    price = price ?: "0"
)

fun AppointmentServiceCenterDto.toDomain() = AppointmentServiceCenter(
    id = id ?: "",
    address = address ?: "",
    phone = phone,
    openingHours = openingHours,
    photoUrl = photoUrl
)

fun AvailableTimeSlotsDto.toDomain() = TimeSlots(
    date = date ?: "",
    slots = slots ?: emptyList(),
    workingHours = workingHours?.toDomain() ?: WorkingHoursRange("", "", null, null)
)

fun WorkingHoursRangeDto.toDomain() = WorkingHoursRange(
    start = start ?: "",
    end = end ?: "",
    lunchStart = lunchStart,
    lunchEnd = lunchEnd
)

private fun formatDate(dateString: String): String {
    if (dateString.isBlank()) return ""
    return try {
        val parts = dateString.split("-")
        if (parts.size == 3) {
            "${parts[2]}.${parts[1]}.${parts[0]}"
        } else {
            dateString
        }
    } catch (e: Exception) {
        dateString
    }
}

private fun formatTime(timeString: String): String {
    if (timeString.isBlank()) return ""
    return try {
        val parts = timeString.split(":")
        if (parts.size >= 2) {
            "${parts[0]}:${parts[1]}"
        } else {
            timeString
        }
    } catch (e: Exception) {
        timeString
    }
}
