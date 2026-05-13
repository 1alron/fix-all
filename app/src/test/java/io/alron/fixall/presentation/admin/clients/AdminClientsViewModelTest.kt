package io.alron.fixall.presentation.admin.clients

import io.alron.fixall.data.remote.dto.AdminClientListItemDto
import io.alron.fixall.domain.repository.AdminRepository
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
class AdminClientsViewModelTest {

    private val repository: AdminRepository = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: AdminClientsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { repository.getClients(any()) } returns Result.success(emptyList())
        viewModel = AdminClientsViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init calls loadClients and updates state on success`() = runTest {
        val clients = listOf(mockk<AdminClientListItemDto>(relaxed = true), mockk())
        coEvery { repository.getClients(any()) } returns Result.success(clients)

        val vm = AdminClientsViewModel(repository)

        assertEquals(clients, vm.state.value.clients)
        assertEquals(2, vm.state.value.totalCount)
        assertFalse(vm.state.value.isLoading)
    }

    @Test
    fun `onSearchChange updates state`() {
        viewModel.onSearchChange("Roman")
        assertEquals("Roman", viewModel.state.value.search)
    }

    @Test
    fun `onEmailChange updates state`() {
        viewModel.onEmailChange("test@test.com")
        assertEquals("test@test.com", viewModel.state.value.email)
    }

    @Test
    fun `onHasCarsToggle updates state`() {
        viewModel.onHasCarsToggle(true)
        assertTrue(viewModel.state.value.hasCars)
    }

    @Test
    fun `clearFilters resets filters and reloads`() = runTest {
        viewModel.onSearchChange("query")
        viewModel.onHasActiveToggle(true)

        viewModel.clearFilters()

        assertEquals("", viewModel.state.value.search)
        assertFalse(viewModel.state.value.hasActive)
        coVerify(exactly = 2) { repository.getClients(any()) }
    }

    @Test
    fun `refreshClients updates isRefreshing state`() = runTest {
        coEvery { repository.getClients(any()) } returns Result.success(emptyList())
        
        viewModel.refreshClients()
        
        assertFalse(viewModel.state.value.isRefreshing)
    }

    @Test
    fun `fetchClients updates error state on failure`() = runTest {
        val errorMsg = "Client fetch failed"
        coEvery { repository.getClients(any()) } returns Result.failure(Exception(errorMsg))
        
        viewModel.loadClients()
        
        assertEquals(errorMsg, viewModel.state.value.error)
        assertFalse(viewModel.state.value.isLoading)
    }
}
