package io.alron.fixall.data.remote.mappers

import io.alron.fixall.data.remote.dto.CarDto
import io.alron.fixall.domain.model.Car

fun CarDto.toDomain() = Car(
    id = id,
    modelId = model,
    modelName = model_name,
    brandName = brand_name,
    year = year,
    licensePlate = license_plate,
    vin = vin,
    photo = photo,
    photoUrl = photo_url
)