package io.alron.fixall.domain.model

data class Car(
    val id: String,
    val model: String,
    val year: String,
    val licensePlate: String,
    val vin: String,
    val photoUrl: String?
)