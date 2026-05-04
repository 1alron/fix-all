package io.alron.fixall.presentation.appointments.details

import io.alron.fixall.domain.model.Appointment
import io.alron.fixall.domain.model.LoyaltyInfo
import io.alron.fixall.data.remote.dto.PaymentStatusDto

data class AppointmentDetailsState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val appointment: Appointment? = null,
    val errorMessage: String? = null,
    val isCancelling: Boolean = false,
    val isPaying: Boolean = false,
    val loyaltyInfo: LoyaltyInfo? = null,
    val paymentStatus: PaymentStatusDto? = null,
    val isCheckingPayment: Boolean = false
)
