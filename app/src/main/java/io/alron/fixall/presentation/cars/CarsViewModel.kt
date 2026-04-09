package io.alron.fixall.presentation.cars

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.alron.fixall.domain.repository.CarsRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CarsEvent {
    data class ShowSnackbar(val message: String) : CarsEvent()
}

@HiltViewModel
class CarsViewModel @Inject constructor(
    private val carsRepository: CarsRepository
) : ViewModel() {
    private val _state = MutableStateFlow(CarsState())
    val state = _state.asStateFlow()

    private val _eventChannel = Channel<CarsEvent>()
    val events = _eventChannel.receiveAsFlow()

    init {
        getCars()
    }

    fun getCars() {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true, errorMessage = null)
            }
            carsRepository.getCars()
                .onSuccess { cars ->
                    _state.update {
                        it.copy(isLoading = false, errorMessage = null, cars = cars)
                    }
                }
                .onFailure { throwable ->
                    _state.update { state ->
                        state.copy(isLoading = false, errorMessage = throwable.localizedMessage)
                    }
                }
        }
    }

    fun deleteCar(id: String) {
        viewModelScope.launch {
            carsRepository.deleteCar(id)
                .onSuccess {
                    _eventChannel.send(CarsEvent.ShowSnackbar("Автомобиль успешно удален"))
                    getCars()
                }
                .onFailure { throwable ->
                    _state.update { state ->
                        state.copy(errorMessage = throwable.localizedMessage)
                    }
                }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _state.update {
                it.copy(isRefreshing = true, errorMessage = null)
            }
            carsRepository.getCars()
                .onSuccess { cars ->
                    _state.update {
                        it.copy(isRefreshing = false, errorMessage = null, cars = cars)
                    }
                }
                .onFailure { throwable ->
                    _state.update { state ->
                        state.copy(isRefreshing = false, errorMessage = throwable.localizedMessage)
                    }
                }
        }
    }
}
