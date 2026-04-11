package io.alron.fixall.presentation.service_centers.reviews

import io.alron.fixall.domain.model.Review

data class ServiceCenterReviewsState(
    val isLoading: Boolean = false,
    val reviews: List<Review> = emptyList(),
    val errorMessage: String? = null,
    val isRefreshing: Boolean = false,
    val canLoadMore: Boolean = false,
    val currentPage: Int = 1
)
