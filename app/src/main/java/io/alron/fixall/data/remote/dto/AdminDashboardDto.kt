package io.alron.fixall.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AdminDashboardDto(
    @SerializedName("total_appointments") val totalAppointments: Int,
    @SerializedName("active_services") val activeServices: Int,
    @SerializedName("total_clients") val totalClients: Int,
    @SerializedName("total_centers") val totalCenters: Int
)
