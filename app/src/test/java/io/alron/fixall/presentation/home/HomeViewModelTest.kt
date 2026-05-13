package io.alron.fixall.presentation.home

import io.alron.fixall.domain.model.Appointment
import io.alron.fixall.domain.model.LoyaltyInfo
import io.alron.fixall.domain.model.User
import io.alron.fixall.domain.model.UserStats
import io.alron.fixall.domain.repository.AppointmentsRepository
import io.alron.fixall.domain.repository.ProfileRepository
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
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val appointmentsRepository: AppointmentsRepository = mockk()
    private val profileRepository: ProfileRepository = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()
    private val upcomingAppointmentFlow = MutableStateFlow<Appointment?>(null)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { appointmentsRepository.upcomingAppointment } returns upcomingAppointmentFlow
        coEvery { profileRepository.getMe() } returns Result.success(mockk(relaxed = true))
        coEvery { profileRepository.getLoyalty() } returns Result.success(mockk(relaxed = true))
        coEvery { profileRepository.getStats() } returns Result.success(mockk(relaxed = true))
        coEvery { appointmentsRepository.getUpcomingAppointment() } returns Result.success(null)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads initial data and updates state on success`() = runTest {
        val user = mockk<User>(relaxed = true)
        val loyalty = mockk<LoyaltyInfo>(relaxed = true)
        val stats = mockk<UserStats>(relaxed = true)

        coEvery { profileRepository.getMe() } returns Result.success(user)
        coEvery { profileRepository.getLoyalty() } returns Result.success(loyalty)
        coEvery { profileRepository.getStats() } returns Result.success(stats)

        val viewModel = HomeViewModel(appointmentsRepository, profileRepository)

        assertEquals(user, viewModel.state.value.user)
        assertEquals(loyalty, viewModel.state.value.loyaltyInfo)
        assertEquals(stats, viewModel.state.value.userStats)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `observeUpcomingAppointment updates state when repository flow emits`() = runTest {
        val viewModel = HomeViewModel(appointmentsRepository, profileRepository)
        val appointment = mockk<Appointment>(relaxed = true)

        upcomingAppointmentFlow.value = appointment

        assertEquals(appointment, viewModel.state.value.upcomingAppointment)
    }

    @Test
    fun `refresh updates state and sets isRefreshing to false`() = runTest {
        val viewModel = HomeViewModel(appointmentsRepository, profileRepository)
        
        viewModel.refresh()

        assertFalse(viewModel.state.value.isRefreshing)
    }

    @Test
    fun `refresh handles failure gracefully`() = runTest {
        val viewModel = HomeViewModel(appointmentsRepository, profileRepository)
        coEvery { profileRepository.getMe() } returns Result.failure(Exception("Error"))

        viewModel.refresh()

        assertFalse(viewModel.state.value.isRefreshing)
    }
}
