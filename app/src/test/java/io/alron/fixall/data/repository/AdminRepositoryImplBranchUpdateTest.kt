package io.alron.fixall.data.repository

import io.alron.fixall.data.remote.api.AdminApi
import io.alron.fixall.data.remote.dto.AdminBranchListItemDto
import io.alron.fixall.data.remote.dto.UpdatePhotoResponseDto
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

class AdminRepositoryImplBranchUpdateTest {

    private val api: AdminApi = mockk()
    private lateinit var repository: AdminRepositoryImpl

    @Before
    fun setup() {
        repository = AdminRepositoryImpl(api)
    }

    @Test
    fun `createBranch returns success and mapped branch`() = runTest {
        val dto = AdminBranchListItemDto(
            id = "new-b", address = "New Addr", phone = "777",
            openingHours = "10-20", photoUrl = null, photo = null,
            servicesCount = 0, workingHours = null
        )
        coEvery { api.createBranch(any()) } returns dto

        val result = repository.createBranch("New Addr", "777", "10-20")

        assertTrue(result.isSuccess)
        assertEquals("new-b", result.getOrNull()?.id)
        assertEquals("New Addr", result.getOrNull()?.address)
    }

    @Test
    fun `updateBranch returns success and updated branch`() = runTest {
        val dto = AdminBranchListItemDto(
            id = "b1", address = "Updated Addr", phone = "111",
            openingHours = "H", photoUrl = "url", photo = null,
            servicesCount = 5, workingHours = null
        )
        coEvery { api.updateBranch(any(), any()) } returns dto

        val result = repository.updateBranch("b1", "Updated Addr", "111", "H")

        assertTrue(result.isSuccess)
        assertEquals("Updated Addr", result.getOrNull()?.address)
    }

    @Test
    fun `updateBranchPhoto returns new photo url`() = runTest {
        val file = mockk<File> {
            every { name } returns "photo.jpg"
        }
        val response = UpdatePhotoResponseDto(success = true, photoUrl = "http://new-photo.jpg")
        coEvery { api.updateBranchPhoto(any(), any()) } returns response

        val result = repository.updateBranchPhoto("b1", file)

        assertTrue(result.isSuccess)
        assertEquals("http://new-photo.jpg", result.getOrNull())
    }

    @Test
    fun `getBranchWorkingHours returns mapped list`() = runTest {
        val whDto = io.alron.fixall.data.remote.dto.AdminWorkingHourDto(
            id = 1, dayOfWeek = 1, dayDisplay = "Mon",
            startTime = "09:00", endTime = "18:00",
            lunchStart = null, lunchEnd = null, isWorking = true
        )
        coEvery { api.getBranchWorkingHours("b1") } returns listOf(whDto)

        val result = repository.getBranchWorkingHours("b1")

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals("Mon", result.getOrNull()?.first()?.dayDisplay)
    }
}
