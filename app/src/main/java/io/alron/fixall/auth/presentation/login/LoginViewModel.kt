package io.alron.fixall.auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.alron.fixall.R
import io.alron.fixall.auth.domain.model.LoginResult
import io.alron.fixall.auth.domain.usecase.LoginUseCase
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
                    _uiState.value = _uiState.value.copy(networkErrorResId = null)
                }

                is LoginResult.InvalidCredentials -> {
                    _uiState.value = _uiState.value.copy(
                        networkErrorResId = R.string.invalid_credentials
                    )
                }

                is LoginResult.NetworkError -> {
                    _uiState.value = _uiState.value.copy(
                        networkErrorResId = R.string.network_error
                    )
                }

                is LoginResult.ServerError -> {
                    _uiState.value = _uiState.value.copy(
                        networkErrorResId = R.string.server_error
                    )
                }

                is LoginResult.UnknownError -> {
                    _uiState.value = _uiState.value.copy(
                        networkErrorResId = R.string.unknown_error
                    )
                }
            }
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun updateUsername(username: String) {
        _uiState.value = _uiState.value.copy(username = username, usernameErrorResId = null)
    }

    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(password = password, passwordErrorResId = null)
    }

    private fun validateFields(): Boolean {
        var isValid = true
        if (_uiState.value.username.isEmpty()) {
            isValid = false
            _uiState.value = _uiState.value.copy(usernameErrorResId = R.string.field_cant_be_blank)
        }
        if (_uiState.value.password.isEmpty()) {
            isValid = false
            _uiState.value = _uiState.value.copy(passwordErrorResId = R.string.field_cant_be_blank)
        }
        return isValid
    }
}