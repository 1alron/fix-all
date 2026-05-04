package io.alron.fixall.presentation.appointments

import io.alron.fixall.domain.model.Appointment

data class AppointmentsState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val appointments: List<Appointment> = emptyList(),
    val errorMessage: String? = null
)
