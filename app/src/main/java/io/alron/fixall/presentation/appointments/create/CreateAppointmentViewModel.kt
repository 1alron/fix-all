package io.alron.fixall.presentation.appointments.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.alron.fixall.domain.model.Branch
import io.alron.fixall.domain.model.Car
import io.alron.fixall.domain.model.Service
import io.alron.fixall.domain.repository.AppointmentsRepository
import io.alron.fixall.domain.repository.BranchesRepository
import io.alron.fixall.domain.repository.CarsRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CreateAppointmentEvent {
    data class ShowToast(val message: String) : CreateAppointmentEvent()
    object AppointmentCreated : CreateAppointmentEvent()
    object NavigateToAddCar : CreateAppointmentEvent()
}

@HiltViewModel
class CreateAppointmentViewModel @Inject constructor(
    private val appointmentsRepository: AppointmentsRepository,
    private val branchesRepository: BranchesRepository,
    private val carsRepository: CarsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CreateAppointmentState())
    val state = _state.asStateFlow()

    private val _eventChannel = Channel<CreateAppointmentEvent>()
    val events = _eventChannel.receiveAsFlow()

    init {
        loadInitialData()
    }

    fun refreshCars() {
        viewModelScope.launch {
            carsRepository.getCars().onSuccess { cars ->
                _state.update { it.copy(userCars = cars) }
            }
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val branchesResult = branchesRepository.getBranches()
            val carsResult = carsRepository.getCars()

            _state.update {
                it.copy(
                    isLoading = false,
                    branches = branchesResult.getOrDefault(emptyList()),
                    userCars = carsResult.getOrDefault(emptyList())
                )
            }
        }
    }

    fun onBranchSelected(branch: Branch) {
        _state.update {
            it.copy(
                selectedBranch = branch,
                selectedService = null,
                availableServices = emptyList(),
                availableTimeSlots = null,
                selectedTime = ""
            )
        }
        loadServices(branch.id)
    }

    private fun loadServices(branchId: String) {
        viewModelScope.launch {
            appointmentsRepository.getAvailableServices(branchId)
                .onSuccess { services ->
                    _state.update { it.copy(availableServices = services) }
                }
        }
    }

    fun onServiceSelected(service: Service) {
        _state.update {
            it.copy(
                selectedService = service,
                availableTimeSlots = null,
                selectedTime = ""
            )
        }
        loadTimeSlots()
    }

    fun onCarSelected(car: Car) {
        _state.update { it.copy(selectedCar = car) }
    }

    fun onDateStringSelected(date: String) {
        _state.update { it.copy(selectedDate = date, availableTimeSlots = null, selectedTime = "") }
        loadTimeSlots()
    }

    fun onTimeSelected(time: String) {
        _state.update { it.copy(selectedTime = time) }
    }

    fun onNotesChanged(notes: String) {
        _state.update { it.copy(notes = notes) }
    }

    private fun loadTimeSlots() {
        val state = _state.value
        if (state.selectedBranch != null && state.selectedService != null && state.selectedDate.isNotBlank()) {
            viewModelScope.launch {
                appointmentsRepository.getAvailableTimeSlots(
                    state.selectedBranch.id,
                    state.selectedService.id,
                    state.selectedDate
                ).onSuccess { slots ->
                    _state.update { it.copy(availableTimeSlots = slots) }
                }.onFailure { throwable ->
                    _eventChannel.send(
                        CreateAppointmentEvent.ShowToast(
                            throwable.localizedMessage ?: "Error loading slots"
                        )
                    )
                }
            }
        }
    }

    fun createAppointment() {
        val state = _state.value
        if (state.selectedCar == null || state.selectedService == null ||
            state.selectedBranch == null || state.selectedDate.isBlank() || state.selectedTime.isBlank()
        ) {
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            appointmentsRepository.createAppointment(
                carId = state.selectedCar.id,
                serviceTypeId = state.selectedService.id,
                serviceCenterId = state.selectedBranch.id,
                scheduledDate = state.selectedDate,
                scheduledTime = state.selectedTime,
                notes = state.notes.takeIf { it.isNotBlank() }
            ).onSuccess {
                _eventChannel.send(CreateAppointmentEvent.ShowToast("Запись успешно создана"))
                _eventChannel.send(CreateAppointmentEvent.AppointmentCreated)
            }.onFailure { throwable ->
                _eventChannel.send(
                    CreateAppointmentEvent.ShowToast(
                        throwable.localizedMessage ?: "Error creating appointment"
                    )
                )
            }
            _state.update { it.copy(isSaving = false) }
        }
    }
}
