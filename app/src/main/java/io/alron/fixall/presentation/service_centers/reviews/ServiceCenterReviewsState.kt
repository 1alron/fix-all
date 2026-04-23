package io.alron.fixall.presentation.service_centers.reviews

import io.alron.fixall.domain.model.Review

data class ServiceCenterReviewsState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isSubmitting: Boolean = false,
    val isEligibilityChecked: Boolean = false,
    val reviews: List<Review> = emptyList(),
    val errorMessage: String? = null,
    val canLoadMore: Boolean = false,
    val currentPage: Int = 1,
    val hasCompletedAppointment: Boolean = false,
    val alreadyReviewed: Boolean = false,
    val currentUserId: String? = null,
    val currentUserFullName: String? = null
)
