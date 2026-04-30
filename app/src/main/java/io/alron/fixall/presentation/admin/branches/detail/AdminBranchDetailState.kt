package io.alron.fixall.presentation.admin.branches.detail

import io.alron.fixall.domain.model.*

data class AdminBranchDetailState(
    val branch: AdminBranch? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,

    val selectedDate: String = "",
    val selectedDateDisplay: String = "",
    val appointments: List<AdminAppointmentListItem> = emptyList(),
    val isLoadingAppointments: Boolean = false,

    val statusStats: AdminStatusStats? = null,
    val isLoadingStatusStats: Boolean = false,
    val statusPeriod: String = "week",

    val servicePopularity: AdminServicePopularity? = null,
    val isLoadingPopularity: Boolean = false,
    val popularityPeriod: String = "month",

    val attendanceStats: BranchAttendanceStats? = null,
    val isLoadingAttendance: Boolean = false,
    val attendancePeriod: String = "week"
)
