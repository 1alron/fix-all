package io.alron.fixall.data.remote.dto

data class AdminServicePopularityDto(
    val period: String,
    val services: List<ServicePopularityItemDto>
)

data class ServicePopularityItemDto(
    val name: String,
    val count: Int,
    val percentage: Double
)
