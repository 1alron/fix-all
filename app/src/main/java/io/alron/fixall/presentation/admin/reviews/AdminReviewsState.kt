package io.alron.fixall.presentation.admin.reviews

import io.alron.fixall.domain.model.AdminReviewListItem
import io.alron.fixall.domain.model.Branch

data class AdminReviewsState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val reviews: List<AdminReviewListItem> = emptyList(),
    val branches: List<Branch> = emptyList(),
    val error: String? = null,
    val centerId: String? = null,
    val unansweredOnly: Boolean = false,
    val search: String = "",
    val rating: Int? = null
)
