package io.alron.fixall.presentation.appointments.create

import io.alron.fixall.domain.model.Branch
import io.alron.fixall.domain.model.Car
import io.alron.fixall.domain.model.Service
import io.alron.fixall.domain.model.TimeSlots

data class CreateAppointmentState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    
    val branches: List<Branch> = emptyList(),
    val selectedBranch: Branch? = null,
    
    val availableServices: List<Service> = emptyList(),
    val selectedService: Service? = null,
    
    val userCars: List<Car> = emptyList(),
    val selectedCar: Car? = null,
    
    val selectedDate: String = "", // YYYY-MM-DD
    val availableTimeSlots: TimeSlots? = null,
    val selectedTime: String = "", // HH:MM
    
    val notes: String = "",
    
    val errorMessage: String? = null
)
