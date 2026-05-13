package io.alron.fixall.data.repository

import io.alron.fixall.data.remote.api.AdminApi
import io.alron.fixall.data.remote.dto.AdminAppointmentDetailDto
import io.alron.fixall.data.remote.dto.AdminPaymentInfoDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AdminRepositoryImplDetailTest {

    private val api: AdminApi = mockk()
    private lateinit var repository: AdminRepositoryImpl

    @Before
    fun setup() {
        repository = AdminRepositoryImpl(api)
    }

    @Test
    fun `getAppointmentDetail returns success and maps full detail DTO`() = runTest {
        val paymentDto = AdminPaymentInfoDto(status = "succeeded", paidAt = "2024-05-20T10:00:00", amount = 100.0)
        val dto = AdminAppointmentDetailDto(
            id = "a1", scheduledDate = "2024-05-20", scheduledTime = "10:00:00", endTime = "11:00:00",
            carInfo = "Car", clientName = "Client", clientEmail = "Email", clientPhone = "Phone",
            serviceName = "Service", serviceDuration = 60, centerAddress = "Addr",
            status = "COMPLETED", statusDisplay = "Done", notes = "Note", vin = "VIN",
            totalPrice = 100.0, isPaid = true, paymentStatus = "succeeded",
            paymentInfo = paymentDto, createdAt = "2024-05-19", updatedAt = "2024-05-20"
        )
        coEvery { api.getAppointmentDetail("a1") } returns dto

        val result = repository.getAppointmentDetail("a1")

        assertTrue(result.isSuccess)
        val data = result.getOrNull()
        assertEquals("a1", data?.id)
        assertNotNull(data?.paymentInfo)
        assertEquals(100.0, data?.paymentInfo?.amount ?: 0.0, 0.001)
    }

    @Test
    fun `getAppointmentDetail handles null paymentInfo gracefully`() = runTest {
        val dto = AdminAppointmentDetailDto(
            id = "a1", scheduledDate = "2024-05-20", scheduledTime = "10:00:00", endTime = "11:00:00",
            carInfo = "Car", clientName = "Client", clientEmail = "Email", clientPhone = "Phone",
            serviceName = "Service", serviceDuration = 60, centerAddress = "Addr",
            status = "SCHEDULED", statusDisplay = "Planned", notes = null, vin = null,
            totalPrice = 50.0, isPaid = false, paymentStatus = null,
            paymentInfo = null, createdAt = "2024-05-19", updatedAt = "2024-05-19"
        )
        coEvery { api.getAppointmentDetail("a1") } returns dto

        val result = repository.getAppointmentDetail("a1")

        assertTrue(result.isSuccess)
        assertEquals(null, result.getOrNull()?.paymentInfo)
    }
}
