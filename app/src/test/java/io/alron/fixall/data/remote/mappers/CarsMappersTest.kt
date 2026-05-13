package io.alron.fixall.data.remote.mappers

import io.alron.fixall.data.remote.dto.CarBrandDto
import io.alron.fixall.data.remote.dto.CarDto
import io.alron.fixall.data.remote.dto.CarModelDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CarsMappersTest {

    @Test
    fun carDtoToDomain_mapsBasicFieldsCorrectly() {
        val dto = CarDto(
            id = "1",
            model = "101",
            model_name = "Berlingo",
            brand_name = "Citroen",
            year = 2020,
            license_plate = "h777hh",
            vin = "VIN123",
            photo = null,
            photo_url = "http://example.com/photo.jpg"
        )
        val domain = dto.toDomain()

        assertEquals(dto.id, domain.id)
        assertEquals(dto.model, domain.modelId)
        assertEquals("Berlingo", domain.modelName)
        assertEquals("Citroen", domain.brandName)
        assertEquals(2020, domain.year)
    }

    @Test
    fun carDtoToDomain_licensePlateIsUppercased() {
        val dto = CarDto(
            id = "1",
            model = "101",
            model_name = "Berlingo",
            brand_name = "Citroen",
            year = 2020,
            license_plate = "h777hh",
            vin = "VIN123",
            photo = null,
            photo_url = "http://example.com/photo.jpg"
        )
        val domain = dto.toDomain()

        assertEquals("H777HH", domain.licensePlate)
    }

    @Test
    fun carDtoToDomain_emptyVinMappedToNull() {
        val dto = CarDto(
            id = "1",
            model = "101",
            model_name = "Berlingo",
            brand_name = "Citroen",
            year = 2020,
            license_plate = "H777HH",
            vin = "",
            photo = null,
            photo_url = "url"
        )
        val domain = dto.toDomain()

        assertNull(domain.vin)
    }

    @Test
    fun carDtoToDomain_blankVinMappedToNull() {
        val dto = CarDto(
            id = "1",
            model = "101",
            model_name = "Berlingo",
            brand_name = "Citroen",
            year = 2020,
            license_plate = "H777HH",
            vin = "   ",
            photo = null,
            photo_url = "url"
        )
        val domain = dto.toDomain()

        assertNull(domain.vin)
    }

    @Test
    fun carBrandDtoToDomain_mapsCorrectly() {
        val dto = CarBrandDto(id = "5", name = "Citroen")
        val domain = dto.toDomain()

        assertEquals("5", domain.id)
        assertEquals("Citroen", domain.name)
    }

    @Test
    fun carModelDtoToDomain_mapsCorrectly() {
        val dto = CarModelDto(id = "101", name = "Berlingo", brandId = "5", brandName = "Citroen")
        val domain = dto.toDomain()

        assertEquals("101", domain.id)
        assertEquals("Berlingo", domain.name)
        assertEquals("5", domain.brandId)
        assertEquals("Citroen", domain.brandName)
    }
}
