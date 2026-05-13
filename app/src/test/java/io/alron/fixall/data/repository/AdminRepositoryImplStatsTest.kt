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

class AdminRepositoryImplStatsTest {

    private val api: AdminApi = mockk()
    private lateinit var repository: AdminRepositoryImpl

    @Before
    fun setup() {
        repository = AdminRepositoryImpl(api)
    }

    @Test
    fun `getStatusStats returns mapped data correctly`() = runTest {
        val dto = AdminStatusStatsDto(
            period = "week",
            statuses = mapOf("COMPLETED" to StatusInfoDto(label = "Done", count = 5)),
            total = 5
        )
        coEvery { api.getStatusStats(any(), any()) } returns dto

        val result = repository.getStatusStats("week", "branch1")

        assertTrue(result.isSuccess)
        assertEquals(5, result.getOrNull()?.statuses?.get("COMPLETED")?.count)
        assertEquals("Done", result.getOrNull()?.statuses?.get("COMPLETED")?.label)
    }

    @Test
    fun `getAttendanceStats maps daily data`() = runTest {
        val dto = AdminAttendanceStatsDto(
            period = "month",
            centers = emptyList(),
            daily = listOf(AdminDailyAttendanceDto(date = "2024-05-20", label = "Mon", count = 10))
        )
        coEvery { api.getAttendanceStats(any(), any()) } returns dto

        val result = repository.getAttendanceStats("month", null)

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.daily?.size)
        assertEquals(10, result.getOrNull()?.daily?.first()?.count)
    }

    @Test
    fun `getServicePopularity returns percentage correctly`() = runTest {
        val dto = AdminServicePopularityDto(
            period = "year",
            services = listOf(ServicePopularityItemDto(name = "Oil", count = 20, percentage = 40.0))
        )
        coEvery { api.getServicePopularity(any(), any()) } returns dto

        val result = repository.getServicePopularity("year", null)

        assertTrue(result.isSuccess)
        assertEquals(40.0, result.getOrNull()?.services?.first()?.percentage ?: 0.0, 0.001)
    }

    @Test
    fun `addNote success returns notes`() = runTest {
        val response = AddNoteResponseDto(success = true, notes = "New note")
        coEvery { api.addNote(any(), any()) } returns response

        val result = repository.addNote("1", "New note")

        assertTrue(result.isSuccess)
        assertEquals("New note", result.getOrNull()?.notes)
    }

    @Test
    fun `setBranchWorkingHours returns updated domain model`() = runTest {
        val whDto = AdminWorkingHourDto(
            id = 1, dayOfWeek = 1, dayDisplay = "Mon", 
            startTime = "09:00", endTime = "18:00", 
            lunchStart = null, lunchEnd = null, isWorking = true
        )
        val response = SetWorkingHoursResponseDto(success = true, message = "Success", data = whDto)
        coEvery { api.setBranchWorkingHours(any(), any()) } returns response

        val result = repository.setBranchWorkingHours("b1", io.alron.fixall.domain.model.AdminWorkingHour(
            id = null, dayOfWeek = 1, dayDisplay = "Mon", startTime = "09:00", endTime = "18:00", 
            lunchStart = null, lunchEnd = null, isWorking = true
        ))

        assertTrue(result.isSuccess)
        assertEquals("09:00", result.getOrNull()?.startTime)
    }
}
