package io.alron.fixall.domain.model

data class Car(
    val id: String,
    val modelId: String,
    val modelName: String,
    val brandName: String,
    val year: Int,
    val licensePlate: String,
    val vin: String?,
    val photo: String?,
    val photoUrl: String?
)