package io.alron.fixall.domain.model

data class Service(
    val id: String,
    val name: String,
    val description: String,
    val duration: Int,
    val price: String,
    val isActive: Boolean
)