package io.alron.fixall.domain.model

data class AdminDashboardStats(
    val totalAppointments: Int,
    val activeServices: Int,
    val totalClients: Int,
    val totalCenters: Int
)
