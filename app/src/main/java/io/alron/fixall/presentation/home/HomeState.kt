package io.alron.fixall.presentation.home

import io.alron.fixall.domain.model.Appointment
import io.alron.fixall.domain.model.LoyaltyInfo
import io.alron.fixall.domain.model.User
import io.alron.fixall.domain.model.UserStats

data class HomeState(
    val user: User? = null,
    val upcomingAppointment: Appointment? = null,
    val loyaltyInfo: LoyaltyInfo? = null,
    val userStats: UserStats? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
)
