package io.alron.fixall.presentation.admin.appointments

import io.alron.fixall.domain.model.AdminAppointmentListItem
import io.alron.fixall.domain.model.Branch
import io.alron.fixall.domain.repository.AdminRepository
import io.alron.fixall.domain.repository.BranchesRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AdminAppointmentsViewModelTest {

    private val adminRepository: AdminRepository = mockk()
    private val branchesRepository: BranchesRepository = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: AdminAppointmentsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { branchesRepository.getBranches() } returns Result.success(emptyList())
        coEvery { adminRepository.getAppointments(any()) } returns Result.success(emptyList())
        viewModel = AdminAppointmentsViewModel(adminRepository, branchesRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads branches and appointments`() {
        coEvery { branchesRepository.getBranches() } returns Result.success(listOf(mockk()))
        coEvery { adminRepository.getAppointments(any()) } returns Result.success(listOf(mockk()))
        
        val vm = AdminAppointmentsViewModel(adminRepository, branchesRepository)
        
        assertEquals(1, vm.state.value.branches.size)
        assertEquals(1, vm.state.value.appointments.size)
    }

    @Test
    fun `onSearchChange updates search query in state`() {
        viewModel.onSearchChange("test search")
        assertEquals("test search", viewModel.state.value.search)
    }

    @Test
    fun `onStatusChange updates status in state`() {
        viewModel.onStatusChange("COMPLETED")
        assertEquals("COMPLETED", viewModel.state.value.status)
    }

    @Test
    fun `onCenterChange updates centerId in state`() {
        viewModel.onCenterChange("center_123")
        assertEquals("center_123", viewModel.state.value.centerId)
    }

    @Test
    fun `onDateFromChange updates dateFrom in state`() {
        viewModel.onDateFromChange("2024-01-01")
        assertEquals("2024-01-01", viewModel.state.value.dateFrom)
    }

    @Test
    fun `clearFilters resets all filters in state`() {
        viewModel.onSearchChange("query")
        viewModel.onStatusChange("SCHEDULED")
        viewModel.onCenterChange("c1")
        
        viewModel.clearFilters()
        
        assertEquals("", viewModel.state.value.search)
        assertNull(viewModel.state.value.status)
        assertNull(viewModel.state.value.centerId)
        assertNull(viewModel.state.value.dateFrom)
        assertNull(viewModel.state.value.dateTo)
    }

    @Test
    fun `loadAppointments updates loading state and fetches data`() = runTest {
        val appointments = listOf(mockk<AdminAppointmentListItem>())
        coEvery { adminRepository.getAppointments(any()) } returns Result.success(appointments)
        
        viewModel.loadAppointments()
        
        assertFalse(viewModel.state.value.isLoading)
        assertEquals(appointments, viewModel.state.value.appointments)
    }

    @Test
    fun `loadAppointments sets error state on failure`() = runTest {
        coEvery { adminRepository.getAppointments(any()) } returns Result.failure(Exception("API Error"))
        
        viewModel.loadAppointments()
        
        assertFalse(viewModel.state.value.isLoading)
        assertEquals("API Error", viewModel.state.value.error)
    }

    @Test
    fun `refreshAppointments sets refreshing state`() = runTest {
        coEvery { adminRepository.getAppointments(any()) } returns Result.success(emptyList())
        
        viewModel.refreshAppointments()
        
        assertFalse(viewModel.state.value.isRefreshing)
    }
}
