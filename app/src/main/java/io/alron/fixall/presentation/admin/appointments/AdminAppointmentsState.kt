package io.alron.fixall.presentation.admin.appointments

import io.alron.fixall.domain.model.AdminAppointmentListItem
import io.alron.fixall.domain.model.Branch

data class AdminAppointmentsState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val appointments: List<AdminAppointmentListItem> = emptyList(),
    val branches: List<Branch> = emptyList(),
    val error: String? = null,
    val centerId: String? = null,
    val dateFrom: String? = null,
    val dateTo: String? = null,
    val status: String? = null,
    val search: String = ""
)
