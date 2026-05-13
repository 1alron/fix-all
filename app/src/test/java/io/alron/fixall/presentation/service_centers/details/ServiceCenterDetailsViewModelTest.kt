package io.alron.fixall.presentation.service_centers.details

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import io.alron.fixall.domain.model.Branch
import io.alron.fixall.domain.repository.BranchesRepository
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
class ServiceCenterDetailsViewModelTest {

    private val repository: BranchesRepository = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()
    private val currentBranchFlow = MutableStateFlow<Branch?>(null)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { repository.currentBranch } returns currentBranchFlow
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(id: String? = "test_id"): ServiceCenterDetailsViewModel {
        val savedStateHandle = SavedStateHandle(if (id != null) mapOf("id" to id) else emptyMap())
        return ServiceCenterDetailsViewModel(repository, savedStateHandle)
    }

    @Test
    fun `init with id calls getBranchDetails and updates state on success`() = runTest {
        val branch = mockk<Branch> {
            every { id } returns "test_id"
        }
        coEvery { repository.getBranch("test_id") } returns Result.success(branch)

        val viewModel = createViewModel("test_id")

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(branch, state.branch)
            assertFalse(state.isLoading)
            assertNull(state.errorMessage)
        }
    }

    @Test
    fun `init with id updates state on failure`() = runTest {
        val error = "Network Error"
        coEvery { repository.getBranch("test_id") } returns Result.failure(Exception(error))

        val viewModel = createViewModel("test_id")

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(error, state.errorMessage)
            assertFalse(state.isLoading)
        }
    }

    @Test
    fun `init without id does not load branch`() = runTest {
        val viewModel = createViewModel(null)

        viewModel.state.test {
            val state = awaitItem()
            assertNull(state.branch)
            assertFalse(state.isLoading)
        }
    }

    @Test
    fun `observeCurrentBranch updates state when current branch matches id`() = runTest {
        val initialBranch = mockk<Branch> { every { id } returns "test_id" }
        val updatedBranch = mockk<Branch> { every { id } returns "test_id" }
        
        coEvery { repository.getBranch("test_id") } returns Result.success(initialBranch)
        
        val viewModel = createViewModel("test_id")
        
        viewModel.state.test {
            assertEquals(initialBranch, awaitItem().branch)
            
            currentBranchFlow.value = updatedBranch
            
            assertEquals(updatedBranch, awaitItem().branch)
        }
    }

    @Test
    fun `observeCurrentBranch does not update state when current branch id differs`() = runTest {
        val initialBranch = mockk<Branch> { every { id } returns "test_id" }
        val otherBranch = mockk<Branch> { every { id } returns "other_id" }
        
        coEvery { repository.getBranch("test_id") } returns Result.success(initialBranch)
        
        val viewModel = createViewModel("test_id")
        
        viewModel.state.test {
            assertEquals(initialBranch, awaitItem().branch)
            
            currentBranchFlow.value = otherBranch
            
            expectNoEvents()
            assertEquals(initialBranch, viewModel.state.value.branch)
        }
    }

    @Test
    fun `getBranchDetails sets loading state then success state`() = runTest {
        val branch = mockk<Branch> { every { id } returns "test_id" }
        
        coEvery { repository.getBranch("test_id") } returns Result.success(branch)
        
        val viewModel = createViewModel("test_id")
        
        viewModel.getBranchDetails("test_id")
        
        assertEquals(branch, viewModel.state.value.branch)
        assertFalse(viewModel.state.value.isLoading)
    }
}
