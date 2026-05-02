package io.alron.fixall.presentation.appointments.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.alron.fixall.domain.model.LoyaltyInfo
import io.alron.fixall.domain.repository.AppointmentsRepository
import io.alron.fixall.domain.repository.ProfileRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AppointmentDetailsEvent {
    data class ShowToast(val message: String) : AppointmentDetailsEvent()
    object AppointmentCancelled : AppointmentDetailsEvent()
    data class OpenPaymentUrl(val url: String) : AppointmentDetailsEvent()
}

@HiltViewModel
class AppointmentDetailsViewModel @Inject constructor(
    private val repository: AppointmentsRepository,
    private val profileRepository: ProfileRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(AppointmentDetailsState())
    val state = _state.asStateFlow()

    private val _eventChannel = Channel<AppointmentDetailsEvent>()
    val events = _eventChannel.receiveAsFlow()

    private val appointmentId: String? = savedStateHandle["id"]

    init {
        appointmentId?.let { 
            getDetails(it)
            loadLoyaltyInfo()
            syncPaymentStatusInitial(it)
        }
    }

    private fun loadLoyaltyInfo() {
        viewModelScope.launch {
            profileRepository.getLoyalty().onSuccess { loyalty ->
                _state.update { it.copy(loyaltyInfo = loyalty) }
            }
        }
    }

    fun getDetails(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            repository.getAppointmentDetails(id)
                .onSuccess { appointment ->
                    _state.update { it.copy(appointment = appointment, isLoading = false) }
                }
                .onFailure { throwable ->
                    _state.update { it.copy(errorMessage = throwable.localizedMessage, isLoading = false) }
                }
        }
    }

    fun refreshDetails(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true, errorMessage = null) }
            repository.getAppointmentDetails(id)
                .onSuccess { appointment ->
                    _state.update { it.copy(appointment = appointment) }
                    if (!appointment.isPaid) {
                        repository.syncPaymentStatus(id).onSuccess { syncResponse ->
                            if (syncResponse.isPaid) {
                                repository.getAppointmentDetails(id).onSuccess { updatedAppointment ->
                                    _state.update { it.copy(appointment = updatedAppointment) }
                                }
                            }
                        }
                    }
                    repository.getPaymentStatus(id).onSuccess { status ->
                         _state.update { it.copy(paymentStatus = status, isRefreshing = false) }
                    }.onFailure {
                         _state.update { it.copy(isRefreshing = false) }
                    }
                }
                .onFailure { throwable ->
                    _state.update { it.copy(errorMessage = throwable.localizedMessage, isRefreshing = false) }
                }
        }
    }

    private fun syncPaymentStatusInitial(id: String) {
        viewModelScope.launch {
            repository.syncPaymentStatus(id).onSuccess { response ->
                if (response.isPaid) {
                    getDetails(id)
                }
                repository.getPaymentStatus(id).onSuccess { status ->
                     _state.update { it.copy(paymentStatus = status) }
                }
            }.onFailure {
                repository.getPaymentStatus(id).onSuccess { status ->
                     _state.update { it.copy(paymentStatus = status) }
                }
            }
        }
    }

    fun initiatePayment(bonusAmount: Double) {
        val id = appointmentId ?: return
        viewModelScope.launch {
            _state.update { it.copy(isPaying = true) }
            repository.initiatePayment(id, bonusAmount)
                .onSuccess { paymentUrl ->
                    _state.update { it.copy(isPaying = false) }
                    _eventChannel.send(AppointmentDetailsEvent.OpenPaymentUrl(paymentUrl))
                    getPaymentStatus(id)
                }
                .onFailure { throwable ->
                    _state.update { it.copy(isPaying = false) }
                    _eventChannel.send(AppointmentDetailsEvent.ShowToast(throwable.localizedMessage ?: "Error"))
                }
        }
    }

    fun continuePayment() {
        initiatePayment(0.0)
    }

    fun getPaymentStatus(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isCheckingPayment = true) }
            repository.getPaymentStatus(id)
                .onSuccess { status ->
                    _state.update { it.copy(paymentStatus = status, isCheckingPayment = false) }
                }
                .onFailure {
                    _state.update { it.copy(isCheckingPayment = false) }
                }
        }
    }

    fun syncPaymentStatus() {
        val id = appointmentId ?: return
        viewModelScope.launch {
            _state.update { it.copy(isCheckingPayment = true) }
            repository.syncPaymentStatus(id)
                .onSuccess { response ->
                    if (response.isPaid) {
                        getDetails(id)
                    }
                    getPaymentStatus(id)
                }
                .onFailure {
                    _state.update { it.copy(isCheckingPayment = false) }
                }
        }
    }

    fun cancelAppointment() {
        val id = appointmentId ?: return
        viewModelScope.launch {
            _state.update { it.copy(isCancelling = true) }
            repository.cancelAppointment(id)
                .onSuccess { message ->
                    _state.update { it.copy(isCancelling = false) }
                    _eventChannel.send(AppointmentDetailsEvent.ShowToast(message))
                    _eventChannel.send(AppointmentDetailsEvent.AppointmentCancelled)
                }
                .onFailure { throwable ->
                    _state.update { it.copy(isCancelling = false) }
                    _eventChannel.send(AppointmentDetailsEvent.ShowToast(throwable.localizedMessage ?: "Error"))
                }
        }
    }
}
