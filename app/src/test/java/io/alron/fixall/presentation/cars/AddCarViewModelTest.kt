package io.alron.fixall.presentation.cars

import android.net.Uri
import app.cash.turbine.test
import io.alron.fixall.R
import io.alron.fixall.domain.model.Car
import io.alron.fixall.domain.model.CarBrand
import io.alron.fixall.domain.model.CarModel
import io.alron.fixall.domain.repository.CarsRepository
import io.alron.fixall.presentation.util.UiText
import io.mockk.*
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
import java.util.Calendar

@OptIn(ExperimentalCoroutinesApi::class)
class AddCarViewModelTest {

    private val repository: CarsRepository = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: AddCarViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { repository.getBrands() } returns Result.success(emptyList())
        coEvery { repository.getModels(any()) } returns Result.success(emptyList())
        
        mockkStatic(Uri::class)
        val mockUri = mockk<Uri>()
        every { Uri.parse(any()) } returns mockUri
        
        viewModel = AddCarViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(Uri::class)
    }

    @Test
    fun `init loads brands correctly`() = runTest {
        val brands = listOf(CarBrand("1", "Citroen"))
        coEvery { repository.getBrands() } returns Result.success(brands)

        val vm = AddCarViewModel(repository)

        assertEquals(brands, vm.uiState.value.brands)
    }

    @Test
    fun `onBrandSelected updates state and loads models`() = runTest {
        val brand = CarBrand("1", "Citroen")
        val models = listOf(CarModel("101", "Berlingo", "1", "Citroen"))
        coEvery { repository.getModels("1") } returns Result.success(models)

        viewModel.onBrandSelected(brand)

        assertEquals(brand, viewModel.uiState.value.selectedBrand)
        assertEquals(models, viewModel.uiState.value.models)
        assertNull(viewModel.uiState.value.selectedModel)
    }

    @Test
    fun `onYearChanged updates state if numeric`() {
        viewModel.onYearChanged("2020")
        assertEquals("2020", viewModel.uiState.value.year)
    }

    @Test
    fun `onLicensePlateChanged uppercases input`() {
        viewModel.onLicensePlateChanged("a123bc")
        assertEquals("A123BC", viewModel.uiState.value.licensePlate)
    }

    @Test
    fun `saveCar validation fails if brand is missing`() = runTest {
        viewModel.onYearChanged("2020")
        viewModel.onLicensePlateChanged("А123ВЕ77")
        
        viewModel.saveCar()
        
        val error = viewModel.uiState.value.brandError
        assertTrue(error is UiText.StringResource)
        assertEquals(R.string.field_cant_be_blank, (error as UiText.StringResource).resId)
    }

    @Test
    fun `saveCar validation fails for invalid year`() = runTest {
        val brand = CarBrand("1", "Citroen")
        val model = CarModel("101", "Berlingo", "1", "Citroen")
        viewModel.onBrandSelected(brand)
        viewModel.onModelSelected(model)
        viewModel.onYearChanged("1800")
        
        viewModel.saveCar()
        
        val error = viewModel.uiState.value.yearError
        assertTrue(error is UiText.StringResource)
        assertEquals(R.string.invalid_year, (error as UiText.StringResource).resId)
    }
}
