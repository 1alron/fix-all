package io.alron.fixall.presentation.admin.clients.add_edit

data class AdminAddEditClientState(
    val username: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val passwordConfirm: String = "",
    val phone: String = "",
    val address: String = "",
    val isStaff: Boolean = false,
    
    val isLoading: Boolean = false,
    val error: String? = null,
    val formErrors: Map<String, String> = emptyMap(),
    val isSuccess: Boolean = false
)
