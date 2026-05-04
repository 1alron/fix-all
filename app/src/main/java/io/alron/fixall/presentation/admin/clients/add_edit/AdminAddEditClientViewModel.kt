package io.alron.fixall.presentation.admin.clients.add_edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import io.alron.fixall.data.remote.dto.AdminClientErrorResponseDto
import io.alron.fixall.data.remote.dto.CreateClientRequestDto
import io.alron.fixall.domain.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class AdminAddEditClientViewModel @Inject constructor(
    private val repository: AdminRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminAddEditClientState())
    val state = _state.asStateFlow()

    fun onUsernameChange(value: String) = _state.update { it.copy(username = value) }
    fun onFirstNameChange(value: String) = _state.update { it.copy(firstName = value) }
    fun onLastNameChange(value: String) = _state.update { it.copy(lastName = value) }
    fun onEmailChange(value: String) = _state.update { it.copy(email = value) }
    fun onPasswordChange(value: String) = _state.update { it.copy(password = value) }
    fun onPasswordConfirmChange(value: String) = _state.update { it.copy(passwordConfirm = value) }
    fun onPhoneChange(value: String) = _state.update { it.copy(phone = value) }
    fun onAddressChange(value: String) = _state.update { it.copy(address = value) }
    fun onIsStaffChange(value: Boolean) = _state.update { it.copy(isStaff = value) }

    fun createClient() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, formErrors = emptyMap()) }
            val request = CreateClientRequestDto(
                username = _state.value.username,
                firstName = _state.value.firstName,
                lastName = _state.value.lastName,
                email = _state.value.email,
                password = _state.value.password,
                passwordConfirm = _state.value.passwordConfirm,
                phone = _state.value.phone.ifBlank { null },
                address = _state.value.address.ifBlank { null },
                isStaff = _state.value.isStaff
            )

            repository.createClient(request)
                .onSuccess {
                    _state.update { it.copy(isLoading = false, isSuccess = true) }
                }
                .onFailure { e ->
                    if (e is HttpException) {
                        val errorBody = e.response()?.errorBody()?.string()
                        try {
                            val errorResponse = Gson().fromJson(errorBody, AdminClientErrorResponseDto::class.java)
                            _state.update { it.copy(isLoading = false, formErrors = errorResponse.errors) }
                        } catch (ex: Exception) {
                            _state.update { it.copy(isLoading = false, error = e.message) }
                        }
                    } else {
                        _state.update { it.copy(isLoading = false, error = e.message) }
                    }
                }
        }
    }
}
