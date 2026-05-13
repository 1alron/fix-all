package io.alron.fixall.presentation.login

import io.alron.fixall.R
import io.alron.fixall.domain.AuthManager
import io.alron.fixall.domain.model.LoginResult
import io.alron.fixall.domain.model.AuthTokens
import io.alron.fixall.presentation.util.UiText
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val authManager: AuthManager = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = LoginViewModel(authManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `updateUsername updates state correctly`() {
        viewModel.updateUsername("new_user")
        assertEquals("new_user", viewModel.uiState.value.username)
        assertNull(viewModel.uiState.value.usernameError)
    }

    @Test
    fun `updatePassword updates state correctly`() {
        viewModel.updatePassword("new_pass")
        assertEquals("new_pass", viewModel.uiState.value.password)
        assertNull(viewModel.uiState.value.passwordError)
    }

    @Test
    fun `login with empty username sets usernameError`() {
        viewModel.updateUsername("")
        viewModel.updatePassword("password")
        
        viewModel.login()
        
        val error = viewModel.uiState.value.usernameError
        assertTrue(error is UiText.StringResource)
        assertEquals(R.string.field_cant_be_blank, (error as UiText.StringResource).resId)
    }

    @Test
    fun `login with empty password sets passwordError`() {
        viewModel.updateUsername("user")
        viewModel.updatePassword("")
        
        viewModel.login()
        
        val error = viewModel.uiState.value.passwordError
        assertTrue(error is UiText.StringResource)
        assertEquals(R.string.field_cant_be_blank, (error as UiText.StringResource).resId)
    }

    @Test
    fun `login success clears networkError`() {
        viewModel.updateUsername("user")
        viewModel.updatePassword("pass")
        coEvery { authManager.login(any(), any()) } returns LoginResult.Success(AuthTokens("a", "r"))
        
        viewModel.login()
        
        assertNull(viewModel.uiState.value.networkError)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `login with invalid credentials sets networkError`() {
        viewModel.updateUsername("user")
        viewModel.updatePassword("pass")
        coEvery { authManager.login(any(), any()) } returns LoginResult.InvalidCredentials
        
        viewModel.login()
        
        val error = viewModel.uiState.value.networkError
        assertTrue(error is UiText.StringResource)
        assertEquals(R.string.invalid_credentials, (error as UiText.StringResource).resId)
    }

    @Test
    fun `login with network error sets networkError`() {
        viewModel.updateUsername("user")
        viewModel.updatePassword("pass")
        coEvery { authManager.login(any(), any()) } returns LoginResult.NetworkError
        
        viewModel.login()
        
        val error = viewModel.uiState.value.networkError
        assertTrue(error is UiText.StringResource)
        assertEquals(R.string.network_error, (error as UiText.StringResource).resId)
    }

    @Test
    fun `login with server error sets networkError`() {
        viewModel.updateUsername("user")
        viewModel.updatePassword("pass")
        coEvery { authManager.login(any(), any()) } returns LoginResult.ServerError
        
        viewModel.login()
        
        val error = viewModel.uiState.value.networkError
        assertTrue(error is UiText.StringResource)
        assertEquals(R.string.server_error, (error as UiText.StringResource).resId)
    }
}
