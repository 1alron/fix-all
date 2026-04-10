package io.alron.fixall.presentation.service_centers.details

import io.alron.fixall.domain.model.Branch

data class ServiceCenterDetailsState(
    val isLoading: Boolean = false,
    val branch: Branch? = null,
    val errorMessage: String? = null
)