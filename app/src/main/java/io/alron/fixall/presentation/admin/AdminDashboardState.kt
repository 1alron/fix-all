package io.alron.fixall.presentation.admin

import io.alron.fixall.domain.model.AdminAttendanceStats
import io.alron.fixall.domain.model.AdminDashboardStats
import io.alron.fixall.domain.model.AdminStatusStats

data class AdminDashboardState(
    val isLoading: Boolean = false,
    val stats: AdminDashboardStats? = null,
    val statusStats: AdminStatusStats? = null,
    val attendanceStats: AdminAttendanceStats? = null,
    val isLoadingStatuses: Boolean = false,
    val isLoadingAttendance: Boolean = false,
    val selectedPeriod: String = "week",
    val selectedAttendancePeriod: String = "week",
    val error: String? = null
)
