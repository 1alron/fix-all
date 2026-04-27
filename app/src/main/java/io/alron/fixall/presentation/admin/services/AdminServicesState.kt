package io.alron.fixall.presentation.admin.services

import io.alron.fixall.domain.model.AdminService
import io.alron.fixall.domain.model.Branch

data class AdminServicesState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val services: List<AdminService> = emptyList(),
    val branches: List<Branch> = emptyList(),
    val error: String? = null,
    val centerId: String? = null,
    val isActiveOnly: Boolean = false,
    val search: String = ""
)
