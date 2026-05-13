package io.alron.fixall.data.repository

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import io.alron.fixall.data.remote.api.CarsApi
import io.alron.fixall.data.remote.dto.AddCarResponseDto
import io.alron.fixall.data.remote.dto.CarBrandDto
import io.alron.fixall.data.remote.dto.CarDto
import io.mockk.*
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class CarsRepositoryImplTest {

    private val api: CarsApi = mockk()
    private val context: Context = mockk()
    private val gson = Gson()
    private lateinit var repository: CarsRepositoryImpl

    @Before
    fun setup() {
        repository = CarsRepositoryImpl(api, context, gson)
    }

    @Test
    fun `getCars returns success when api returns data`() = runTest {
        val carDto = CarDto(
            id = "1", model = "m1", model_name = "Model", brand_name = "Brand",
            year = 2020, license_plate = "A123BC", vin = null, photo = null, photo_url = null
        )
        coEvery { api.getCars() } returns listOf(carDto)

        val result = repository.getCars()

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals("A123BC", result.getOrNull()?.get(0)?.licensePlate)
    }

    @Test
    fun `addCar handles server field error correctly`() = runTest {
        val errorJson = """{"success": false, "message": "error", "errors": {"license_plate": ["Invalid format"]}}"""
        val response = Response.error<AddCarResponseDto>(400, errorJson.toResponseBody("application/json".toMediaTypeOrNull()))
        val exception = HttpException(response)
        
        coEvery { api.addCar(any(), any(), any(), any(), any()) } throws exception

        val result = repository.addCar("m1", 2020, "invalid", null, null)

        assertTrue(result.isFailure)
        assertEquals("Invalid format", result.exceptionOrNull()?.message)
    }

    @Test
    fun `deleteCar returns success on 204 no content`() = runTest {
        val responseBody = "".toResponseBody("text/plain".toMediaTypeOrNull())
        val response = Response.success<ResponseBody>(204, responseBody)
        coEvery { api.deleteCar(any()) } returns response

        val result = repository.deleteCar("123")

        assertTrue(result.isSuccess)
    }

    @Test
    fun `getBrands returns mapped domain models`() = runTest {
        val brands = listOf(CarBrandDto("1", "Citroen"))
        coEvery { api.getBrands() } returns brands

        val result = repository.getBrands()

        assertTrue(result.isSuccess)
        assertEquals("Citroen", result.getOrNull()?.get(0)?.name)
    }

    @Test
    fun `updateCar success returns updated domain model`() = runTest {
        val updatedDto = CarDto(
            id = "1", model = "m1", model_name = "Model", brand_name = "Brand",
            year = 2021, license_plate = "A123BC", vin = null, photo = null, photo_url = null
        )
        val apiResponse = AddCarResponseDto(success = true, message = "success", data = updatedDto, errors = emptyMap())
        coEvery { api.updateCar(any(), any(), any(), any(), any(), any()) } returns apiResponse

        val result = repository.updateCar("1", "m1", 2021, "A123BC", null, null)

        assertTrue(result.isSuccess)
        assertEquals(2021, result.getOrNull()?.year)
    }
}
