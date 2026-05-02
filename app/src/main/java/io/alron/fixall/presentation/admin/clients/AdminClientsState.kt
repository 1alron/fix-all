package io.alron.fixall.presentation.admin.clients

import io.alron.fixall.data.remote.dto.AdminClientListItemDto
import io.alron.fixall.domain.model.AdminBranch

data class AdminClientsState(
    val clients: List<AdminClientListItemDto> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    
    val search: String = "",
    val email: String = "",
    val phone: String = "",
    val hasCars: Boolean = false,
    val hasActive: Boolean = false,
    val dateFrom: String? = null,
    val dateTo: String? = null,
    
    val totalCount: Int = 0
)
