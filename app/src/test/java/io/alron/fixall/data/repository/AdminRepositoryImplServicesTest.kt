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

class AdminRepositoryImplServicesTest {

    private val api: AdminApi = mockk()
    private lateinit var repository: AdminRepositoryImpl

    @Before
    fun setup() {
        repository = AdminRepositoryImpl(api)
    }

    @Test
    fun `getServices returns success and maps domain models`() = runTest {
        val dto = AdminServiceItemDto(
            id = "s1", name = "Test Service", description = "Desc", 
            duration = 30, price = "100.0", serviceCenterId = "c1", 
            centerAddress = "Addr", isActive = true
        )
        coEvery { api.getServices(any()) } returns listOf(dto)

        val result = repository.getServices(emptyMap())

        assertTrue(result.isSuccess)
        assertEquals("Test Service", result.getOrNull()?.first()?.name)
    }

    @Test
    fun `getServiceDetail returns mapped domain model`() = runTest {
        val dto = AdminServiceItemDto(
            id = "s1", name = "Test", description = null, 
            duration = 30, price = "100", serviceCenterId = "c1", 
            centerAddress = null, isActive = true
        )
        coEvery { api.getServiceDetail("s1") } returns dto

        val result = repository.getServiceDetail("s1")

        assertTrue(result.isSuccess)
        assertEquals("s1", result.getOrNull()?.id)
    }

    @Test
    fun `createService success returns created domain model`() = runTest {
        val request = CreateUpdateServiceRequestDto("New", "Desc", 60, "500", "c1", true)
        val responseDto = AdminServiceItemDto("s2", "New", "Desc", 60, "500", "c1", "Addr", true)
        coEvery { api.createService(any()) } returns responseDto

        val result = repository.createService(request)

        assertTrue(result.isSuccess)
        assertEquals("s2", result.getOrNull()?.id)
    }

    @Test
    fun `getBranches returns mapped admin branches`() = runTest {
        val dto = AdminBranchListItemDto(
            id = "b1", address = "Address", phone = "555", 
            openingHours = "24/7", photoUrl = "url", photo = null, 
            servicesCount = 10, workingHours = emptyList()
        )
        coEvery { api.getBranches() } returns listOf(dto)

        val result = repository.getBranches()

        assertTrue(result.isSuccess)
        assertEquals("Address", result.getOrNull()?.first()?.address)
    }

    @Test
    fun `getBranchDetail returns mapped admin branch`() = runTest {
        val dto = AdminBranchListItemDto(
            id = "b1", address = "Addr", phone = "111", 
            openingHours = "H", photoUrl = "url", photo = null, 
            servicesCount = 5, workingHours = null
        )
        coEvery { api.getBranchDetail("b1") } returns dto

        val result = repository.getBranchDetail("b1")

        assertTrue(result.isSuccess)
        assertEquals("b1", result.getOrNull()?.id)
        assertTrue(result.getOrNull()?.workingHours?.isEmpty() == true)
    }
}
