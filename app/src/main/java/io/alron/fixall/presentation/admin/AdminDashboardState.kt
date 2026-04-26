package io.alron.fixall.presentation.admin

import io.alron.fixall.domain.model.AdminDashboardStats

data class AdminDashboardState(
    val isLoading: Boolean = false,
    val stats: AdminDashboardStats? = null,
    val error: String? = null
)
