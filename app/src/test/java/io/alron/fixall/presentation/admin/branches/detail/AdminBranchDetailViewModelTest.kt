package io.alron.fixall.presentation.admin.branches.detail

import androidx.lifecycle.SavedStateHandle
import io.alron.fixall.domain.model.*
import io.alron.fixall.domain.repository.AdminRepository
import io.mockk.coEvery
import io.mockk.every
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
class AdminBranchDetailViewModelTest {

    private val repository: AdminRepository = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: AdminBranchDetailViewModel
    private val branchId = "branch_123"

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        coEvery { repository.getBranchDetail(branchId) } returns Result.success(mockk(relaxed = true))
        coEvery { repository.getAppointments(any()) } returns Result.success(emptyList())
        coEvery { repository.getStatusStats(any(), any()) } returns Result.success(mockk(relaxed = true))
        coEvery { repository.getServicePopularity(any(), any()) } returns Result.success(mockk(relaxed = true))
        coEvery { repository.getBranchAttendanceStats(any(), any()) } returns Result.success(mockk(relaxed = true))

        val savedStateHandle = SavedStateHandle(mapOf("branchId" to branchId))
        viewModel = AdminBranchDetailViewModel(repository, savedStateHandle)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads branch details`() = runTest {
        val branch = mockk<AdminBranch>(relaxed = true) { every { id } returns branchId }
        coEvery { repository.getBranchDetail(branchId) } returns Result.success(branch)
        
        val savedStateHandle = SavedStateHandle(mapOf("branchId" to branchId))
        val vm = AdminBranchDetailViewModel(repository, savedStateHandle)

        assertEquals(branch, vm.state.value.branch)
    }

    @Test
    fun `fetchAppointments filters out CANCELLED status`() = runTest {
        val appointments = listOf(
            mockk<AdminAppointmentListItem>(relaxed = true) { every { status } returns "SCHEDULED" },
            mockk<AdminAppointmentListItem>(relaxed = true) { every { status } returns "IN_PROGRESS" },
            mockk<AdminAppointmentListItem>(relaxed = true) { every { status } returns "COMPLETED" },
            mockk<AdminAppointmentListItem>(relaxed = true) { every { status } returns "CANCELLED" }
        )
        coEvery { repository.getAppointments(any()) } returns Result.success(appointments)

        viewModel.loadAll()

        assertEquals(3, viewModel.state.value.appointments.size)
        assertTrue(viewModel.state.value.appointments.all { it.status != "CANCELLED" })
    }

    @Test
    fun `onDateSelected updates state and refreshes appointments`() = runTest {
        viewModel.onDateSelected(2024, 4, 20)

        assertEquals("2024-05-20", viewModel.state.value.selectedDate)
        assertEquals("20.05.2024", viewModel.state.value.selectedDateDisplay)
    }

    @Test
    fun `loadStatusStats updates period and fetches data`() = runTest {
        val stats = mockk<AdminStatusStats>(relaxed = true)
        coEvery { repository.getStatusStats("month", branchId) } returns Result.success(stats)

        viewModel.loadStatusStats("month")

        assertEquals("month", viewModel.state.value.statusPeriod)
        assertEquals(stats, viewModel.state.value.statusStats)
    }

    @Test
    fun `loadAttendanceStats updates period and fetches data`() = runTest {
        val stats = mockk<BranchAttendanceStats>(relaxed = true)
        coEvery { repository.getBranchAttendanceStats(branchId, "month") } returns Result.success(stats)

        viewModel.loadAttendanceStats("month")

        assertEquals("month", viewModel.state.value.attendancePeriod)
        assertEquals(stats, viewModel.state.value.attendanceStats)
    }

    @Test
    fun `deleteBranch calls repository and notifies success`() = runTest {
        coEvery { repository.deleteBranch(branchId) } returns Result.success(Unit)
        var called = false

        viewModel.deleteBranch { called = true }

        assertTrue(called)
    }
}
