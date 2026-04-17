package io.alron.fixall.presentation.profile.stats

import io.alron.fixall.domain.model.UserStats

data class StatsState(
    val isLoading: Boolean = false,
    val stats: UserStats? = null,
    val errorMessage: String? = null
)
