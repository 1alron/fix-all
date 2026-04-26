package io.alron.fixall.domain.model

data class AdminServicePopularity(
    val period: String,
    val services: List<ServicePopularityItem>
)

data class ServicePopularityItem(
    val name: String,
    val count: Int,
    val percentage: Double
)
