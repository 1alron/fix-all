package io.alron.fixall.presentation.appointments.create

import app.cash.turbine.test
import com.google.gson.Gson
import io.alron.fixall.domain.model.Branch
import io.alron.fixall.domain.model.Car
import io.alron.fixall.domain.model.Service
import io.alron.fixall.domain.model.TimeSlots
import io.alron.fixall.domain.model.WorkingHoursRange
import io.alron.fixall.domain.repository.AppointmentsRepository
import io.alron.fixall.domain.repository.BranchesRepository
import io.alron.fixall.domain.repository.CarsRepository
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
class CreateAppointmentViewModelTest {

    private val appointmentsRepository: AppointmentsRepository = mockk()
    private val branchesRepository: BranchesRepository = mockk()
    private val carsRepository: CarsRepository = mockk()
    private val gson: Gson = Gson()
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: CreateAppointmentViewModel
    
    private val defaultWorkingHours = WorkingHoursRange("09:00", "18:00", null, null)
    private fun emptyTimeSlots(date: String = "2024-05-20") = TimeSlots(date, emptyList(), defaultWorkingHours)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { branchesRepository.getBranches() } returns Result.success(emptyList())
        coEvery { carsRepository.getCars() } returns Result.success(emptyList())
        coEvery { appointmentsRepository.getAvailableServices(any()) } returns Result.success(emptyList())
        coEvery { appointmentsRepository.getAvailableTimeSlots(any(), any(), any()) } returns Result.success(emptyTimeSlots())
        
        viewModel = CreateAppointmentViewModel(appointmentsRepository, branchesRepository, carsRepository, gson)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads branches and cars`() = runTest {
        val branches = listOf(mockk<Branch>(relaxed = true))
        val cars = listOf(mockk<Car>(relaxed = true))
        coEvery { branchesRepository.getBranches() } returns Result.success(branches)
        coEvery { carsRepository.getCars() } returns Result.success(cars)

        val vm = CreateAppointmentViewModel(appointmentsRepository, branchesRepository, carsRepository, gson)

        assertEquals(branches, vm.state.value.branches)
        assertEquals(cars, vm.state.value.userCars)
        assertFalse(vm.state.value.isLoading)
    }

    @Test
    fun `onBranchSelected updates state and loads services`() = runTest {
        val branch = mockk<Branch>(relaxed = true) { coEvery { id } returns "b1" }
        val services = listOf(mockk<Service>(relaxed = true))
        coEvery { appointmentsRepository.getAvailableServices("b1") } returns Result.success(services)

        viewModel.onBranchSelected(branch)

        assertEquals(branch, viewModel.state.value.selectedBranch)
        assertEquals(services, viewModel.state.value.availableServices)
        assertNull(viewModel.state.value.selectedService)
    }

    @Test
    fun `onServiceSelected updates state and clears slots`() = runTest {
        val service = mockk<Service>(relaxed = true) { coEvery { id } returns "s1" }
        val branch = mockk<Branch>(relaxed = true) { coEvery { id } returns "b1" }
        
        viewModel.onBranchSelected(branch)
        viewModel.onDateStringSelected("2024-05-20")
        viewModel.onServiceSelected(service)

        assertEquals(service, viewModel.state.value.selectedService)
        assertEquals("", viewModel.state.value.selectedTime)
    }

    @Test
    fun `onCarSelected updates selectedCar in state`() {
        val car = mockk<Car>(relaxed = true)
        viewModel.onCarSelected(car)
        assertEquals(car, viewModel.state.value.selectedCar)
    }

    @Test
    fun `onDateStringSelected updates date and clears slots`() = runTest {
        viewModel.onDateStringSelected("2024-05-20")
        assertEquals("2024-05-20", viewModel.state.value.selectedDate)
        assertEquals("", viewModel.state.value.selectedTime)
    }

    @Test
    fun `onTimeSelected updates selectedTime in state`() {
        viewModel.onTimeSelected("10:00")
        assertEquals("10:00", viewModel.state.value.selectedTime)
    }

    @Test
    fun `onNotesChanged updates notes in state`() {
        viewModel.onNotesChanged("Please check brakes")
        assertEquals("Please check brakes", viewModel.state.value.notes)
    }

    @Test
    fun `createAppointment does nothing if fields are missing`() = runTest {
        viewModel.createAppointment()
        coVerify(exactly = 0) { appointmentsRepository.createAppointment(any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `createAppointment calls repository on success and emits event`() = runTest {
        val car = mockk<Car>(relaxed = true) { coEvery { id } returns "c1" }
        val service = mockk<Service>(relaxed = true) { coEvery { id } returns "s1" }
        val branch = mockk<Branch>(relaxed = true) { coEvery { id } returns "b1" }
        
        coEvery { appointmentsRepository.getAvailableServices("b1") } returns Result.success(emptyList())
        coEvery { appointmentsRepository.getAvailableTimeSlots("b1", "s1", "2024-05-20") } returns Result.success(emptyTimeSlots("2024-05-20"))

        viewModel.onBranchSelected(branch)
        viewModel.onCarSelected(car)
        viewModel.onServiceSelected(service)
        viewModel.onDateStringSelected("2024-05-20")
        viewModel.onTimeSelected("10:00")

        coEvery { 
            appointmentsRepository.createAppointment("c1", "s1", "b1", "2024-05-20", "10:00", any()) 
        } returns Result.success(mockk(relaxed = true))

        viewModel.events.test {
            viewModel.createAppointment()
            
            val item1 = awaitItem()
            assertTrue(item1 is CreateAppointmentEvent.ShowToast)
            val item2 = awaitItem()
            assertTrue(item2 is CreateAppointmentEvent.AppointmentCreated)
        }
    }

    @Test
    fun `refreshCars updates userCars in state`() = runTest {
        val cars = listOf(mockk<Car>(relaxed = true))
        coEvery { carsRepository.getCars() } returns Result.success(cars)

        viewModel.refreshCars()

        assertEquals(cars, viewModel.state.value.userCars)
    }
}
