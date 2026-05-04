package io.alron.fixall.presentation.admin.clients.detail

import io.alron.fixall.domain.model.AdminClientDetail

data class AdminClientDetailState(
    val client: AdminClientDetail? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    
    val isEditMode: Boolean = false,
    val editUsername: String = "",
    val editFirstName: String = "",
    val editLastName: String = "",
    val editEmail: String = "",
    val editPhone: String = "",
    val editAddress: String = "",
    val editIsStaff: Boolean = false,
    
    val formErrors: Map<String, String> = emptyMap()
)
