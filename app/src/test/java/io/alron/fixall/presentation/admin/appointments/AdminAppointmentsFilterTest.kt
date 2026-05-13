package io.alron.fixall.presentation.admin.appointments

import io.alron.fixall.domain.repository.AdminRepository
import io.alron.fixall.domain.repository.BranchesRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AdminAppointmentsFilterTest {

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
    fun `loadAppointments sends center_id filter when selected`() {
        viewModel.onCenterChange("center_123")
        viewModel.loadAppointments()

        coVerify { adminRepository.getAppointments(match { it["center_id"] == "center_123" }) }
    }

    @Test
    fun `loadAppointments sends status filter when selected`() {
        viewModel.onStatusChange("IN_PROGRESS")
        viewModel.loadAppointments()

        coVerify { adminRepository.getAppointments(match { it["status"] == "IN_PROGRESS" }) }
    }

    @Test
    fun `loadAppointments sends trimmed search query`() {
        viewModel.onSearchChange("  Roman  ")
        viewModel.loadAppointments()

        coVerify { adminRepository.getAppointments(match { it["search"] == "Roman" }) }
    }

    @Test
    fun `loadAppointments sends date filters when selected`() {
        viewModel.onDateFromChange("2024-01-01")
        viewModel.onDateToChange("2024-01-31")
        viewModel.loadAppointments()

        coVerify { 
            adminRepository.getAppointments(match { 
                it["date_from"] == "2024-01-01" && it["date_to"] == "2024-01-31" 
            }) 
        }
    }

    @Test
    fun `loadAppointments sends multiple filters together`() {
        viewModel.onCenterChange("c1")
        viewModel.onStatusChange("COMPLETED")
        viewModel.loadAppointments()

        coVerify { 
            adminRepository.getAppointments(match { 
                it["center_id"] == "c1" && it["status"] == "COMPLETED" 
            }) 
        }
    }
}
