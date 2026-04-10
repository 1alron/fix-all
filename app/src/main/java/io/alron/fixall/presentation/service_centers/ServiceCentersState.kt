package io.alron.fixall.presentation.service_centers

import io.alron.fixall.domain.model.Branch

data class ServiceCentersState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val branches: List<Branch> = emptyList(),
    val errorMessage: String? = null
)