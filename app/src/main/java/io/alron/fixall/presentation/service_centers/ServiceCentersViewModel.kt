package io.alron.fixall.presentation.service_centers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.alron.fixall.domain.repository.BranchesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServiceCentersViewModel @Inject constructor(
    private val repository: BranchesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ServiceCentersState())
    val state = _state.asStateFlow()

    init {
        getBranches()
    }

    fun getBranches() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            repository.getBranches()
                .onSuccess { branches ->
                    _state.update { it.copy(branches = branches, isLoading = false) }
                }
                .onFailure { throwable ->
                    _state.update { it.copy(errorMessage = throwable.localizedMessage, isLoading = false) }
                }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true, errorMessage = null) }
            repository.getBranches()
                .onSuccess { branches ->
                    _state.update { it.copy(branches = branches, isRefreshing = false) }
                }
                .onFailure { throwable ->
                    _state.update { it.copy(errorMessage = throwable.localizedMessage, isRefreshing = false) }
                }
        }
    }
}