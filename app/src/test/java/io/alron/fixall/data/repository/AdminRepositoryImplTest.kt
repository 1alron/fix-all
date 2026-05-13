package io.alron.fixall.data.repository

import io.alron.fixall.data.remote.api.AdminApi
import io.alron.fixall.data.remote.dto.*
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AdminRepositoryImplTest {

    private val api: AdminApi = mockk()
    private lateinit var repository: AdminRepositoryImpl

    @Before
    fun setup() {
        repository = AdminRepositoryImpl(api)
    }

    @Test
    fun `getDashboardStats returns success and maps DTO to domain`() = runTest {
        val dto = AdminDashboardDto(
            totalAppointments = 10,
            activeServices = 5,
            totalClients = 100,
            totalCenters = 2,
            upcoming = emptyList()
        )
        coEvery { api.getDashboardStats() } returns dto

        val result = repository.getDashboardStats()

        assertTrue(result.isSuccess)
        assertEquals(10, result.getOrNull()?.totalAppointments)
    }

    @Test
    fun `getDashboardStats returns failure on api exception`() = runTest {
        coEvery { api.getDashboardStats() } throws Exception("API Error")

        val result = repository.getDashboardStats()

        assertTrue(result.isFailure)
        assertEquals("API Error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getAppointments returns mapped list`() = runTest {
        val dto = AdminAppointmentListItemDto(
            id = "1",
            scheduledDate = "2024-05-20",
            scheduledTime = "10:00:00",
            carInfo = "Car",
            clientName = "Client",
            clientEmail = "email",
            serviceName = "Service",
            centerAddress = "Addr",
            status = "SCHEDULED",
            statusDisplay = "Scheduled",
            notes = null
        )
        coEvery { api.getAppointments(any()) } returns listOf(dto)

        val result = repository.getAppointments(emptyMap())

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals("20.05.2024", result.getOrNull()?.get(0)?.scheduledDate)
    }

    @Test
    fun `changeStatus returns success on api success`() = runTest {
        coEvery { api.changeStatus(any(), any()) } returns Unit

        val result = repository.changeStatus("1", "COMPLETED")

        assertTrue(result.isSuccess)
    }

    @Test
    fun `deleteService returns success on api success`() = runTest {
        coEvery { api.deleteService(any()) } returns Unit

        val result = repository.deleteService("1")

        assertTrue(result.isSuccess)
    }
}
