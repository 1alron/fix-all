package io.alron.fixall.data.repository

import io.alron.fixall.data.remote.api.CarsApi
import io.alron.fixall.data.remote.mappers.toDomain
import io.alron.fixall.domain.model.Car
import io.alron.fixall.domain.repository.CarsRepository

class CarsRepositoryImpl(
    private val carsApi: CarsApi
) : CarsRepository {
    override suspend fun getCars(): List<Car> {
        val carsDto = carsApi.getCars()
        val carsDomain = carsDto.map { it.toDomain() }
        return carsDomain
    }
}