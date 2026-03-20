package io.alron.fixall.domain.model

data class Branch(
    val id: String,
    val address: String,
    val phone: String,
    val openingHours: String,
    val photoUrl: String
)