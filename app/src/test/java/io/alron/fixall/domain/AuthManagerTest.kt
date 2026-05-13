package io.alron.fixall.domain

import io.alron.fixall.domain.model.AuthTokens
import io.alron.fixall.domain.model.LoginResult
import io.alron.fixall.domain.model.RegisterResult
import io.alron.fixall.domain.repository.AuthRepository
import io.alron.fixall.domain.repository.TokenStorageRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthManagerTest {

    private val authRepository: AuthRepository = mockk()
    private val tokenStorageRepository: TokenStorageRepository = mockk(relaxed = true)

    @Test
    fun `init sets isAuthorized to true when token exists`() {
        every { tokenStorageRepository.getAccessToken() } returns "valid_token"
        
        val authManager = AuthManager(authRepository, tokenStorageRepository)
        
        assertTrue(authManager.isAuthorized.value)
    }

    @Test
    fun `init sets isAuthorized to false when token is null`() {
        every { tokenStorageRepository.getAccessToken() } returns null
        
        val authManager = AuthManager(authRepository, tokenStorageRepository)
        
        assertFalse(authManager.isAuthorized.value)
    }

    @Test
    fun `init sets isAuthorized to false when token is empty`() {
        every { tokenStorageRepository.getAccessToken() } returns ""
        
        val authManager = AuthManager(authRepository, tokenStorageRepository)
        
        assertFalse(authManager.isAuthorized.value)
    }

    @Test
    fun `login calls repository login with correct params`() = runTest {
        every { tokenStorageRepository.getAccessToken() } returns null
        val authManager = AuthManager(authRepository, tokenStorageRepository)
        coEvery { authRepository.login("user", "pass") } returns LoginResult.InvalidCredentials

        authManager.login("user", "pass")

        coVerify { authRepository.login("user", "pass") }
    }

    @Test
    fun `login updates isAuthorized to true on success`() = runTest {
        every { tokenStorageRepository.getAccessToken() } returns null
        val authManager = AuthManager(authRepository, tokenStorageRepository)
        coEvery { authRepository.login("user", "pass") } returns LoginResult.Success(AuthTokens("a", "r"))

        authManager.login("user", "pass")

        assertTrue(authManager.isAuthorized.value)
    }

    @Test
    fun `login does not update isAuthorized on failure`() = runTest {
        every { tokenStorageRepository.getAccessToken() } returns null
        val authManager = AuthManager(authRepository, tokenStorageRepository)
        coEvery { authRepository.login("user", "pass") } returns LoginResult.ServerError

        authManager.login("user", "pass")

        assertFalse(authManager.isAuthorized.value)
    }

    @Test
    fun `logout clears token storage`() {
        every { tokenStorageRepository.getAccessToken() } returns "token"
        val authManager = AuthManager(authRepository, tokenStorageRepository)

        authManager.logout()

        verify { tokenStorageRepository.clear() }
    }

    @Test
    fun `logout sets isAuthorized to false`() {
        every { tokenStorageRepository.getAccessToken() } returns "token"
        val authManager = AuthManager(authRepository, tokenStorageRepository)

        authManager.logout()

        assertFalse(authManager.isAuthorized.value)
    }

    @Test
    fun `register calls repository register`() = runTest {
        every { tokenStorageRepository.getAccessToken() } returns null
        val authManager = AuthManager(authRepository, tokenStorageRepository)
        coEvery { 
            authRepository.register(any(), any(), any(), any(), any(), any()) 
        } returns RegisterResult.ServerError

        authManager.register("u", "f", "l", "e", "p1", "p2")

        coVerify { 
            authRepository.register("u", "f", "l", "e", "p1", "p2") 
        }
    }

    @Test
    fun `register success triggers login`() = runTest {
        every { tokenStorageRepository.getAccessToken() } returns null
        val authManager = AuthManager(authRepository, tokenStorageRepository)
        coEvery { 
            authRepository.register(any(), any(), any(), any(), any(), any()) 
        } returns RegisterResult.Success
        coEvery { authRepository.login(any(), any()) } returns LoginResult.Success(AuthTokens("a", "r"))

        authManager.register("u", "f", "l", "e", "p1", "p2")

        coVerify { authRepository.login("u", "p1") }
        assertTrue(authManager.isAuthorized.value)
    }

    @Test
    fun `getAccessToken delegates to storage`() {
        every { tokenStorageRepository.getAccessToken() } returns "secret"
        val authManager = AuthManager(authRepository, tokenStorageRepository)

        val result = authManager.getAccessToken()

        assertEquals("secret", result)
        verify { tokenStorageRepository.getAccessToken() }
    }
}
