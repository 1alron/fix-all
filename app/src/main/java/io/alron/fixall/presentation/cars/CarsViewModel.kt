package io.alron.fixall.presentation.cars

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.alron.fixall.domain.repository.CarsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CarsViewModel @Inject constructor(
    private val carsRepository: CarsRepository
) : ViewModel() {
    private val _state = MutableStateFlow(CarsState())
    val state = _state.asStateFlow()

    init {
        getCars()
    }

    fun getCars() {
        viewModelScope.launch {
            runCatching {
                _state.update {
                    it.copy(isLoading = true, errorMessage = null)
                }
                val cars = carsRepository.getCars()
                _state.update {
                    it.copy(isLoading = false, errorMessage = null, cars = cars)
                }
            }.onFailure { throwable ->
                _state.update { state ->
                    state.copy(isLoading = false, errorMessage = throwable.localizedMessage )
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
                val cars = carsRepository.getCars()
                _state.update {
                    it.copy(isRefreshing = false, errorMessage = null, cars = cars)
                }
            }.onFailure { throwable ->
                _state.update { state ->
                    state.copy(isRefreshing = false, errorMessage = throwable.localizedMessage)
                }
            }
        }
    }
}