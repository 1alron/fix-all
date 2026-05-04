package io.alron.fixall.data.remote.api

import io.alron.fixall.data.remote.dto.AddCarResponseDto
import io.alron.fixall.data.remote.dto.CarBrandDto
import io.alron.fixall.data.remote.dto.CarDto
import io.alron.fixall.data.remote.dto.CarModelDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface CarsApi {
    @GET("/api/cars/")
    suspend fun getCars(): List<CarDto>

    @Multipart
    @POST("/api/cars/")
    suspend fun addCar(
        @Part("model_id") modelId: RequestBody,
        @Part("year") year: RequestBody,
        @Part("license_plate") licensePlate: RequestBody,
        @Part("vin") vin: RequestBody?,
        @Part photo: MultipartBody.Part?
    ): AddCarResponseDto

    @Multipart
    @PATCH("/api/cars/{id}/")
    suspend fun updateCar(
        @Path("id") id: String,
        @Part("model_id") modelId: RequestBody,
        @Part("year") year: RequestBody,
        @Part("license_plate") licensePlate: RequestBody,
        @Part("vin") vin: RequestBody?,
        @Part photo: MultipartBody.Part?
    ): AddCarResponseDto

    @DELETE("/api/cars/{id}/")
    suspend fun deleteCar(@Path("id") id: String): Response<ResponseBody>

    @GET("/api/car-brands/")
    suspend fun getBrands(): List<CarBrandDto>

    @GET("/api/car-models/")
    suspend fun getModels(@Query("brand_id") brandId: String? = null): List<CarModelDto>
}