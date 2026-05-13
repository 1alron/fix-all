package io.alron.fixall.data.remote.mappers

import io.alron.fixall.data.remote.dto.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppointmentMappersTest {

    @Test
    fun appointmentCarDtoToDomain_mapsCorrectly() {
        val dto = AppointmentCarDto(
            id = "c1",
            modelName = "Berlingo",
            brandName = "Citroen",
            year = 2020,
            licensePlate = "H777HH",
            vin = "VIN123",
            photoUrl = "url"
        )
        val domain = dto.toDomain()

        assertEquals(dto.id, domain.id)
        assertEquals(dto.modelName, domain.modelName)
        assertEquals(dto.brandName, domain.brandName)
        assertEquals(dto.year, domain.year)
        assertEquals(dto.licensePlate, domain.licensePlate)
        assertEquals(dto.vin, domain.vin)
        assertEquals(dto.photoUrl, domain.photoUrl)
    }

    @Test
    fun appointmentServiceDtoToDomain_mapsCorrectly() {
        val dto = AppointmentServiceDto(
            id = "s1",
            name = "Service",
            description = "Desc",
            duration = 60,
            price = "100"
        )
        val domain = dto.toDomain()

        assertEquals(dto.id, domain.id)
        assertEquals(dto.name, domain.name)
        assertEquals(dto.description, domain.description)
        assertEquals(dto.duration, domain.duration)
        assertEquals(dto.price, domain.price)
    }

    @Test
    fun appointmentServiceCenterDtoToDomain_mapsCorrectly() {
        val dto = AppointmentServiceCenterDto(
            id = "sc1",
            address = "Address",
            phone = "123",
            openingHours = "9-18",
            photoUrl = "url"
        )
        val domain = dto.toDomain()

        assertEquals(dto.id, domain.id)
        assertEquals(dto.address, domain.address)
        assertEquals(dto.phone, domain.phone)
        assertEquals(dto.openingHours, domain.openingHours)
        assertEquals(dto.photoUrl, domain.photoUrl)
    }

    @Test
    fun appointmentDtoToDomain_mapsCorrectly() {
        val dto = AppointmentDto(
            id = "a1",
            car = null,
            serviceType = null,
            serviceCenter = null,
            scheduledDate = "2024-05-20",
            scheduledTime = "14:30:00",
            endTime = "15:30:00",
            status = "SCHEDULED",
            statusDisplay = "Scheduled",
            notes = "Notes",
            createdAt = "2024-05-19T10:00:00",
            updatedAt = "2024-05-19T10:00:00",
            canCancel = true,
            totalPrice = 150.0,
            paymentUrl = "url",
            paymentStatus = "pending",
            isPaid = false,
            paidAt = null
        )
        val domain = dto.toDomain()

        assertEquals("a1", domain.id)
        assertEquals("20.05.2024", domain.scheduledDate)
        assertEquals("14:30", domain.scheduledTime)
        assertEquals("SCHEDULED", domain.status)
        assertTrue(domain.canCancel)
        assertEquals("150.0", domain.totalPrice)
    }

    @Test
    fun appointmentDtoToDomain_handlesNulls() {
        val dto = AppointmentDto(
            id = null,
            car = null,
            serviceType = null,
            serviceCenter = null,
            scheduledDate = null,
            scheduledTime = null,
            endTime = null,
            status = null,
            statusDisplay = null,
            notes = null,
            createdAt = "",
            updatedAt = "",
            canCancel = null,
            totalPrice = null,
            paymentUrl = null,
            paymentStatus = null,
            isPaid = null,
            paidAt = null
        )
        val domain = dto.toDomain()

        assertEquals("", domain.id)
        assertEquals("", domain.scheduledDate)
        assertEquals("", domain.scheduledTime)
        assertFalse(domain.canCancel)
        assertEquals("0", domain.totalPrice)
        assertFalse(domain.isPaid)
    }
}
