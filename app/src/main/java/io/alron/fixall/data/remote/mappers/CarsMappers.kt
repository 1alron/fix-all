package io.alron.fixall.data.remote.mappers

import io.alron.fixall.data.remote.dto.CarDto
import io.alron.fixall.domain.model.Car

fun CarDto.toDomain() = Car(
    id = id,
    model = model,
    year = year,
    licensePlate = license_plate,
    vin = vin,
    photoUrl = photo
)