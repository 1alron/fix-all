package io.alron.fixall.presentation.home

import io.alron.fixall.domain.model.Branch

data class HomeState(
    val branches: List<Branch> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
)