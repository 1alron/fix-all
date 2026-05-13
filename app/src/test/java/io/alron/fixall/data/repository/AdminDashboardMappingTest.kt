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

class AdminDashboardMappingTest {

    private val api: AdminApi = mockk()
    private lateinit var repository: AdminRepositoryImpl

    @Before
    fun setup() {
        repository = AdminRepositoryImpl(api)
    }

    @Test
    fun `getDashboardStats maps upcoming appointments with formatted date and time`() = runTest {
        val upcomingDto = AdminAppointmentDto(
            id = "a1",
            date = "2024-10-25",
            time = "14:00:00",
            client = "Ivan",
            service = "Repair",
            car = "BMW",
            center = "Center 1",
            status = "SCHEDULED",
            statusDisplay = "Запланировано"
        )
        val dto = AdminDashboardDto(
            totalAppointments = 1,
            activeServices = 1,
            totalClients = 1,
            totalCenters = 1,
            upcoming = listOf(upcomingDto)
        )
        coEvery { api.getDashboardStats() } returns dto

        val result = repository.getDashboardStats()
        val upcoming = result.getOrNull()?.upcoming?.first()

        assertEquals("25.10.2024", upcoming?.date)
        assertEquals("14:00", upcoming?.time)
        assertEquals("BMW", upcoming?.car)
    }

    @Test
    fun `getDashboardStats handles empty upcoming list`() = runTest {
        val dto = AdminDashboardDto(0, 0, 0, 0, emptyList())
        coEvery { api.getDashboardStats() } returns dto

        val result = repository.getDashboardStats()

        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()?.upcoming?.isEmpty() == true)
    }
}
