package io.alron.fixall.presentation.admin.appointments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.alron.fixall.domain.repository.AdminRepository
import io.alron.fixall.domain.repository.BranchesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminAppointmentsViewModel @Inject constructor(
    private val repository: AdminRepository,
    private val branchesRepository: BranchesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminAppointmentsState())
    val state = _state.asStateFlow()

    init {
        loadBranches()
        loadAppointments()
    }

    private fun loadBranches() {
        viewModelScope.launch {
            branchesRepository.getBranches()
                .onSuccess { branches ->
                    _state.update { it.copy(branches = branches) }
                }
        }
    }

    fun loadAppointments() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            val filters = mutableMapOf<String, String>()
            _state.value.centerId?.let { filters["center_id"] = it }
            _state.value.dateFrom?.let { filters["date_from"] = it }
            _state.value.dateTo?.let { filters["date_to"] = it }
            _state.value.status?.let { filters["status"] = it }
            if (_state.value.search.isNotBlank()) {
                filters["search"] = _state.value.search.trim()
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
    }

    fun onStatusChange(status: String?) {
        _state.update { it.copy(status = status) }
    }

    fun onDateFromChange(date: String?) {
        _state.update { it.copy(dateFrom = date) }
    }

    fun onDateToChange(date: String?) {
        _state.update { it.copy(dateTo = date) }
    }

    fun onCenterChange(centerId: String?) {
        _state.update { it.copy(centerId = centerId) }
    }

    fun clearFilters() {
        _state.update { 
            it.copy(
                centerId = null,
                dateFrom = null,
                dateTo = null,
                status = null,
                search = ""
            )
        }
        loadAppointments()
    }
}
