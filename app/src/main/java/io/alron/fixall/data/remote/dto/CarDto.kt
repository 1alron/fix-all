package io.alron.fixall.data.remote.dto

data class CarDto(
    val id: String,
    val model: String,
    val year: String,
    val license_plate: String,
    val vin: String,
    val photo: String?
)
