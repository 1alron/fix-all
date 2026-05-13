package io.alron.fixall.data.repository

import io.alron.fixall.data.remote.api.ProfileApi
import io.alron.fixall.data.remote.dto.*
import io.alron.fixall.domain.model.User
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ProfileRepositoryImplTest {

    private val api: ProfileApi = mockk()
    private lateinit var repository: ProfileRepositoryImpl

    @Before
    fun setup() {
        repository = ProfileRepositoryImpl(api)
    }

    @Test
    fun `getMe returns success and updates currentUser flow`() = runTest {
        val userDto = UserDto(
            id = 1,
            username = "user",
            email = "email",
            firstName = "F",
            lastName = "L",
            fullName = "F L",
            isStaff = false,
            profile = UserProfileDto(null, null, null)
        )
        coEvery { api.getMe() } returns userDto

        val result = repository.getMe()

        assertTrue(result.isSuccess)
        assertEquals("user", repository.currentUser.value?.username)
    }

    @Test
    fun `updateProfile success updates currentUser flow`() = runTest {
        val updatedUserDto = UserDto(
            id = 1,
            username = "updated",
            email = "email",
            firstName = "F",
            lastName = "L",
            fullName = "F L",
            isStaff = false,
            profile = UserProfileDto(null, null, null)
        )
        val response = UpdateProfileResponseDto(
            success = true,
            message = "Success",
            data = updatedUserDto,
            errors = null
        )
        coEvery { api.updateProfile(any()) } returns response

        val result = repository.updateProfile(username = "updated")

        assertTrue(result.isSuccess)
        assertEquals("updated", repository.currentUser.value?.username)
    }

    @Test
    fun `getStats returns mapped UserStats`() = runTest {
        val statsDto = UserStatsDto(
            totalAppointments = 5,
            completedAppointments = 3,
            cancelledAppointments = 1,
            totalSpent = 100.0,
            averageCheck = 20.0,
            firstVisit = null,
            lastVisit = null,
            visitsByMonth = emptyMap(),
            topServices = emptyList(),
            topCenters = emptyList(),
            byWeekday = emptyList(),
            byHour = emptyList()
        )
        coEvery { api.getStats() } returns statsDto

        val result = repository.getStats()

        assertTrue(result.isSuccess)
        assertEquals(5, result.getOrNull()?.totalAppointments)
    }

    @Test
    fun `getLoyalty returns mapped LoyaltyInfo`() = runTest {
        val loyaltyDto = LoyaltyInfoDto(
            status = "SILVER",
            statusDisplay = "Silver",
            bonusBalance = 50.0,
            totalSpent = 1000.0,
            nextStatus = "GOLD",
            nextStatusProgress = 0.5,
            personalDiscount = 2.0,
            statusDiscount = 3.0,
            totalDiscount = 5.0
        )
        coEvery { api.getLoyalty() } returns loyaltyDto

        val result = repository.getLoyalty()

        assertTrue(result.isSuccess)
        assertEquals("SILVER", result.getOrNull()?.status)
    }

    @Test
    fun `clearCache sets currentUser to null`() {
        repository.clearCache()
        assertEquals(null, repository.currentUser.value)
    }
}
