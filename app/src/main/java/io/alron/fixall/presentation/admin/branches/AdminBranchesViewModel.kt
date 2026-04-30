package io.alron.fixall.presentation.admin.branches

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
class AdminBranchesViewModel @Inject constructor(
    private val repository: AdminRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminBranchesState())
    val state = _state.asStateFlow()

    init {
        getBranches(isRefresh = false)
    }

    fun getBranches(isRefresh: Boolean = false) {
        viewModelScope.launch {
            _state.update { 
                if (isRefresh) it.copy(isRefreshing = true, error = null)
                else it.copy(isLoading = true, error = null)
            }
            fetchBranches()
        }
    }

    fun refreshSilently() {
        viewModelScope.launch {
            fetchBranches()
        }
    }

    private suspend fun fetchBranches() {
        repository.getBranches()
            .onSuccess { branches ->
                _state.update { it.copy(branches = branches, isLoading = false, isRefreshing = false) }
            }
            .onFailure { error ->
                _state.update { it.copy(isLoading = false, isRefreshing = false, error = error.message) }
            }
    }
}
