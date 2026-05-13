package io.alron.fixall.presentation.admin.appointments.details

import androidx.lifecycle.SavedStateHandle
import io.alron.fixall.domain.model.AddNoteResponse
import io.alron.fixall.domain.model.AdminAppointmentDetail
import io.alron.fixall.domain.repository.AdminRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.coVerify
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
class AdminAppointmentDetailViewModelTest {

    private val repository: AdminRepository = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()
    private val appointmentId = "test_appointment_123"
    private lateinit var viewModel: AdminAppointmentDetailViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { repository.getAppointmentDetail(appointmentId) } returns Result.success(mockk(relaxed = true))
        
        val savedStateHandle = SavedStateHandle(mapOf("id" to appointmentId))
        viewModel = AdminAppointmentDetailViewModel(repository, savedStateHandle)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init calls loadAppointment and updates state on success`() = runTest {
        val appointment = mockk<AdminAppointmentDetail>(relaxed = true) {
            every { id } returns appointmentId
        }
        coEvery { repository.getAppointmentDetail(appointmentId) } returns Result.success(appointment)

        val savedStateHandle = SavedStateHandle(mapOf("id" to appointmentId))
        val vm = AdminAppointmentDetailViewModel(repository, savedStateHandle)

        assertEquals(appointment, vm.state.value.appointment)
        assertFalse(vm.state.value.isLoading)
        assertNull(vm.state.value.error)
    }

    @Test
    fun `loadAppointment updates state with error on failure`() = runTest {
        val errorMsg = "Appointment not found"
        coEvery { repository.getAppointmentDetail(appointmentId) } returns Result.failure(Exception(errorMsg))

        viewModel.loadAppointment()

        assertEquals(errorMsg, viewModel.state.value.error)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `changeStatus failure updates error state`() = runTest {
        val errorMsg = "Failed to change status"
        coEvery { repository.changeStatus(any(), any()) } returns Result.failure(Exception(errorMsg))

        viewModel.changeStatus("COMPLETED")

        assertEquals(errorMsg, viewModel.state.value.error)
        assertFalse(viewModel.state.value.isUpdatingStatus)
    }

    @Test
    fun `addNote success updates appointment notes`() = runTest {
        val note = "Customer will be late"
        val response = AddNoteResponse(success = true, notes = note)
        coEvery { repository.addNote(appointmentId, note) } returns Result.success(response)
        
        val initialAppointment = AdminAppointmentDetail(
            id = appointmentId,
            scheduledDate = "", scheduledTime = "", endTime = "", carInfo = "", clientName = "", clientEmail = "", clientPhone = "", serviceName = "", serviceDuration = 0, centerAddress = "", status = "", statusDisplay = "", notes = null, vin = "", totalPrice = 0.0, isPaid = false, paymentStatus = null, paymentInfo = null, createdAt = "", updatedAt = ""
        )
        coEvery { repository.getAppointmentDetail(appointmentId) } returns Result.success(initialAppointment)
        viewModel.loadAppointment()

        viewModel.addNote(note)

        assertEquals(note, viewModel.state.value.appointment?.notes)
        assertFalse(viewModel.state.value.isAddingNote)
    }

    @Test
    fun `addNote failure updates error state`() = runTest {
        val errorMsg = "Failed to add note"
        coEvery { repository.addNote(any(), any()) } returns Result.failure(Exception(errorMsg))

        viewModel.addNote("test note")

        assertEquals(errorMsg, viewModel.state.value.error)
        assertFalse(viewModel.state.value.isAddingNote)
    }

    @Test
    fun `refreshAppointment sets isRefreshing to false eventually`() = runTest {
        coEvery { repository.getAppointmentDetail(appointmentId) } returns Result.success(mockk(relaxed = true))
        
        viewModel.refreshAppointment()
        
        assertFalse(viewModel.state.value.isRefreshing)
    }
}
