package io.alron.fixall.data.repository

import io.alron.fixall.data.remote.api.AppointmentsApi
import io.alron.fixall.data.remote.dto.*
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AppointmentsRepositoryImplDetailsTest {

    private val api: AppointmentsApi = mockk()
    private lateinit var repository: AppointmentsRepositoryImpl

    @Before
    fun setup() {
        repository = AppointmentsRepositoryImpl(api)
    }

    @Test
    fun `getAppointmentDetails returns mapped appointment on success`() = runTest {
        val dto = AppointmentDto(
            id = "a1",
            scheduledDate = "2024-05-20",
            scheduledTime = "14:30:00",
            status = "IN_PROGRESS",
            totalPrice = 100.0
        )
        coEvery { api.getAppointmentDetails("a1") } returns dto

        val result = repository.getAppointmentDetails("a1")

        assertTrue(result.isSuccess)
        assertEquals("20.05.2024", result.getOrNull()?.scheduledDate)
        assertEquals("IN_PROGRESS", result.getOrNull()?.status)
    }

    @Test
    fun `getAvailableServices returns list of services`() = runTest {
        val services = listOf(
            ServiceDto("s1", "Oil", "Desc", 30, "50.0", true)
        )
        coEvery { api.getAvailableServices("b1") } returns services

        val result = repository.getAvailableServices("b1")

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals("Oil", result.getOrNull()?.first()?.name)
    }

    @Test
    fun `getPaymentStatus returns DTO correctly`() = runTest {
        val statusDto = PaymentStatusDto(paymentId = "p1", status = "succeeded")
        coEvery { api.getPaymentStatus("a1") } returns statusDto

        val result = repository.getPaymentStatus("a1")

        assertTrue(result.isSuccess)
        assertEquals("succeeded", result.getOrNull()?.status)
    }

    @Test
    fun `syncPaymentStatus returns sync response`() = runTest {
        val syncDto = SyncPaymentStatusResponseDto(isPaid = true, paymentStatus = "succeeded")
        coEvery { api.syncPaymentStatus("a1") } returns syncDto

        val result = repository.syncPaymentStatus("a1")

        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()?.isPaid == true)
    }

    @Test
    fun `getAppointmentHistory returns mapped list`() = runTest {
        val dto = AppointmentDto(id = "h1", scheduledDate = "2023-01-01")
        coEvery { api.getAppointmentHistory(1) } returns listOf(dto)

        val result = repository.getAppointmentHistory(1)

        assertTrue(result.isSuccess)
        assertEquals("01.01.2023", result.getOrNull()?.first()?.scheduledDate)
    }
}
