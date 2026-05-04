package io.alron.fixall.presentation.registration

import io.alron.fixall.presentation.util.UiText

data class RegistrationUiState(
    val step: Int = 1,
    val firstName: String = "",
    val lastName: String = "",
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val firstNameError: UiText? = null,
    val lastNameError: UiText? = null,
    val usernameError: UiText? = null,
    val emailError: UiText? = null,
    val passwordError: UiText? = null,
    val confirmPasswordError: UiText? = null,
    val networkError: UiText? = null,
    val isCheckedAgreement: Boolean = false
)