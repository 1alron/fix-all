package io.alron.fixall.presentation.admin.clients

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
class AdminClientsViewModel @Inject constructor(
    private val repository: AdminRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminClientsState())
    val state = _state.asStateFlow()

    init {
        loadClients()
    }

    fun loadClients() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            fetchClients()
        }
    }

    fun refreshClients() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true, error = null) }
            fetchClients()
        }
    }

    fun refreshSilently() {
        viewModelScope.launch {
            fetchClients()
        }
    }

    private suspend fun fetchClients() {
        val filters = mutableMapOf<String, String>()
        if (_state.value.search.isNotBlank()) filters["search"] = _state.value.search
        if (_state.value.email.isNotBlank()) filters["email"] = _state.value.email
        if (_state.value.phone.isNotBlank()) filters["phone"] = _state.value.phone
        if (_state.value.hasCars) filters["has_cars"] = "true"
        if (_state.value.hasActive) filters["has_active"] = "true"
        _state.value.dateFrom?.let { filters["date_from"] = it }
        _state.value.dateTo?.let { filters["date_to"] = it }

        repository.getClients(filters)
            .onSuccess { clients ->
                _state.update { it.copy(
                    clients = clients,
                    totalCount = clients.size,
                    isLoading = false,
                    isRefreshing = false
                ) }
            }
            .onFailure { error ->
                _state.update { it.copy(
                    isLoading = false,
                    isRefreshing = false,
                    error = error.message
                ) }
            }
    }

    fun onSearchChange(value: String) = _state.update { it.copy(search = value) }
    fun onEmailChange(value: String) = _state.update { it.copy(email = value) }
    fun onPhoneChange(value: String) = _state.update { it.copy(phone = value) }
    fun onHasCarsToggle(value: Boolean) = _state.update { it.copy(hasCars = value) }
    fun onHasActiveToggle(value: Boolean) = _state.update { it.copy(hasActive = value) }
    fun onDateFromChange(value: String?) = _state.update { it.copy(dateFrom = value) }
    fun onDateToChange(value: String?) = _state.update { it.copy(dateTo = value) }

    fun clearFilters() {
        _state.update { it.copy(
            search = "",
            email = "",
            phone = "",
            hasCars = false,
            hasActive = false,
            dateFrom = null,
            dateTo = null
        ) }
        loadClients()
    }
}
