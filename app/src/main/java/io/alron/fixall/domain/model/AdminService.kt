package io.alron.fixall.domain.model

data class AdminService(
    val id: String,
    val name: String,
    val description: String?,
    val duration: Int,
    val price: String,
    val serviceCenterId: String,
    val centerAddress: String?,
    val isActive: Boolean
)
