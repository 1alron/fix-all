package io.alron.fixall.presentation.admin.branches.add_edit

import androidx.lifecycle.SavedStateHandle
import io.alron.fixall.domain.model.AdminWorkingHour
import io.alron.fixall.domain.repository.AdminRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AdminAddEditBranchTimeTest {

    private val repository: AdminRepository = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: AdminAddEditBranchViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { repository.getUniqueServiceNames() } returns Result.success(listOf("Service 1"))
        viewModel = AdminAddEditBranchViewModel(repository, SavedStateHandle())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onWorkingHourChange correctly updates specific day in list`() {
        val updatedHour = AdminWorkingHour(
            id = null, dayOfWeek = 1, dayDisplay = "Mon",
            startTime = "10:00", endTime = "20:00",
            lunchStart = null, lunchEnd = null, isWorking = true
        )

        viewModel.onWorkingHourChange(0, updatedHour)

        assertEquals("10:00", viewModel.state.value.workingHours[0].startTime)
        assertEquals("20:00", viewModel.state.value.workingHours[0].endTime)
    }

    @Test
    fun `onServicePriceChange updates price for specific service`() {
        viewModel.onServicePriceChange(0, "1500")
        assertEquals("1500", viewModel.state.value.selectableServices[0].price)
    }

    @Test
    fun `onServiceDurationChange updates duration for specific service`() {
        viewModel.onServiceDurationChange(0, 90)
        assertEquals(90, viewModel.state.value.selectableServices[0].duration)
    }
}
