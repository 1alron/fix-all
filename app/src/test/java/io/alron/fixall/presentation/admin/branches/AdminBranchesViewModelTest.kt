package io.alron.fixall.presentation.admin.branches

import io.alron.fixall.domain.model.AdminBranch
import io.alron.fixall.domain.repository.AdminRepository
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
class AdminBranchesViewModelTest {

    private val repository: AdminRepository = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: AdminBranchesViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { repository.getBranches() } returns Result.success(emptyList())
        viewModel = AdminBranchesViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init calls getBranches and updates state on success`() = runTest {
        val branches = listOf(mockk<AdminBranch>(relaxed = true))
        coEvery { repository.getBranches() } returns Result.success(branches)

        val vm = AdminBranchesViewModel(repository)

        assertEquals(branches, vm.state.value.branches)
        assertFalse(vm.state.value.isLoading)
        assertNull(vm.state.value.error)
    }

    @Test
    fun `getBranches with isRefresh true updates isRefreshing state`() = runTest {
        coEvery { repository.getBranches() } returns Result.success(emptyList())
        
        viewModel.getBranches(isRefresh = true)
        
        assertFalse(viewModel.state.value.isRefreshing)
    }

    @Test
    fun `getBranches updates error state on failure`() = runTest {
        val errorMsg = "API Error"
        coEvery { repository.getBranches() } returns Result.failure(Exception(errorMsg))
        
        viewModel.getBranches()
        
        assertEquals(errorMsg, viewModel.state.value.error)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `refreshSilently updates state without showing loading`() = runTest {
        val branches = listOf(mockk<AdminBranch>(relaxed = true))
        coEvery { repository.getBranches() } returns Result.success(branches)
        
        viewModel.refreshSilently()
        
        assertEquals(branches, viewModel.state.value.branches)
        assertFalse(viewModel.state.value.isLoading)
        assertFalse(viewModel.state.value.isRefreshing)
    }
}
