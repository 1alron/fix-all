package io.alron.fixall.presentation.appointments

import io.alron.fixall.domain.model.Appointment
import io.alron.fixall.domain.repository.AppointmentsRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
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
class AppointmentsViewModelTest {

    private val repository: AppointmentsRepository = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()
    private val appointmentsFlow = MutableStateFlow<List<Appointment>>(emptyList())

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { repository.appointments } returns appointmentsFlow
        coEvery { repository.getAppointments() } returns Result.success(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init calls getAppointments and observes repository flow`() = runTest {
        val appointments = listOf(mockk<Appointment>(relaxed = true))
        coEvery { repository.getAppointments() } returns Result.success(appointments)
        
        val viewModel = AppointmentsViewModel(repository)
        
        appointmentsFlow.value = appointments
        
        assertEquals(appointments, viewModel.state.value.appointments)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `getAppointments handles failure and sets errorMessage`() = runTest {
        val errorMsg = "Network Error"
        coEvery { repository.getAppointments() } returns Result.failure(Exception(errorMsg))
        
        val viewModel = AppointmentsViewModel(repository)
        
        assertEquals(errorMsg, viewModel.state.value.errorMessage)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `refresh updates isRefreshing state`() = runTest {
        val viewModel = AppointmentsViewModel(repository)
        coEvery { repository.getAppointments() } returns Result.success(emptyList())
        
        viewModel.refresh()
        
        assertFalse(viewModel.state.value.isRefreshing)
        assertNull(viewModel.state.value.errorMessage)
    }

    @Test
    fun `refresh sets error message on failure`() = runTest {
        val viewModel = AppointmentsViewModel(repository)
        coEvery { repository.getAppointments() } returns Result.failure(Exception("Refresh failed"))
        
        viewModel.refresh()
        
        assertEquals("Refresh failed", viewModel.state.value.errorMessage)
        assertFalse(viewModel.state.value.isRefreshing)
    }

    @Test
    fun `observeAppointments updates state when flow emits new values`() = runTest {
        val viewModel = AppointmentsViewModel(repository)
        val appointments = listOf(mockk<Appointment>(relaxed = true))
        
        appointmentsFlow.value = appointments
        
        assertEquals(appointments, viewModel.state.value.appointments)
    }
}
