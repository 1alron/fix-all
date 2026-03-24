package io.alron.fixall.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.alron.fixall.domain.AuthManager
import io.alron.fixall.domain.repository.BranchesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val branchesRepository: BranchesRepository,
    private val authManager: AuthManager
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    init {
        getBranches()
    }

    fun getBranches() {
        viewModelScope.launch {
            runCatching {
                _state.update {
                    it.copy(isLoading = true, errorMessage = null)
                }
                val branches = branchesRepository.getBranches()
                _state.update {
                    it.copy(isLoading = false, errorMessage = null, branches = branches)
                }
            }.onFailure {
                _state.update {
                    it.copy(isLoading = false, errorMessage = "Не удалось загрузить данные")
                }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _state.update {
                it.copy(isRefreshing = true, errorMessage = null)
            }
            runCatching {
                val branches = branchesRepository.getBranches()
                _state.update {
                    it.copy(isRefreshing = false, errorMessage = null, branches = branches)
                }
            }.onFailure {
                _state.update {
                    it.copy(isRefreshing = false, errorMessage = "Не удалось загрузить данные")
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authManager.logout()
        }
    }
}