package io.alron.fixall.presentation.appointments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.alron.fixall.domain.repository.AppointmentsRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AppointmentsEvent {
    data class ShowToast(val message: String) : AppointmentsEvent()
}

@HiltViewModel
class AppointmentsViewModel @Inject constructor(
    private val repository: AppointmentsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AppointmentsState())
    val state = _state.asStateFlow()

    private val _eventChannel = Channel<AppointmentsEvent>()
    val events = _eventChannel.receiveAsFlow()

    init {
        observeAppointments()
    }

    private fun observeAppointments() {
        _state.update { it.copy(isLoading = true) }
        repository.appointments
            .onEach { appointments ->
                _state.update { it.copy(appointments = appointments, isLoading = false) }
            }
            .launchIn(viewModelScope)
    }

    fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true, errorMessage = null) }
            repository.getAppointments()
                .onFailure { throwable ->
                    _state.update { it.copy(errorMessage = throwable.localizedMessage) }
                }
            _state.update { it.copy(isRefreshing = false) }
        }
    }

    fun cancelAppointment(id: String) {
        viewModelScope.launch {
            repository.cancelAppointment(id)
                .onSuccess { message ->
                    _eventChannel.send(AppointmentsEvent.ShowToast(message))
                }
                .onFailure { throwable ->
                    _eventChannel.send(AppointmentsEvent.ShowToast(throwable.localizedMessage ?: "Error"))
                }
        }
    }
}
