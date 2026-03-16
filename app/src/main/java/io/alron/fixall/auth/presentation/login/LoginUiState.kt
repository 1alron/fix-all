package io.alron.fixall.auth.presentation.login

import io.alron.fixall.auth.presentation.util.UiText

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val networkError: UiText? = null,
    val usernameError: UiText? = null,
    val passwordError: UiText? = null,
)