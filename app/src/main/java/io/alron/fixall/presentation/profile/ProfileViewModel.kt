package io.alron.fixall.presentation.profile

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.alron.fixall.domain.AuthManager
import io.alron.fixall.domain.repository.ProfileRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

sealed class ProfileEvent {
    data class ShowToast(val message: String) : ProfileEvent()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository,
    private val authManager: AuthManager
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    private val _eventChannel = Channel<ProfileEvent>()
    val events = _eventChannel.receiveAsFlow()

    init {
        loadProfileData()
    }

    fun refresh() {
        loadProfileData()
    }

    private fun loadProfileData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            val userResult = repository.getMe()
            val loyaltyResult = repository.getLoyalty()

            if (userResult.isSuccess) {
                _state.update { it.copy(
                    user = userResult.getOrNull(),
                    loyaltyInfo = loyaltyResult.getOrNull(),
                    isLoading = false
                ) }
            } else {
                _state.update { it.copy(
                    isLoading = false,
                    errorMessage = userResult.exceptionOrNull()?.localizedMessage ?: "Unknown error"
                ) }
            }
        }
    }

    fun updateProfile(
        username: String,
        firstName: String,
        lastName: String,
        email: String,
        phone: String?,
        address: String?
    ) {
        _state.update { it.copy(fieldErrors = null) }
        
        if (!validate(username, email, phone)) return

        viewModelScope.launch {
            _state.update { it.copy(isUpdating = true) }
            repository.updateProfile(username, firstName, lastName, email, phone, address)
                .onSuccess { updatedUser ->
                    _state.update { it.copy(user = updatedUser, isUpdating = false) }
                    _eventChannel.send(ProfileEvent.ShowToast("Профиль обновлен"))
                }
                .onFailure { throwable ->
                    _state.update { it.copy(isUpdating = false) }
                    _eventChannel.send(ProfileEvent.ShowToast(throwable.localizedMessage ?: "Ошибка обновления"))
                }
        }
    }

    private fun validate(username: String, email: String, phone: String?): Boolean {
        var isValid = true
        val errors = mutableMapOf<String, List<String>>()

        val usernameRegex = "^[\\w.@+-]+\$".toRegex()
        if (!username.matches(usernameRegex)) {
            errors["username"] = listOf("Только буквы, цифры и @/./+/-/_")
            isValid = false
        }
        
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errors["email"] = listOf("Неверный формат почты")
            isValid = false
        }
        
        val phoneRegex = "^(\\+7|8)[0-9]{10}$".toRegex()
        if (!phone.isNullOrBlank() && !phone.matches(phoneRegex)) {
            errors["phone"] = listOf("Введите корректный номер (10-11 цифр)")
            isValid = false
        }

        if (!isValid) {
            _state.update { it.copy(fieldErrors = errors) }
        }
        
        return isValid
    }

    fun uploadAvatar(part: MultipartBody.Part) {
        viewModelScope.launch {
            _state.update { it.copy(isUploadingAvatar = true) }
            repository.uploadAvatar(part)
                .onSuccess {
                    loadProfileData()
                    _state.update { it.copy(isUploadingAvatar = false) }
                }
                .onFailure { throwable ->
                    _state.update { it.copy(isUploadingAvatar = false) }
                    _eventChannel.send(ProfileEvent.ShowToast(throwable.localizedMessage ?: "Error"))
                }
        }
    }

    fun deleteAvatar() {
        viewModelScope.launch {
            repository.deleteAvatar()
                .onSuccess {
                    loadProfileData()
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authManager.logout()
        }
    }
}
