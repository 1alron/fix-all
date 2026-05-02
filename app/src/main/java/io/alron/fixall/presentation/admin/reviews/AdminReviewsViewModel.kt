package io.alron.fixall.presentation.admin.reviews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.alron.fixall.domain.model.AdminReviewListItem
import io.alron.fixall.domain.repository.AdminRepository
import io.alron.fixall.domain.repository.BranchesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminReviewsViewModel @Inject constructor(
    private val repository: AdminRepository,
    private val branchesRepository: BranchesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminReviewsState())
    val state = _state.asStateFlow()

    init {
        loadBranches()
        loadReviews()
    }

    private fun loadBranches() {
        viewModelScope.launch {
            branchesRepository.getBranches()
                .onSuccess { branches ->
                    _state.update { it.copy(branches = branches) }
                }
        }
    }

    fun loadReviews() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            fetchReviews()
        }
    }

    fun refreshReviews() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true, error = null) }
            fetchReviews()
        }
    }

    private suspend fun fetchReviews() {
        val filters = mutableMapOf<String, String>()
        _state.value.centerId?.let { filters["center_id"] = it }
        if (_state.value.unansweredOnly) {
            filters["unanswered"] = "true"
        }
        _state.value.rating?.let { filters["rating"] = it.toString() }
        if (_state.value.search.isNotBlank()) {
            filters["search"] = _state.value.search.trim()
        }

        repository.getReviews(filters)
            .onSuccess { results ->
                _state.update { it.copy(
                    isLoading = false, 
                    isRefreshing = false, 
                    reviews = results.map { dto ->
                        AdminReviewListItem(
                            id = dto.id,
                            userName = dto.userName,
                            userPhone = dto.userPhone,
                            centerAddress = dto.centerAddress,
                            rating = dto.rating,
                            comment = dto.comment,
                            adminReply = dto.adminReply,
                            adminReplyAt = dto.adminReplyAt,
                            createdAt = dto.createdAt
                        )
                    }
                ) }
            }
            .onFailure { error ->
                _state.update { it.copy(isLoading = false, isRefreshing = false, error = error.message) }
            }
    }

    fun onSearchChange(query: String) {
        _state.update { it.copy(search = query) }
    }

    fun onRatingChange(rating: Int?) {
        _state.update { it.copy(rating = rating) }
    }

    fun onUnansweredOnlyChange(unanswered: Boolean) {
        _state.update { it.copy(unansweredOnly = unanswered) }
    }

    fun onCenterChange(centerId: String?) {
        _state.update { it.copy(centerId = centerId) }
    }

    fun clearFilters() {
        _state.update { 
            it.copy(
                centerId = null,
                unansweredOnly = false,
                search = "",
                rating = null
            )
        }
        loadReviews()
    }

    fun replyToReview(id: String, reply: String) {
        viewModelScope.launch {
            repository.replyToReview(id, reply)
                .onSuccess { loadReviews() }
        }
    }

    fun deleteReview(id: String) {
        viewModelScope.launch {
            repository.deleteReview(id)
                .onSuccess { loadReviews() }
        }
    }
}
