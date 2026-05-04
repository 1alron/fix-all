package io.alron.fixall.data.remote.dto

data class CarDto(
    val id: String,
    val model: String,
    val model_name: String,
    val brand_name: String,
    val year: Int,
    val license_plate: String,
    val vin: String?,
    val photo: String?,
    val photo_url: String?
)
