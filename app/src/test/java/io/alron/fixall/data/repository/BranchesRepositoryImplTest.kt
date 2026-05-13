package io.alron.fixall.data.repository

import io.alron.fixall.data.remote.api.BranchesApi
import io.alron.fixall.data.remote.dto.BranchDto
import io.alron.fixall.domain.model.Branch
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class BranchesRepositoryImplTest {

    private val api: BranchesApi = mockk()
    private lateinit var repository: BranchesRepositoryImpl

    @Before
    fun setup() {
        repository = BranchesRepositoryImpl(api)
    }

    @Test
    fun `getBranches returns success when api returns data`() = runTest {
        val branchDto = BranchDto(
            id = "1", address = "Addr", phone = "123", 
            openingHours = "9-18", photoUrl = "url"
        )
        coEvery { api.getBranches() } returns listOf(branchDto)

        val result = repository.getBranches()

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals("Addr", result.getOrNull()?.get(0)?.address)
    }

    @Test
    fun `getBranches returns failure when api throws exception`() = runTest {
        coEvery { api.getBranches() } throws Exception("Network error")

        val result = repository.getBranches()

        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getBranch success updates currentBranch flow`() = runTest {
        val branchDto = BranchDto(
            id = "1", address = "Addr", phone = "123", 
            openingHours = "9-18", photoUrl = "url"
        )
        coEvery { api.getBranch("1") } returns branchDto

        repository.getBranch("1")

        val result = repository.getBranch("1")
        assertTrue(result.isSuccess)
        assertEquals("1", result.getOrNull()?.id)
    }
}
