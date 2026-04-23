package io.alron.fixall.presentation.appointments.details

import io.alron.fixall.domain.model.Appointment
import io.alron.fixall.domain.model.LoyaltyInfo

data class AppointmentDetailsState(
    val isLoading: Boolean = false,
    val appointment: Appointment? = null,
    val errorMessage: String? = null,
    val isCancelling: Boolean = false,
    val isPaying: Boolean = false,
    val loyaltyInfo: LoyaltyInfo? = null
)
