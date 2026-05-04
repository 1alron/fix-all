package io.alron.fixall.domain.model

data class AdminDashboardStats(
    val totalAppointments: Int,
    val activeServices: Int,
    val totalClients: Int,
    val totalCenters: Int,
    val upcoming: List<AdminAppointment>
)

data class AdminAppointment(
    val id: String,
    val date: String,
    val time: String,
    val client: String,
    val service: String,
    val car: String,
    val center: String,
    val status: String,
    val statusDisplay: String
)
