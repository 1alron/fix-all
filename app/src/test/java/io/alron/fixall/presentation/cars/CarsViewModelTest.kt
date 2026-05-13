package io.alron.fixall.presentation.cars

import app.cash.turbine.test
import io.alron.fixall.domain.model.Car
import io.alron.fixall.domain.repository.CarsRepository
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
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CarsViewModelTest {

    private val carsRepository: CarsRepository = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: CarsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { carsRepository.getCars() } returns Result.success(emptyList())
        viewModel = CarsViewModel(carsRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init calls getCars and updates state on success`() = runTest {
        val cars = listOf(mockk<Car>(), mockk<Car>())
        coEvery { carsRepository.getCars() } returns Result.success(cars)

        val vm = CarsViewModel(carsRepository)

        assertEquals(cars, vm.state.value.cars)
        assertFalse(vm.state.value.isLoading)
        assertNull(vm.state.value.errorMessage)
    }

    @Test
    fun `getCars updates error message on failure`() = runTest {
        val error = "Failed to load cars"
        coEvery { carsRepository.getCars() } returns Result.failure(Exception(error))

        viewModel.getCars()

        assertEquals(error, viewModel.state.value.errorMessage)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `deleteCar success triggers getCars and shows snackbar`() = runTest {
        coEvery { carsRepository.deleteCar("123") } returns Result.success(Unit)
        coEvery { carsRepository.getCars() } returns Result.success(emptyList())

        viewModel.events.test {
            viewModel.deleteCar("123")
            val event = awaitItem()
            assert(event is CarsEvent.ShowSnackbar)
            assertEquals("Автомобиль успешно удален", (event as CarsEvent.ShowSnackbar).message)
        }
    }

    @Test
    fun `deleteCar failure updates error message`() = runTest {
        val error = "Delete failed"
        coEvery { carsRepository.deleteCar("123") } returns Result.failure(Exception(error))

        viewModel.deleteCar("123")

        assertEquals(error, viewModel.state.value.errorMessage)
    }

    @Test
    fun `refresh updates isRefreshing state`() = runTest {
        coEvery { carsRepository.getCars() } returns Result.success(emptyList())

        viewModel.refresh()

        assertFalse(viewModel.state.value.isRefreshing)
    }
}
