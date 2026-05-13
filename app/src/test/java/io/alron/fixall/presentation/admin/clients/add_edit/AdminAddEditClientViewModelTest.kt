package io.alron.fixall.presentation.admin.clients.add_edit

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
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AdminAddEditClientViewModelTest {

    private val repository: AdminRepository = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: AdminAddEditClientViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AdminAddEditClientViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onUsernameChange updates state correctly`() {
        viewModel.onUsernameChange("new_user")
        assertEquals("new_user", viewModel.state.value.username)
    }

    @Test
    fun `onFirstNameChange updates state correctly`() {
        viewModel.onFirstNameChange("Ivan")
        assertEquals("Ivan", viewModel.state.value.firstName)
    }

    @Test
    fun `onIsStaffChange updates staff status in state`() {
        viewModel.onIsStaffChange(true)
        assertTrue(viewModel.state.value.isStaff)
    }

    @Test
    fun `createClient success updates isSuccess state`() = runTest {
        coEvery { repository.createClient(any()) } returns Result.success(mockk())
        
        viewModel.onUsernameChange("user1")
        viewModel.onEmailChange("test@test.com")
        viewModel.onPasswordChange("pass123")
        viewModel.onPasswordConfirmChange("pass123")
        
        viewModel.createClient()
        
        assertTrue(viewModel.state.value.isSuccess)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `createClient failure with general error sets error message`() = runTest {
        coEvery { repository.createClient(any()) } returns Result.failure(Exception("Network Error"))
        
        viewModel.createClient()
        
        assertEquals("Network Error", viewModel.state.value.error)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `onPasswordConfirmChange updates state correctly`() {
        viewModel.onPasswordConfirmChange("password")
        assertEquals("password", viewModel.state.value.passwordConfirm)
    }
}
