package io.alron.fixall.auth.presentation.registration

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.alron.fixall.R
import io.alron.fixall.auth.domain.model.RegisterResult
import io.alron.fixall.auth.domain.usecase.RegisterUseCase
import io.alron.fixall.auth.presentation.util.UiText
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(RegistrationUiState())
    val uiState: StateFlow<RegistrationUiState> = _uiState.asStateFlow()

    fun updateFirstName(value: String) {
        _uiState.value = _uiState.value.copy(
            firstName = value,
            firstNameError = null
        )
    }

    fun updateLastName(value: String) {
        _uiState.value = _uiState.value.copy(
            lastName = value,
            lastNameError = null
        )
    }

    fun updateUsername(value: String) {
        _uiState.value = _uiState.value.copy(
            username = value,
            usernameError = null
        )
    }

    fun updateEmail(value: String) {
        _uiState.value = _uiState.value.copy(
            email = value,
            emailError = null
        )
    }

    fun updatePassword(value: String) {
        _uiState.value = _uiState.value.copy(
            password = value,
            passwordError = null
        )
    }

    fun updateConfirmPassword(value: String) {
        _uiState.value = _uiState.value.copy(
            confirmPassword = value,
            confirmPasswordError = null
        )
    }

    fun onNextStep() {
        if (validateStep1()) {
            _uiState.value = _uiState.value.copy(step = 2)
        }
    }

    fun onPreviousStep() {
        _uiState.value = _uiState.value.copy(step = 1)
    }

    fun onCheckedChange() {
        _uiState.value = _uiState.value.copy(
            isCheckedAgreement =
                !_uiState.value.isCheckedAgreement
        )
    }

    fun register(onSuccess: () -> Unit) {
        if (validateStep2()) {
            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(isLoading = true)
                val result = registerUseCase(
                    username = _uiState.value.username,
                    firstName = _uiState.value.firstName,
                    lastName = _uiState.value.lastName,
                    email = _uiState.value.email,
                    password1 = _uiState.value.password,
                    password2 = _uiState.value.confirmPassword
                )
                when (result) {
                    is RegisterResult.Success -> {
                        onSuccess()
                    }

                    is RegisterResult.Error -> {
                        val fieldErrors = result.fieldErrors
                        _uiState.value = _uiState.value.copy(
                            usernameError = fieldErrors?.get("username")?.firstOrNull()
                                ?.let { UiText.DynamicString(it) },
                            emailError = fieldErrors?.get("email")?.firstOrNull()
                                ?.let { UiText.DynamicString(it) },
                            firstNameError = fieldErrors?.get("first_name")?.firstOrNull()
                                ?.let { UiText.DynamicString(it) },
                            lastNameError = fieldErrors?.get("last_name")?.firstOrNull()
                                ?.let { UiText.DynamicString(it) },
                            passwordError = fieldErrors?.get("password")?.firstOrNull()
                                ?.let { UiText.DynamicString(it) },
                        )
                        if (fieldErrors?.keys?.any {
                                it in listOf(
                                    "username",
                                    "email",
                                    "first_name",
                                    "last_name"
                                )
                            } == true) {
                            _uiState.value = _uiState.value.copy(step = 1)
                        }
                    }

                    RegisterResult.NetworkError -> {
                        _uiState.value =
                            _uiState.value.copy(
                                networkError = UiText.StringResource(R.string.network_error)
                            )
                    }

                    RegisterResult.ServerError -> {
                        _uiState.value =
                            _uiState.value.copy(
                                networkError = UiText.StringResource(R.string.server_error)
                            )
                    }

                    RegisterResult.UnknownError -> {
                        _uiState.value =
                            _uiState.value.copy(
                                networkError = UiText.StringResource(R.string.unknown_error)
                            )
                    }
                }
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    private fun validateStep1(): Boolean {
        var isValid = true
        val currentState = _uiState.value

        if (currentState.firstName.isBlank()) {
            _uiState.value =
                _uiState.value.copy(
                    firstNameError = UiText.StringResource(R.string.field_cant_be_blank)
                )
            isValid = false
        }
        if (currentState.lastName.isBlank()) {
            _uiState.value =
                _uiState.value.copy(
                    lastNameError = UiText.StringResource(R.string.field_cant_be_blank)
                )
            isValid = false
        }
        if (currentState.username.isBlank()) {
            _uiState.value =
                _uiState.value.copy(
                    usernameError = UiText.StringResource(R.string.field_cant_be_blank)
                )
            isValid = false
        }

        if (currentState.email.isBlank()) {
            _uiState.value =
                _uiState.value.copy(
                    emailError = UiText.StringResource(R.string.field_cant_be_blank)
                )
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(currentState.email).matches()) {
            _uiState.value =
                _uiState.value.copy(
                    emailError = UiText.StringResource(R.string.invalid_email_format)
                )
            isValid = false
        }

        return isValid
    }

    private fun validateStep2(): Boolean {
        var isValid = true
        val currentState = _uiState.value

        if (currentState.password.isBlank()) {
            _uiState.value =
                _uiState.value.copy(
                    passwordError = UiText.StringResource(R.string.field_cant_be_blank)
                )
            isValid = false
        } else if (currentState.password.length < 8) {
            _uiState.value =
                _uiState.value.copy(
                    passwordError =
                        UiText.StringResource(R.string.password_too_short)
                )
            isValid = false
        }

        if (currentState.confirmPassword.isBlank()) {
            _uiState.value =
                _uiState.value.copy(
                    confirmPasswordError = UiText.StringResource(R.string.field_cant_be_blank)
                )
            isValid = false
        } else if (currentState.password != currentState.confirmPassword) {
            _uiState.value =
                _uiState.value.copy(
                    confirmPasswordError = UiText.StringResource(R.string.passwords_dont_match)
                )
            isValid = false
        }

        return isValid
    }
}