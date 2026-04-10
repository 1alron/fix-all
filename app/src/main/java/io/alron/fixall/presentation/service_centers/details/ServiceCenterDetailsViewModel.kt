package io.alron.fixall.presentation.service_centers.details

import androidx.lifecycle.SavedStateHandle
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
class ServiceCenterDetailsViewModel @Inject constructor(
    private val repository: BranchesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(ServiceCenterDetailsState())
    val state = _state.asStateFlow()

    init {
        savedStateHandle.get<String>("id")?.let { id ->
            getBranchDetails(id)
        }
    }

    fun getBranchDetails(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            repository.getBranch(id)
                .onSuccess { branch ->
                    _state.update { it.copy(branch = branch, isLoading = false) }
                }
                .onFailure { throwable ->
                    _state.update { it.copy(errorMessage = throwable.localizedMessage, isLoading = false) }
                }
        }
    }
}