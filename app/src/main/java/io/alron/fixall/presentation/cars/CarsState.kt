package io.alron.fixall.presentation.cars

import io.alron.fixall.domain.model.Car

data class CarsState(
    val cars: List<Car> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
)
