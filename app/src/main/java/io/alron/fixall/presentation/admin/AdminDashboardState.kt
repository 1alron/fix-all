package io.alron.fixall.presentation.admin

import io.alron.fixall.domain.model.AdminDashboardStats
import io.alron.fixall.domain.model.AdminStatusStats

data class AdminDashboardState(
    val isLoading: Boolean = false,
    val stats: AdminDashboardStats? = null,
    val statusStats: AdminStatusStats? = null,
    val isLoadingStatuses: Boolean = false,
    val selectedPeriod: String = "week",
    val error: String? = null
)
