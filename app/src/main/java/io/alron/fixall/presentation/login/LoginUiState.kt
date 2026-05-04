package io.alron.fixall.presentation.login

import io.alron.fixall.presentation.util.UiText

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val networkError: UiText? = null,
    val usernameError: UiText? = null,
    val passwordError: UiText? = null,
)