package io.alron.fixall.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AdminDashboardDto(
    @SerializedName("total_appointments") val totalAppointments: Int,
    @SerializedName("active_services") val activeServices: Int,
    @SerializedName("total_clients") val totalClients: Int,
    @SerializedName("total_centers") val totalCenters: Int,
    val upcoming: List<AdminAppointmentDto>
)

data class AdminAppointmentDto(
    val id: String,
    val date: String,
    val time: String,
    val client: String,
    val service: String,
    val car: String,
    val center: String,
    val status: String,
    @SerializedName("status_display") val statusDisplay: String
)
