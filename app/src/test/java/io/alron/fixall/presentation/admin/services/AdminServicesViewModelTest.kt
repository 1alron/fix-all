package io.alron.fixall.presentation.admin.services

import io.alron.fixall.domain.model.AdminService
import io.alron.fixall.domain.model.Branch
import io.alron.fixall.domain.repository.AdminRepository
import io.alron.fixall.domain.repository.BranchesRepository
import io.alron.fixall.data.remote.dto.ToggleActiveResponseDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AdminServicesViewModelTest {

    private val adminRepository: AdminRepository = mockk()
    private val branchesRepository: BranchesRepository = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: AdminServicesViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { branchesRepository.getBranches() } returns Result.success(emptyList())
        coEvery { adminRepository.getServices(any()) } returns Result.success(emptyList())
        viewModel = AdminServicesViewModel(adminRepository, branchesRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads branches and services`() {
        val branches = listOf(mockk<Branch>(relaxed = true))
        val services = listOf(mockk<AdminService>(relaxed = true))
        coEvery { branchesRepository.getBranches() } returns Result.success(branches)
        coEvery { adminRepository.getServices(any()) } returns Result.success(services)

        val vm = AdminServicesViewModel(adminRepository, branchesRepository)

        assertEquals(branches, vm.state.value.branches)
        assertEquals(services, vm.state.value.services)
    }

    @Test
    fun `onSearchChange updates state`() {
        viewModel.onSearchChange("wash")
        assertEquals("wash", viewModel.state.value.search)
    }

    @Test
    fun `onActiveOnlyChange updates state`() {
        viewModel.onActiveOnlyChange(true)
        assertTrue(viewModel.state.value.isActiveOnly)
    }

    @Test
    fun `onCenterChange updates state`() {
        viewModel.onCenterChange("c1")
        assertEquals("c1", viewModel.state.value.centerId)
    }

    @Test
    fun `clearFilters resets state and reloads services`() = runTest {
        viewModel.onSearchChange("query")
        viewModel.onActiveOnlyChange(true)
        
        viewModel.clearFilters()
        
        assertEquals("", viewModel.state.value.search)
        assertFalse(viewModel.state.value.isActiveOnly)
        coVerify { adminRepository.getServices(any()) }
    }

    @Test
    fun `createService success reloads list`() = runTest {
        coEvery { adminRepository.createService(any()) } returns Result.success(mockk(relaxed = true))
        
        viewModel.createService("Name", "Desc", 60, "100", "c1", true)
        
        coVerify { adminRepository.getServices(any()) }
    }

    @Test
    fun `updateService success updates local list`() = runTest {
        val initialService = AdminService("1", "Old", "", 60, "100", "c1", "Addr", true)
        val updatedService = initialService.copy(name = "New")
        coEvery { adminRepository.getServices(any()) } returns Result.success(listOf(initialService))
        viewModel.loadServices()

        coEvery { adminRepository.updateService("1", any()) } returns Result.success(updatedService)

        viewModel.updateService("1", "New", "", 60, "100", "c1", true)

        assertEquals("New", viewModel.state.value.services[0].name)
    }

    @Test
    fun `deleteService success removes from local list`() = runTest {
        val service = AdminService("1", "Name", "", 60, "100", "c1", "Addr", true)
        coEvery { adminRepository.getServices(any()) } returns Result.success(listOf(service))
        viewModel.loadServices()

        coEvery { adminRepository.deleteService("1") } returns Result.success(Unit)

        viewModel.deleteService("1")

        assertTrue(viewModel.state.value.services.isEmpty())
    }

    @Test
    fun `toggleActive updates local service state`() = runTest {
        val service = AdminService("1", "Name", "", 60, "100", "c1", "Addr", true)
        coEvery { adminRepository.getServices(any()) } returns Result.success(listOf(service))
        viewModel.loadServices()

        coEvery { adminRepository.toggleServiceActive("1") } returns Result.success(
            ToggleActiveResponseDto(success = true, isActive = false, message = "Updated")
        )

        viewModel.toggleActive("1")

        assertFalse(viewModel.state.value.services[0].isActive)
    }
}
