package io.alron.fixall.data.remote.api

import io.alron.fixall.data.remote.dto.CarDto
import retrofit2.http.GET

interface CarsApi {
    @GET("/api/cars/")
    suspend fun getCars(): List<CarDto>
}