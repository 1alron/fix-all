package io.alron.fixall.presentation.admin.appointments.details

import io.alron.fixall.domain.model.AdminAppointmentDetail

data class AdminAppointmentDetailState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val appointment: AdminAppointmentDetail? = null,
    val error: String? = null,
    val isUpdatingStatus: Boolean = false,
    val isAddingNote: Boolean = false
)
