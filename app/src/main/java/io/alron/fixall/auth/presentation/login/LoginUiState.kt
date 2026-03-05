package io.alron.fixall.auth.presentation.login

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val networkErrorResId: Int? = null,
    val usernameErrorResId: Int? = null,
    val passwordErrorResId: Int? = null,
)