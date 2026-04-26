package io.alron.fixall.presentation.admin.appointments.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.alron.fixall.domain.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminAppointmentDetailViewModel @Inject constructor(
    private val repository: AdminRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val appointmentId: String = checkNotNull(savedStateHandle["id"])

    private val _state = MutableStateFlow(AdminAppointmentDetailState())
    val state = _state.asStateFlow()

    init {
        loadAppointment()
    }

    fun loadAppointment() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            fetchAppointment()
        }
    }

    fun refreshAppointment() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true, error = null) }
            fetchAppointment()
        }
    }

    private suspend fun fetchAppointment() {
        repository.getAppointmentDetail(appointmentId)
            .onSuccess { appointment ->
                _state.update { it.copy(isLoading = false, isRefreshing = false, appointment = appointment) }
            }
            .onFailure { error ->
                _state.update { it.copy(isLoading = false, isRefreshing = false, error = error.message) }
            }
    }

    fun changeStatus(newStatus: String) {
        viewModelScope.launch {
            _state.update { it.copy(isUpdatingStatus = true) }
            repository.changeStatus(appointmentId, newStatus)
                .onSuccess {
                    loadAppointment()
                }
                .onFailure { error ->
                    _state.update { it.copy(isUpdatingStatus = false, error = error.message) }
                }
        }
    }

    fun addNote(note: String) {
        viewModelScope.launch {
            _state.update { it.copy(isAddingNote = true) }
            repository.addNote(appointmentId, note)
                .onSuccess { response ->
                    _state.update { 
                        it.copy(
                            isAddingNote = false, 
                            appointment = it.appointment?.copy(notes = response.notes)
                        )
                    }
                }
                .onFailure { error ->
                    _state.update { it.copy(isAddingNote = false, error = error.message) }
                }
        }
    }
}
