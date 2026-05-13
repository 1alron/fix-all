package io.alron.fixall.data.repository

import io.alron.fixall.data.remote.api.AppointmentsApi
import io.alron.fixall.data.remote.dto.*
import io.alron.fixall.domain.model.Appointment
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class AppointmentsRepositoryImplTest {

    private val api: AppointmentsApi = mockk()
    private lateinit var repository: AppointmentsRepositoryImpl

    @Before
    fun setup() {
        repository = AppointmentsRepositoryImpl(api)
    }

    @Test
    fun `getAppointments success emits to flow and returns list`() = runTest {
        val dto = AppointmentDto(id = "1", status = "SCHEDULED")
        coEvery { api.getAppointments() } returns listOf(dto)

        val result = repository.getAppointments()

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals("1", result.getOrNull()?.get(0)?.id)
    }

    @Test
    fun `getUpcomingAppointment success with data returns appointment`() = runTest {
        val dto = AppointmentDto(id = "upcoming_1")
        coEvery { api.getUpcomingAppointment() } returns dto

        val result = repository.getUpcomingAppointment()

        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
        assertEquals("upcoming_1", result.getOrNull()?.id)
    }

    @Test
    fun `getUpcomingAppointment success with no data returns null`() = runTest {
        val dto = AppointmentDto(id = null)
        coEvery { api.getUpcomingAppointment() } returns dto

        val result = repository.getUpcomingAppointment()

        assertTrue(result.isSuccess)
        assertNull(result.getOrNull())
    }

    @Test
    fun `cancelAppointment success returns message`() = runTest {
        val response = CancelAppointmentResponseDto(success = true, message = "Cancelled")
        coEvery { api.cancelAppointment(any()) } returns response
        coEvery { api.getAppointments() } returns emptyList()
        coEvery { api.getUpcomingAppointment() } returns AppointmentDto()

        val result = repository.cancelAppointment("1")

        assertTrue(result.isSuccess)
        assertEquals("Cancelled", result.getOrNull())
    }

    @Test
    fun `initiatePayment returns payment url on success`() = runTest {
        val response = PaymentResponseDto(paymentUrl = "https://pay.me")
        coEvery { api.initiatePayment(any(), any()) } returns response

        val result = repository.initiatePayment("a1", 0.0)

        assertTrue(result.isSuccess)
        assertEquals("https://pay.me", result.getOrNull())
    }

    @Test
    fun `getAvailableTimeSlots returns mapped slots`() = runTest {
        val dto = AvailableTimeSlotsDto(slots = listOf("09:00", "10:00"))
        coEvery { api.getAvailableTimeSlots(any(), any(), any()) } returns dto

        val result = repository.getAvailableTimeSlots("b1", "s1", "2024-01-01")

        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.slots?.size)
    }
}
