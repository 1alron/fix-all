package io.alron.fixall.presentation.admin.appointments

import io.alron.fixall.domain.model.AdminAppointmentListItem

data class AdminAppointmentsState(
    val isLoading: Boolean = false,
    val appointments: List<AdminAppointmentListItem> = emptyList(),
    val error: String? = null,
    
    // Filters
    val centerId: String? = null,
    val date: String? = null,
    val status: String? = null,
    val search: String = ""
)
