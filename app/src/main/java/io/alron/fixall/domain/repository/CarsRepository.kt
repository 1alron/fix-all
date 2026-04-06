package io.alron.fixall.domain.repository

import io.alron.fixall.domain.model.Car

interface CarsRepository {
    suspend fun getCars(): List<Car>
}