package io.alron.fixall.presentation.admin.appointments

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
class AdminAppointmentsViewModel @Inject constructor(
    private val repository: AdminRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminAppointmentsState())
    val state = _state.asStateFlow()

    init {
        loadAppointments()
    }

    fun loadAppointments() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            val filters = mutableMapOf<String, String>()
            _state.value.centerId?.let { filters["center_id"] = it }
            _state.value.date?.let { filters["date"] = it }
            _state.value.status?.let { filters["status"] = it }
            if (_state.value.search.isNotBlank()) {
                filters["search"] = _state.value.search
            }

            repository.getAppointments(filters)
                .onSuccess { appointments ->
                    _state.update { it.copy(isLoading = false, appointments = appointments) }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    fun onSearchChange(query: String) {
        _state.update { it.copy(search = query) }
        loadAppointments()
    }

    fun onStatusChange(status: String?) {
        _state.update { it.copy(status = status) }
        loadAppointments()
    }

    fun onDateChange(date: String?) {
        _state.update { it.copy(date = date) }
        loadAppointments()
    }

    fun onCenterChange(centerId: String?) {
        _state.update { it.copy(centerId = centerId) }
        loadAppointments()
    }

    fun clearFilters() {
        _state.update { 
            it.copy(
                centerId = null,
                date = null,
                status = null,
                search = ""
            )
        }
        loadAppointments()
    }
}
