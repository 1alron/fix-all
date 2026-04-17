package io.alron.fixall.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.alron.fixall.domain.AuthManager
import io.alron.fixall.domain.repository.AppointmentsRepository
import io.alron.fixall.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authManager: AuthManager,
    private val appointmentsRepository: AppointmentsRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    init {
        observeUpcomingAppointment()
        loadInitialData()
    }

    private fun observeUpcomingAppointment() {
        appointmentsRepository.upcomingAppointment
            .onEach { appointment ->
                _state.update { it.copy(upcomingAppointment = appointment) }
            }
            .launchIn(viewModelScope)
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            val loyaltyJob = launch {
                profileRepository.getLoyalty().onSuccess { loyalty ->
                    _state.update { it.copy(loyaltyInfo = loyalty) }
                }
            }
            
            val statsJob = launch {
                profileRepository.getStats().onSuccess { stats ->
                    _state.update { it.copy(userStats = stats) }
                }
            }
            
            val upcomingJob = launch {
                appointmentsRepository.getUpcomingAppointment()
            }
            
            loyaltyJob.join()
            statsJob.join()
            upcomingJob.join()
            
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true) }
            
            profileRepository.getLoyalty().onSuccess { loyalty ->
                _state.update { it.copy(loyaltyInfo = loyalty) }
            }
            
            profileRepository.getStats().onSuccess { stats ->
                _state.update { it.copy(userStats = stats) }
            }
            
            appointmentsRepository.getUpcomingAppointment()

            _state.update { it.copy(isRefreshing = false) }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authManager.logout()
        }
    }
}
