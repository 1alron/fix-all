package io.alron.fixall.presentation.admin.services

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.alron.fixall.data.remote.dto.CreateUpdateServiceRequestDto
import io.alron.fixall.domain.model.AdminService
import io.alron.fixall.domain.repository.AdminRepository
import io.alron.fixall.domain.repository.BranchesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminServicesViewModel @Inject constructor(
    private val repository: AdminRepository,
    private val branchesRepository: BranchesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminServicesState())
    val state = _state.asStateFlow()

    init {
        loadBranches()
        loadServices()
    }

    private fun loadBranches() {
        viewModelScope.launch {
            branchesRepository.getBranches().onSuccess { branches ->
                _state.update { it.copy(branches = branches) }
            }
        }
    }

    fun loadServices() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            fetchServices()
        }
    }

    fun refreshServices() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true, error = null) }
            fetchServices()
        }
    }

    private suspend fun fetchServices() {
        val filters = mutableMapOf<String, String>()
        _state.value.centerId?.let { filters["center_id"] = it }
        if (_state.value.isActiveOnly) filters["active_only"] = "true"
        if (_state.value.search.isNotBlank()) filters["search"] = _state.value.search.trim()

        repository.getServices(filters)
            .onSuccess { services ->
                _state.update { it.copy(isLoading = false, isRefreshing = false, services = services) }
            }
            .onFailure { error ->
                _state.update { it.copy(isLoading = false, isRefreshing = false, error = error.message) }
            }
    }

    fun onSearchChange(query: String) {
        _state.update { it.copy(search = query) }
    }

    fun onActiveOnlyChange(activeOnly: Boolean) {
        _state.update { it.copy(isActiveOnly = activeOnly) }
    }

    fun onCenterChange(centerId: String?) {
        _state.update { it.copy(centerId = centerId) }
    }

    fun clearFilters() {
        _state.update { it.copy(centerId = null, isActiveOnly = false, search = "") }
        loadServices()
    }

    fun createService(name: String, description: String?, duration: Int, price: String, centerId: String, isActive: Boolean) {
        viewModelScope.launch {
            repository.createService(CreateUpdateServiceRequestDto(name, description, duration, price, centerId, isActive))
                .onSuccess { loadServices() }
        }
    }

    fun updateService(id: String, name: String, description: String?, duration: Int, price: String, centerId: String, isActive: Boolean) {
        viewModelScope.launch {
            repository.updateService(id, CreateUpdateServiceRequestDto(name, description, duration, price, centerId, isActive))
                .onSuccess { updatedService ->
                    _state.update { currentState ->
                        val updatedList = currentState.services.map {
                            if (it.id == id) updatedService else it
                        }
                        currentState.copy(services = updatedList)
                    }
                }
        }
    }

    fun deleteService(id: String) {
        viewModelScope.launch {
            repository.deleteService(id).onSuccess {
                _state.update { currentState ->
                    val updatedList = currentState.services.filter { it.id != id }
                    currentState.copy(services = updatedList)
                }
            }
        }
    }

    fun toggleActive(id: String) {
        viewModelScope.launch {
            repository.toggleServiceActive(id).onSuccess { response ->
                _state.update { currentState ->
                    val updatedList = currentState.services.map {
                        if (it.id == id) it.copy(isActive = response.isActive) else it
                    }
                    currentState.copy(services = updatedList)
                }
            }
        }
    }
}
