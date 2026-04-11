package io.alron.fixall.presentation.appointments.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.alron.fixall.domain.repository.AppointmentsRepository
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
}

@HiltViewModel
class AppointmentDetailsViewModel @Inject constructor(
    private val repository: AppointmentsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(AppointmentDetailsState())
    val state = _state.asStateFlow()

    private val _eventChannel = Channel<AppointmentDetailsEvent>()
    val events = _eventChannel.receiveAsFlow()

    private val appointmentId: String? = savedStateHandle["id"]

    init {
        appointmentId?.let { getDetails(it) }
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
