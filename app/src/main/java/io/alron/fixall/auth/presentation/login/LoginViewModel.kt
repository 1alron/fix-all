package io.alron.fixall.auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.alron.fixall.R
import io.alron.fixall.auth.domain.model.LoginResult
import io.alron.fixall.auth.domain.usecase.LoginUseCase
import io.alron.fixall.auth.presentation.util.UiText
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login() {
        if (!validateFields()) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (loginUseCase(
                _uiState.value.username,
                _uiState.value.password
            )) {
                is LoginResult.Success -> {
                    _uiState.value = _uiState.value.copy(networkError = null)
                }

                is LoginResult.InvalidCredentials -> {
                    _uiState.value = _uiState.value.copy(
                        networkError = UiText.StringResource(R.string.invalid_credentials)
                    )
                }

                is LoginResult.NetworkError -> {
                    _uiState.value = _uiState.value.copy(
                        networkError = UiText.StringResource(R.string.network_error)
                    )
                }

                is LoginResult.ServerError -> {
                    _uiState.value = _uiState.value.copy(
                        networkError = UiText.StringResource(R.string.server_error)
                    )
                }

                is LoginResult.UnknownError -> {
                    _uiState.value = _uiState.value.copy(
                        networkError = UiText.StringResource(R.string.unknown_error)
                    )
                }
            }
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun updateUsername(username: String) {
        _uiState.value = _uiState.value.copy(username = username, usernameError = null)
    }

    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(password = password, passwordError = null)
    }

    private fun validateFields(): Boolean {
        var isValid = true
        if (_uiState.value.username.isEmpty()) {
            isValid = false
            _uiState.value = _uiState.value.copy(
                usernameError = UiText.StringResource(R.string.field_cant_be_blank)
            )
        }
        if (_uiState.value.password.isEmpty()) {
            isValid = false
            _uiState.value = _uiState.value.copy(
                passwordError = UiText.StringResource(R.string.field_cant_be_blank)
            )
        }
        return isValid
    }
}