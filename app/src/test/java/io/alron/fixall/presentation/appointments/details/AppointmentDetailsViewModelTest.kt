package io.alron.fixall.presentation.appointments.details

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import io.alron.fixall.data.remote.dto.PaymentStatusDto
import io.alron.fixall.data.remote.dto.SyncPaymentStatusResponseDto
import io.alron.fixall.domain.model.Appointment
import io.alron.fixall.domain.model.LoyaltyInfo
import io.alron.fixall.domain.repository.AppointmentsRepository
import io.alron.fixall.domain.repository.ProfileRepository
import io.mockk.coEvery
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
class AppointmentDetailsViewModelTest {

    private val appointmentsRepository: AppointmentsRepository = mockk()
    private val profileRepository: ProfileRepository = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()
    private val appointmentId = "a123"

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        coEvery { appointmentsRepository.getAppointmentDetails(appointmentId) } returns Result.success(mockk(relaxed = true))
        coEvery { profileRepository.getLoyalty() } returns Result.success(mockk(relaxed = true))
        coEvery { appointmentsRepository.syncPaymentStatus(appointmentId) } returns Result.success(SyncPaymentStatusResponseDto(isPaid = false, paymentStatus = null))
        coEvery { appointmentsRepository.getPaymentStatus(appointmentId) } returns Result.success(mockk(relaxed = true))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = AppointmentDetailsViewModel(
        appointmentsRepository, 
        profileRepository, 
        SavedStateHandle(mapOf("id" to appointmentId))
    )

    @Test
    fun `init loads details and loyalty info`() = runTest {
        val appointment = mockk<Appointment>(relaxed = true)
        val loyalty = mockk<LoyaltyInfo>(relaxed = true)
        coEvery { appointmentsRepository.getAppointmentDetails(appointmentId) } returns Result.success(appointment)
        coEvery { profileRepository.getLoyalty() } returns Result.success(loyalty)

        val viewModel = createViewModel()

        assertEquals(appointment, viewModel.state.value.appointment)
        assertEquals(loyalty, viewModel.state.value.loyaltyInfo)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `getDetails failure updates error state`() = runTest {
        coEvery { appointmentsRepository.getAppointmentDetails(appointmentId) } returns Result.failure(Exception("Not found"))
        
        val viewModel = createViewModel()
        viewModel.getDetails(appointmentId)

        assertEquals("Not found", viewModel.state.value.errorMessage)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `initiatePayment success emits OpenPaymentUrl event`() = runTest {
        val url = "https://payment.url"
        coEvery { appointmentsRepository.initiatePayment(appointmentId, 10.0) } returns Result.success(url)
        
        val viewModel = createViewModel()
        
        viewModel.events.test {
            viewModel.initiatePayment(10.0)
            val event = awaitItem()
            assertTrue(event is AppointmentDetailsEvent.OpenPaymentUrl)
            assertEquals(url, (event as AppointmentDetailsEvent.OpenPaymentUrl).url)
        }
    }

    @Test
    fun `cancelAppointment success shows toast and emits Cancelled event`() = runTest {
        coEvery { appointmentsRepository.cancelAppointment(appointmentId) } returns Result.success("Cancelled OK")
        
        val viewModel = createViewModel()
        
        viewModel.events.test {
            viewModel.cancelAppointment()
            
            val toastEvent = awaitItem()
            assertTrue(toastEvent is AppointmentDetailsEvent.ShowToast)
            assertEquals("Cancelled OK", (toastEvent as AppointmentDetailsEvent.ShowToast).message)
            
            assertTrue(awaitItem() is AppointmentDetailsEvent.AppointmentCancelled)
        }
        assertFalse(viewModel.state.value.isCancelling)
    }

    @Test
    fun `syncPaymentStatus if paid reloads details`() = runTest {
        coEvery { appointmentsRepository.syncPaymentStatus(appointmentId) } returns Result.success(SyncPaymentStatusResponseDto(isPaid = true, paymentStatus = "succeeded"))
        val updatedAppointment = mockk<Appointment>(relaxed = true)
        coEvery { appointmentsRepository.getAppointmentDetails(appointmentId) } returns Result.success(updatedAppointment)

        val viewModel = createViewModel()
        viewModel.syncPaymentStatus()

        assertEquals(updatedAppointment, viewModel.state.value.appointment)
        assertFalse(viewModel.state.value.isCheckingPayment)
    }
}
