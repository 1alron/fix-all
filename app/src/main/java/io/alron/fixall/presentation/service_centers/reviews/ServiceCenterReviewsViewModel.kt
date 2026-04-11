package io.alron.fixall.presentation.service_centers.reviews

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.alron.fixall.domain.repository.BranchesRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ServiceCenterReviewsEvent {
    data class ShowToast(val message: String) : ServiceCenterReviewsEvent()
    object ReviewAdded : ServiceCenterReviewsEvent()
}

@HiltViewModel
class ServiceCenterReviewsViewModel @Inject constructor(
    private val repository: BranchesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(ServiceCenterReviewsState(isLoading = true))
    val state = _state.asStateFlow()

    private val _eventChannel = Channel<ServiceCenterReviewsEvent>()
    val events = _eventChannel.receiveAsFlow()

    private val branchId: String? = savedStateHandle["id"]

    init {
        loadReviews()
    }

    fun loadReviews(refresh: Boolean = false) {
        val id = branchId ?: return
        viewModelScope.launch {
            if (refresh) {
                _state.update { it.copy(isRefreshing = true, currentPage = 1) }
            } else {
                _state.update { it.copy(isLoading = true) }
            }

            repository.getReviews(id, _state.value.currentPage)
                .onSuccess { newReviews ->
                    _state.update { currentState ->
                        currentState.copy(
                            reviews = if (refresh) newReviews else currentState.reviews + newReviews,
                            isLoading = false,
                            isRefreshing = false,
                            canLoadMore = newReviews.isNotEmpty()
                        )
                    }
                }
                .onFailure { throwable ->
                    _state.update { it.copy(isLoading = false, isRefreshing = false, errorMessage = throwable.localizedMessage) }
                }
        }
    }

    fun addReview(rating: Int, comment: String) {
        val id = branchId ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            repository.addReview(id, rating, comment)
                .onSuccess {
                    _state.update { it.copy(isLoading = false) }
                    _eventChannel.send(ServiceCenterReviewsEvent.ShowToast("Спасибо за отзыв!"))
                    _eventChannel.send(ServiceCenterReviewsEvent.ReviewAdded)
                    loadReviews(refresh = true)
                }
                .onFailure { throwable ->
                    _state.update { it.copy(isLoading = false) }
                    _eventChannel.send(ServiceCenterReviewsEvent.ShowToast(throwable.localizedMessage ?: "Ошибка"))
                }
        }
    }

    fun loadNextPage() {
        if (_state.value.canLoadMore && !_state.value.isLoading) {
            _state.update { it.copy(currentPage = it.currentPage + 1) }
            loadReviews()
        }
    }
}
