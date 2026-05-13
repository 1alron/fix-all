package io.alron.fixall.data.repository

import io.alron.fixall.data.remote.api.AdminApi
import io.alron.fixall.data.remote.dto.AdminAppointmentListItemDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AdminRepositoryMappingTest {

    private val api: AdminApi = mockk()
    private lateinit var repository: AdminRepositoryImpl

    @Before
    fun setup() {
        repository = AdminRepositoryImpl(api)
    }

    @Test
    fun `getAppointments correctly formats date and time strings`() = runTest {
        val dto = AdminAppointmentListItemDto(
            id = "id-123",
            scheduledDate = "2024-12-31",
            scheduledTime = "09:45:00",
            carInfo = "Tesla Model 3",
            clientName = "Elon Musk",
            clientEmail = "elon@spacex.com",
            serviceName = "Battery Swap",
            centerAddress = "Mars Base 1",
            status = "SCHEDULED",
            statusDisplay = "Planned",
            notes = "No gravity issues"
        )
        coEvery { api.getAppointments(any()) } returns listOf(dto)

        val result = repository.getAppointments(emptyMap())
        val item = result.getOrNull()?.first()

        assertEquals("31.12.2024", item?.scheduledDate)
        assertEquals("09:45", item?.scheduledTime)
    }

    @Test
    fun `getAppointments handles null client information gracefully`() = runTest {
        val dto = AdminAppointmentListItemDto(
            id = "id-empty",
            scheduledDate = "2024-01-01",
            scheduledTime = "12:00:00",
            carInfo = "Ghost Car",
            clientName = null,
            clientEmail = null,
            serviceName = "Internal cleaning",
            centerAddress = "Main Office",
            status = "COMPLETED",
            statusDisplay = "Done",
            notes = null
        )
        coEvery { api.getAppointments(any()) } returns listOf(dto)

        val result = repository.getAppointments(emptyMap())
        val item = result.getOrNull()?.first()

        assertEquals(null, item?.clientName)
        assertEquals(null, item?.clientEmail)
    }
}
