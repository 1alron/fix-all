package io.alron.fixall.presentation.profile

import app.cash.turbine.test
import io.alron.fixall.domain.AuthManager
import io.alron.fixall.domain.model.User
import io.alron.fixall.domain.model.LoyaltyInfo
import io.alron.fixall.domain.repository.ProfileRepository
import io.alron.fixall.presentation.util.EmailValidator
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

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    private val repository: ProfileRepository = mockk()
    private val authManager: AuthManager = mockk(relaxed = true)
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: ProfileViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        mockkObject(EmailValidator)
        every { EmailValidator.isValid(any()) } returns true

        coEvery { repository.getMe() } returns Result.success(mockk(relaxed = true))
        coEvery { repository.getLoyalty() } returns Result.success(mockk(relaxed = true))
        
        viewModel = ProfileViewModel(repository, authManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkObject(EmailValidator)
    }

    @Test
    fun `init loads profile and loyalty data success`() = runTest {
        val user = mockk<User>(relaxed = true)
        val loyalty = mockk<LoyaltyInfo>(relaxed = true)
        coEvery { repository.getMe() } returns Result.success(user)
        coEvery { repository.getLoyalty() } returns Result.success(loyalty)

        val vm = ProfileViewModel(repository, authManager)

        assertEquals(user, vm.state.value.user)
        assertEquals(loyalty, vm.state.value.loyaltyInfo)
        assertFalse(vm.state.value.isLoading)
    }

    @Test
    fun `loadProfileData failure updates state with error`() = runTest {
        coEvery { repository.getMe() } returns Result.failure(Exception("Network error"))

        viewModel.refresh()

        assertEquals("Network error", viewModel.state.value.errorMessage)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `logout calls authManager logout`() = runTest {
        viewModel.logout()
        coVerify { authManager.logout() }
    }

    @Test
    fun `deleteAvatar success reloads profile data`() = runTest {
        coEvery { repository.deleteAvatar() } returns Result.success(Unit)
        
        viewModel.deleteAvatar()

        coVerify { repository.getMe() }
        coVerify { repository.deleteAvatar() }
    }
}
