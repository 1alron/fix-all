package io.alron.fixall.data.repository

import com.google.gson.Gson
import io.alron.fixall.data.remote.api.AuthApi
import io.alron.fixall.data.remote.dto.RegisterResponseDto
import io.alron.fixall.domain.model.RegisterResult
import io.alron.fixall.domain.repository.TokenStorageRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AuthRepositoryImplRegisterTest {

    private val api: AuthApi = mockk()
    private val tokenStorage: TokenStorageRepository = mockk(relaxed = true)
    private val gson = Gson()
    private lateinit var repository: AuthRepositoryImpl

    @Before
    fun setup() {
        repository = AuthRepositoryImpl(api, tokenStorage, gson)
    }

    @Test
    fun `register success returns Success result`() = runTest {
        val response = RegisterResponseDto(success = true, id = 1, username = "testuser", errors = null)
        coEvery { api.register(any()) } returns response

        val result = repository.register("u", "f", "l", "e", "p", "p")

        assertTrue(result is RegisterResult.Success)
    }

    @Test
    fun `register with validation errors returns Error with fieldErrors`() = runTest {
        val errors = mapOf("email" to listOf("Already exists"))
        val response = RegisterResponseDto(success = false, id = null, username = null, errors = errors)
        coEvery { api.register(any()) } returns response

        val result = repository.register("u", "f", "l", "e", "p", "p")

        assertTrue(result is RegisterResult.Error)
        assertEquals(errors, (result as RegisterResult.Error).fieldErrors)
    }

    @Test
    fun `register with network error returns NetworkError`() = runTest {
        coEvery { api.register(any()) } throws java.io.IOException()

        val result = repository.register("u", "f", "l", "e", "p", "p")

        assertTrue(result is RegisterResult.NetworkError)
    }
}
