package io.alron.fixall.presentation.admin

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
class AdminDashboardViewModel @Inject constructor(
    private val repository: AdminRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminDashboardState())
    val state = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        loadDashboard()
        loadStatusStats(_state.value.selectedPeriod)
        loadAttendanceStats(_state.value.selectedAttendancePeriod)
        loadServicePopularity(_state.value.selectedPopularityPeriod)
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            repository.getDashboardStats()
                .onSuccess { stats ->
                    _state.update { it.copy(isLoading = false, stats = stats) }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    fun loadStatusStats(period: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingStatuses = true, selectedPeriod = period) }
            repository.getStatusStats(period)
                .onSuccess { stats ->
                    _state.update { it.copy(isLoadingStatuses = false, statusStats = stats) }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoadingStatuses = false) }
                }
        }
    }

    fun loadAttendanceStats(period: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingAttendance = true, selectedAttendancePeriod = period) }
            repository.getAttendanceStats(period)
                .onSuccess { stats ->
                    _state.update { it.copy(isLoadingAttendance = false, attendanceStats = stats) }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoadingAttendance = false) }
                }
        }
    }

    fun loadServicePopularity(period: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingPopularity = true, selectedPopularityPeriod = period) }
            repository.getServicePopularity(period)
                .onSuccess { stats ->
                    _state.update { it.copy(isLoadingPopularity = false, servicePopularity = stats) }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoadingPopularity = false) }
                }
        }
    }
}
