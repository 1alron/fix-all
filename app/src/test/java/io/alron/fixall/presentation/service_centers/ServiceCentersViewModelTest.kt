package io.alron.fixall.presentation.service_centers

import io.alron.fixall.domain.model.Branch
import io.alron.fixall.domain.repository.BranchesRepository
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
class ServiceCentersViewModelTest {

    private val repository: BranchesRepository = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: ServiceCentersViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { repository.getBranches() } returns Result.success(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init calls getBranches and updates state on success`() = runTest {
        val branches = listOf(mockk<Branch>(relaxed = true))
        coEvery { repository.getBranches() } returns Result.success(branches)

        viewModel = ServiceCentersViewModel(repository)

        assertEquals(branches, viewModel.state.value.branches)
        assertFalse(viewModel.state.value.isLoading)
        assertNull(viewModel.state.value.errorMessage)
    }

    @Test
    fun `getBranches updates state with error on failure`() = runTest {
        val errorMsg = "Failed to load"
        coEvery { repository.getBranches() } returns Result.failure(Exception(errorMsg))

        viewModel = ServiceCentersViewModel(repository)

        assertEquals(errorMsg, viewModel.state.value.errorMessage)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `refresh updates isRefreshing state`() = runTest {
        viewModel = ServiceCentersViewModel(repository)
        coEvery { repository.getBranches() } returns Result.success(emptyList())

        viewModel.refresh()

        assertFalse(viewModel.state.value.isRefreshing)
        assertNull(viewModel.state.value.errorMessage)
    }
}
