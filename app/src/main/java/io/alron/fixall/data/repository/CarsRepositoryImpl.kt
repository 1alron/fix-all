package io.alron.fixall.data.repository

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import io.alron.fixall.data.remote.api.CarsApi
import io.alron.fixall.data.remote.dto.AddCarResponseDto
import io.alron.fixall.data.remote.mappers.toDomain
import io.alron.fixall.domain.model.Car
import io.alron.fixall.domain.model.CarBrand
import io.alron.fixall.domain.model.CarModel
import io.alron.fixall.domain.repository.CarsRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class CarsRepositoryImpl @Inject constructor(
    private val carsApi: CarsApi,
    private val context: Context,
    private val gson: Gson
) : CarsRepository {
    override suspend fun getCars(): Result<List<Car>> {
        return try {
            val carsDto = carsApi.getCars()
            Result.success(carsDto.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addCar(
        modelId: String,
        year: Int,
        licensePlate: String,
        vin: String?,
        imageUri: Uri?
    ): Result<Car> {
        return try {
            val modelIdBody = modelId.toRequestBody("text/plain".toMediaTypeOrNull())
            val yearBody = year.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val licensePlateBody = licensePlate.toRequestBody("text/plain".toMediaTypeOrNull())
            val vinBody = vin?.toRequestBody("text/plain".toMediaTypeOrNull())

            val photoPart = imageUri?.let { uri ->
                val file = uriToFile(context, uri)
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("photo", file.name, requestFile)
            }

            val response = carsApi.addCar(
                modelId = modelIdBody,
                year = yearBody,
                licensePlate = licensePlateBody,
                vin = vinBody,
                photo = photoPart
            )

            if (response.success && response.data != null) {
                Result.success(response.data.toDomain())
            } else {
                val errorMsg = response.errors?.values?.firstOrNull()?.toString() ?: response.message ?: "Failed to add car"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: HttpException) {
            Result.failure(parseError(e))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateCar(
        id: String,
        modelId: String,
        year: Int,
        licensePlate: String,
        vin: String?,
        imageUri: Uri?
    ): Result<Car> {
        return try {
            val modelIdBody = modelId.toRequestBody("text/plain".toMediaTypeOrNull())
            val yearBody = year.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val licensePlateBody = licensePlate.toRequestBody("text/plain".toMediaTypeOrNull())
            val vinBody = vin?.toRequestBody("text/plain".toMediaTypeOrNull())

            val photoPart = imageUri?.let { uri ->
                if (uri.scheme == "content" || uri.scheme == "file") {
                    val file = uriToFile(context, uri)
                    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("photo", file.name, requestFile)
                } else null
            }

            val response = carsApi.updateCar(
                id = id,
                modelId = modelIdBody,
                year = yearBody,
                licensePlate = licensePlateBody,
                vin = vinBody,
                photo = photoPart
            )

            if (response.success && response.data != null) {
                Result.success(response.data.toDomain())
            } else {
                val errorMsg = response.errors?.values?.firstOrNull()?.toString() ?: response.message ?: "Failed to update car"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: HttpException) {
            Result.failure(parseError(e))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseError(e: HttpException): Exception {
        return try {
            val errorBody = e.response()?.errorBody()?.string()
            val errorRes = gson.fromJson(errorBody, AddCarResponseDto::class.java)
            
            val fieldError = errorRes.errors?.let { errors ->
                val firstKey = errors.keys.firstOrNull()
                val errorValue = errors[firstKey]
                if (errorValue is List<*>) errorValue.firstOrNull()?.toString()
                else errorValue?.toString()
            }
            
            Exception(fieldError ?: errorRes.message ?: "Server error")
        } catch (_: Exception) {
            Exception("An unexpected error occurred")
        }
    }

    override suspend fun deleteCar(id: String): Result<Unit> {
        return try {
            val response = carsApi.deleteCar(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete car"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getBrands(): Result<List<CarBrand>> {
        return try {
            val brandsDto = carsApi.getBrands()
            Result.success(brandsDto.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getModels(brandId: String?): Result<List<CarModel>> {
        return try {
            val modelsDto = carsApi.getModels(brandId)
            Result.success(modelsDto.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun uriToFile(context: Context, uri: Uri): File {
        val file = File(context.cacheDir, "temp_image_\${System.currentTimeMillis()}.jpg")
        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
        return file
    }
}