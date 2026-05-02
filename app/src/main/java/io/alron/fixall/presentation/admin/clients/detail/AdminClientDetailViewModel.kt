package io.alron.fixall.presentation.admin.clients.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import io.alron.fixall.data.remote.dto.AdminClientErrorResponseDto
import io.alron.fixall.data.remote.dto.UpdateClientRequestDto
import io.alron.fixall.domain.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class AdminClientDetailViewModel @Inject constructor(
    private val repository: AdminRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val clientId: Int = checkNotNull(savedStateHandle["clientId"])

    private val _state = MutableStateFlow(AdminClientDetailState())
    val state = _state.asStateFlow()

    init {
        loadClientDetail()
    }

    fun loadClientDetail() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            repository.getClientDetail(clientId)
                .onSuccess { detail ->
                    _state.update { it.copy(
                        client = detail,
                        isLoading = false,
                        editUsername = detail.username,
                        editFirstName = detail.fullName.split(" ").getOrNull(0) ?: "",
                        editLastName = detail.fullName.split(" ").getOrNull(1) ?: "",
                        editEmail = detail.email,
                        editPhone = detail.phone ?: "",
                        editAddress = detail.address ?: "",
                        editIsStaff = detail.isStaff
                    ) }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    fun refreshAppointment() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true) }
            repository.getClientDetail(clientId)
                .onSuccess { detail ->
                    _state.update { it.copy(client = detail, isRefreshing = false) }
                }
                .onFailure {
                    _state.update { it.copy(isRefreshing = false) }
                }
        }
    }

    fun deleteClient(onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.deleteClient(clientId)
                .onSuccess { onSuccess() }
                .onFailure { error ->
                    _state.update { it.copy(error = error.message) }
                }
        }
    }

    fun toggleEditMode() {
        _state.update { it.copy(isEditMode = !it.isEditMode, formErrors = emptyMap()) }
    }

    fun onUsernameChange(value: String) = _state.update { it.copy(editUsername = value) }
    fun onFirstNameChange(value: String) = _state.update { it.copy(editFirstName = value) }
    fun onLastNameChange(value: String) = _state.update { it.copy(editLastName = value) }
    fun onEmailChange(value: String) = _state.update { it.copy(editEmail = value) }
    fun onPhoneChange(value: String) = _state.update { it.copy(editPhone = value) }
    fun onAddressChange(value: String) = _state.update { it.copy(editAddress = value) }
    fun onIsStaffChange(value: Boolean) = _state.update { it.copy(editIsStaff = value) }

    fun updateClient() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, formErrors = emptyMap()) }
            val request = UpdateClientRequestDto(
                username = _state.value.editUsername,
                firstName = _state.value.editFirstName,
                lastName = _state.value.editLastName,
                email = _state.value.editEmail,
                phone = _state.value.editPhone.ifBlank { null },
                address = _state.value.editAddress.ifBlank { null },
                isStaff = _state.value.editIsStaff
            )
            
            repository.updateClient(clientId, request)
                .onSuccess {
                    _state.update { it.copy(isEditMode = false) }
                    loadClientDetail()
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
