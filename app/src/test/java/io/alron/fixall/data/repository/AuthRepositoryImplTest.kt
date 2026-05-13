package io.alron.fixall.data.repository

import com.google.gson.Gson
import io.alron.fixall.data.remote.api.AuthApi
import io.alron.fixall.data.remote.dto.LoginResponseDto
import io.alron.fixall.data.remote.dto.RefreshResponseDto
import io.alron.fixall.domain.model.LoginResult
import io.alron.fixall.domain.repository.TokenStorageRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import java.net.HttpURLConnection

class AuthRepositoryImplTest {

    private val api: AuthApi = mockk()
    private val tokenStorage: TokenStorageRepository = mockk(relaxed = true)
    private val gson = Gson()
    private lateinit var repository: AuthRepositoryImpl

    @Before
    fun setup() {
        repository = AuthRepositoryImpl(api, tokenStorage, gson)
    }

    @Test
    fun `login success saves tokens to storage`() = runTest {
        val response = LoginResponseDto(access = "access", refresh = "refresh")
        coEvery { api.login(any()) } returns response

        repository.login("user", "pass")

        coVerify { tokenStorage.saveTokens(any()) }
    }

    @Test
    fun `login success returns Success result`() = runTest {
        val response = LoginResponseDto(access = "access", refresh = "refresh")
        coEvery { api.login(any()) } returns response

        val result = repository.login("user", "pass")

        assertTrue(result is LoginResult.Success)
    }

    @Test
    fun `login with 401 error returns InvalidCredentials`() = runTest {
        val exception = mockk<HttpException> {
            every { code() } returns HttpURLConnection.HTTP_UNAUTHORIZED
        }
        coEvery { api.login(any()) } throws exception

        val result = repository.login("user", "pass")

        assertTrue(result is LoginResult.InvalidCredentials)
    }

    @Test
    fun `login with 500 error returns ServerError`() = runTest {
        val exception = mockk<HttpException> {
            every { code() } returns HttpURLConnection.HTTP_INTERNAL_ERROR
        }
        coEvery { api.login(any()) } throws exception

        val result = repository.login("user", "pass")

        assertTrue(result is LoginResult.ServerError)
    }

    @Test
    fun `refreshToken success updates storage`() = runTest {
        val response = RefreshResponseDto(access = "new_access")
        coEvery { api.refreshToken(any()) } returns response

        repository.refreshToken("old_refresh")

        coVerify { tokenStorage.saveTokens(match { it.access == "new_access" }) }
    }
}
