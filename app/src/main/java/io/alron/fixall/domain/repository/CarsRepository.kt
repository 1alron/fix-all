package io.alron.fixall.domain.repository

import android.net.Uri
import io.alron.fixall.domain.model.Car
import io.alron.fixall.domain.model.CarBrand
import io.alron.fixall.domain.model.CarModel

interface CarsRepository {
    suspend fun getCars(): Result<List<Car>>
    suspend fun addCar(
        modelId: String,
        year: Int,
        licensePlate: String,
        vin: String?,
        imageUri: Uri? = null
    ): Result<Car>
    suspend fun updateCar(
        id: String,
        modelId: String,
        year: Int,
        licensePlate: String,
        vin: String?,
        imageUri: Uri? = null
    ): Result<Car>
    suspend fun deleteCar(id: String): Result<Unit>
    suspend fun getBrands(): Result<List<CarBrand>>
    suspend fun getModels(brandId: String? = null): Result<List<CarModel>>
}