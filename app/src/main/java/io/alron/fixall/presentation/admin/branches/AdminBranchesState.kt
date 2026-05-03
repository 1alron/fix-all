package io.alron.fixall.presentation.admin.branches

import io.alron.fixall.domain.model.AdminBranch

data class AdminBranchesState(
    val branches: List<AdminBranch> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null
)
