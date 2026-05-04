package io.alron.fixall.data.remote.mappers

import io.alron.fixall.data.remote.dto.CarBrandDto
import io.alron.fixall.data.remote.dto.CarDto
import io.alron.fixall.data.remote.dto.CarModelDto
import io.alron.fixall.domain.model.Car
import io.alron.fixall.domain.model.CarBrand
import io.alron.fixall.domain.model.CarModel

fun CarDto.toDomain() = Car(
    id = id,
    modelId = model,
    modelName = model_name,
    brandName = brand_name,
    year = year,
    licensePlate = license_plate.uppercase(),
    vin = vin?.takeIf { it.isNotBlank() },
    photo = photo,
    photoUrl = photo_url
)

fun CarBrandDto.toDomain() = CarBrand(
    id = id,
    name = name
)

fun CarModelDto.toDomain() = CarModel(
    id = id,
    name = name,
    brandId = brandId,
    brandName = brandName
)