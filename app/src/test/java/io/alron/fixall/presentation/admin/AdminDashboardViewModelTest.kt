package io.alron.fixall.presentation.admin

import io.alron.fixall.domain.model.AdminDashboardStats
import io.alron.fixall.domain.repository.AdminRepository
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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AdminDashboardViewModelTest {

    private val repository: AdminRepository = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: AdminDashboardViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { repository.getDashboardStats() } returns Result.success(mockk(relaxed = true))
        coEvery { repository.getStatusStats(any()) } returns Result.success(mockk(relaxed = true))
        coEvery { repository.getAttendanceStats(any()) } returns Result.success(mockk(relaxed = true))
        coEvery { repository.getServicePopularity(any()) } returns Result.success(mockk(relaxed = true))
        
        viewModel = AdminDashboardViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadDashboard updates state on success`() = runTest {
        val stats = mockk<AdminDashboardStats>(relaxed = true)
        coEvery { repository.getDashboardStats() } returns Result.success(stats)

        viewModel.loadDashboard()

        assertEquals(stats, viewModel.state.value.stats)
        assertFalse(viewModel.state.value.isLoading)
        assertNull(viewModel.state.value.error)
    }

    @Test
    fun `loadDashboard updates error state on failure`() = runTest {
        val errorMessage = "Dashboard error"
        coEvery { repository.getDashboardStats() } returns Result.failure(Exception(errorMessage))

        viewModel.loadDashboard()

        assertEquals(errorMessage, viewModel.state.value.error)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `loadStatusStats updates statusStats on success`() = runTest {
        val statusStats = mockk<io.alron.fixall.domain.model.AdminStatusStats>(relaxed = true)
        coEvery { repository.getStatusStats("month") } returns Result.success(statusStats)

        viewModel.loadStatusStats("month")

        assertEquals(statusStats, viewModel.state.value.statusStats)
        assertEquals("month", viewModel.state.value.selectedPeriod)
        assertFalse(viewModel.state.value.isLoadingStatuses)
    }

    @Test
    fun `loadStatusStats handles failure gracefully`() = runTest {
        coEvery { repository.getStatusStats(any()) } returns Result.failure(Exception("Error"))
        
        viewModel.loadStatusStats("week")
        
        assertFalse(viewModel.state.value.isLoadingStatuses)
    }

    @Test
    fun `loadAttendanceStats updates attendanceStats on success`() = runTest {
        val attendanceStats = mockk<io.alron.fixall.domain.model.AdminAttendanceStats>(relaxed = true)
        coEvery { repository.getAttendanceStats("week") } returns Result.success(attendanceStats)

        viewModel.loadAttendanceStats("week")

        assertEquals(attendanceStats, viewModel.state.value.attendanceStats)
        assertEquals("week", viewModel.state.value.selectedAttendancePeriod)
        assertFalse(viewModel.state.value.isLoadingAttendance)
    }

    @Test
    fun `loadAttendanceStats handles failure gracefully`() = runTest {
        coEvery { repository.getAttendanceStats(any()) } returns Result.failure(Exception("Error"))
        
        viewModel.loadAttendanceStats("month")
        
        assertFalse(viewModel.state.value.isLoadingAttendance)
    }

    @Test
    fun `loadServicePopularity updates servicePopularity on success`() = runTest {
        val popularity = mockk<io.alron.fixall.domain.model.AdminServicePopularity>(relaxed = true)
        coEvery { repository.getServicePopularity("year") } returns Result.success(popularity)

        viewModel.loadServicePopularity("year")

        assertEquals(popularity, viewModel.state.value.servicePopularity)
        assertEquals("year", viewModel.state.value.selectedPopularityPeriod)
        assertFalse(viewModel.state.value.isLoadingPopularity)
    }

    @Test
    fun `loadServicePopularity handles failure gracefully`() = runTest {
        coEvery { repository.getServicePopularity(any()) } returns Result.failure(Exception("Error"))
        
        viewModel.loadServicePopularity("month")
        
        assertFalse(viewModel.state.value.isLoadingPopularity)
    }

    @Test
    fun `refresh calls all loading methods`() = runTest {
        viewModel.refresh()
        
        coVerify { repository.getDashboardStats() }
        coVerify { repository.getStatusStats(any()) }
        coVerify { repository.getAttendanceStats(any()) }
        coVerify { repository.getServicePopularity(any()) }
    }
}
