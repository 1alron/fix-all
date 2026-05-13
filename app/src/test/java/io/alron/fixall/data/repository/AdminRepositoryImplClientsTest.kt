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

class AdminRepositoryImplClientsTest {

    private val api: AdminApi = mockk()
    private lateinit var repository: AdminRepositoryImpl

    @Before
    fun setup() {
        repository = AdminRepositoryImpl(api)
    }

    @Test
    fun `getClients returns success and maps DTOs`() = runTest {
        val dto = AdminClientListItemDto(
            id = 1, username = "user1", fullName = "John Doe", email = "test@test.com",
            phone = "123", address = null, carsCount = 1, appointmentsCount = 2,
            activeAppointmentsCount = 0, dateJoined = "2024-01-01", isStaff = false
        )
        coEvery { api.getClients(any()) } returns listOf(dto)

        val result = repository.getClients(emptyMap())

        assertTrue(result.isSuccess)
        assertEquals("user1", result.getOrNull()?.first()?.username)
        assertEquals(1, result.getOrNull()?.size)
    }

    @Test
    fun `getClientDetail maps complex client information correctly`() = runTest {
        val dto = AdminClientDetailDto(
            id = 1, username = "user1", fullName = "Name", email = "email",
            phone = "phone", address = "addr", dateJoined = "date", isStaff = true,
            carsCount = 1, appointmentsCount = 5, totalPaid = 1000.0,
            cars = emptyList(), recentAppointments = emptyList(),
            topServices = emptyList(), topCenters = emptyList(),
            weekdayCounts = emptyList(), hourCounts = emptyList()
        )
        coEvery { api.getClientDetail(1) } returns dto

        val result = repository.getClientDetail(1)

        assertTrue(result.isSuccess)
        assertEquals("Name", result.getOrNull()?.fullName)
        assertTrue(result.getOrNull()?.isStaff == true)
    }

    @Test
    fun `updateClient returns success on api success`() = runTest {
        coEvery { api.updateClient(any(), any()) } returns SuccessResponseDto(success = true)

        val result = repository.updateClient(1, mockk())

        assertTrue(result.isSuccess)
    }

    @Test
    fun `deleteClient returns success on api success`() = runTest {
        coEvery { api.deleteClient(any()) } returns SuccessResponseDto(success = true)

        val result = repository.deleteClient(1)

        assertTrue(result.isSuccess)
    }
}
