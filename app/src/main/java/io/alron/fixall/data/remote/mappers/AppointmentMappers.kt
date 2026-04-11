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
    scheduledDate = scheduledDate ?: "",
    scheduledTime = scheduledTime ?: "",
    endTime = endTime,
    status = status ?: "",
    statusDisplay = statusDisplay ?: "",
    notes = notes,
    createdAt = createdAt,
    updatedAt = updatedAt,
    canCancel = canCancel ?: false,
    totalPrice = totalPrice?.toString() ?: "0"
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
