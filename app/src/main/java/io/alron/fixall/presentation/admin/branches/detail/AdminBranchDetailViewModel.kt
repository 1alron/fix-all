package io.alron.fixall.presentation.admin.branches.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.alron.fixall.domain.repository.AdminRepository
import io.alron.fixall.presentation.util.DateTimeUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.joinAll
import java.io.File
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AdminBranchDetailViewModel @Inject constructor(
    private val repository: AdminRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val branchId: String = checkNotNull(savedStateHandle["branchId"])

    private val _state = MutableStateFlow(AdminBranchDetailState())
    val state = _state.asStateFlow()

    init {
        val calendar = Calendar.getInstance()
        val initialDate = String.format(Locale.US, "%d-%02d-%02d", 
            calendar.get(Calendar.YEAR), 
            calendar.get(Calendar.MONTH) + 1, 
            calendar.get(Calendar.DAY_OF_MONTH))
            
        val initialDisplay = String.format(Locale.getDefault(), "%02d.%02d.%d", 
            calendar.get(Calendar.DAY_OF_MONTH), 
            calendar.get(Calendar.MONTH) + 1, 
            calendar.get(Calendar.YEAR))
            
        _state.update { it.copy(selectedDate = initialDate, selectedDateDisplay = initialDisplay) }
        loadAll()
    }

    fun loadAll(isRefresh: Boolean = false, isSilent: Boolean = false) {
        viewModelScope.launch {
            if (isRefresh) {
                _state.update { it.copy(isRefreshing = true) }
            } else if (!isSilent) {
                _state.update { it.copy(isLoading = true) }
            }
            
            val jobs = listOf(
                launch { fetchBranch() },
                launch { fetchAppointments() },
                launch { fetchStatusStats() },
                launch { fetchPopularityStats() },
                launch { fetchAttendanceStats() }
            )
            
            jobs.joinAll()
            
            _state.update { it.copy(isLoading = false, isRefreshing = false) }
        }
    }

    private suspend fun fetchBranch() {
        repository.getBranchDetail(branchId)
            .onSuccess { branch ->
                _state.update { it.copy(branch = branch) }
            }
            .onFailure { error ->
                _state.update { it.copy(error = error.message) }
            }
    }

    fun onDateSelected(year: Int, month: Int, day: Int) {
        val dateStr = String.format(Locale.US, "%d-%02d-%02d", year, month + 1, day)
        val displayStr = String.format(Locale.getDefault(), "%02d.%02d.%d", day, month + 1, year)
        _state.update { it.copy(selectedDate = dateStr, selectedDateDisplay = displayStr) }
        viewModelScope.launch { fetchAppointments() }
    }

    private suspend fun fetchAppointments() {
        _state.update { it.copy(isLoadingAppointments = true) }
        val date = state.value.selectedDate
        val filters = mutableMapOf("center_id" to branchId)
        if (date.isNotBlank()) {
            filters["date_from"] = date
            filters["date_to"] = date
        }

        repository.getAppointments(filters)
            .onSuccess { appointments ->
                val filtered = appointments.filter { 
                    it.status == "SCHEDULED" || it.status == "IN_PROGRESS" || it.status == "COMPLETED"
                }
                _state.update { it.copy(appointments = filtered, isLoadingAppointments = false) }
            }
            .onFailure {
                _state.update { it.copy(isLoadingAppointments = false) }
            }
    }

    fun loadStatusStats(period: String = state.value.statusPeriod) {
        viewModelScope.launch {
            _state.update { it.copy(statusPeriod = period) }
            fetchStatusStats()
        }
    }

    private suspend fun fetchStatusStats() {
        _state.update { it.copy(isLoadingStatusStats = true) }
        repository.getStatusStats(state.value.statusPeriod, branchId)
            .onSuccess { stats ->
                _state.update { it.copy(statusStats = stats, isLoadingStatusStats = false) }
            }
            .onFailure {
                _state.update { it.copy(isLoadingStatusStats = false) }
            }
    }

    fun loadPopularityStats(period: String = state.value.popularityPeriod) {
        viewModelScope.launch {
            _state.update { it.copy(popularityPeriod = period) }
            fetchPopularityStats()
        }
    }

    private suspend fun fetchPopularityStats() {
        _state.update { it.copy(isLoadingPopularity = true) }
        repository.getServicePopularity(state.value.popularityPeriod, branchId)
            .onSuccess { stats ->
                _state.update { it.copy(servicePopularity = stats, isLoadingPopularity = false) }
            }
            .onFailure {
                _state.update { it.copy(isLoadingPopularity = false) }
            }
    }

    fun loadAttendanceStats(period: String = state.value.attendancePeriod) {
        viewModelScope.launch {
            _state.update { it.copy(attendancePeriod = period) }
            fetchAttendanceStats()
        }
    }

    private suspend fun fetchAttendanceStats() {
        _state.update { it.copy(isLoadingAttendance = true) }
        repository.getBranchAttendanceStats(branchId, state.value.attendancePeriod)
            .onSuccess { stats ->
                _state.update { it.copy(attendanceStats = stats, isLoadingAttendance = false) }
            }
            .onFailure {
                _state.update { it.copy(isLoadingAttendance = false) }
            }
    }
    
    fun deleteBranch(onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.deleteBranch(branchId)
                .onSuccess { onSuccess() }
        }
    }
    
    fun updatePhoto(file: File) {
        viewModelScope.launch {
            repository.updateBranchPhoto(branchId, file)
                .onSuccess { loadAll(isSilent = true) }
        }
    }
}
