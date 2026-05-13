package io.alron.fixall.data.repository

import com.google.gson.Gson
import io.alron.fixall.data.remote.api.AuthApi
import io.alron.fixall.data.remote.dto.RegisterResponseDto
import io.alron.fixall.domain.model.RegisterResult
import io.alron.fixall.domain.repository.TokenStorageRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class AuthRepositoryImplErrorTest {

    private val api: AuthApi = mockk()
    private val tokenStorage: TokenStorageRepository = mockk(relaxed = true)
    private val gson = Gson()
    private lateinit var repository: AuthRepositoryImpl

    @Before
    fun setup() {
        repository = AuthRepositoryImpl(api, tokenStorage, gson)
    }

    @Test
    fun `register handles 400 Bad Request with field errors`() = runTest {
        val errorJson = """
            {
                "success": false,
                "errors": {
                    "username": ["This field is required"],
                    "email": ["Enter a valid email"]
                }
            }
        """.trimIndent()
        
        val response = Response.error<RegisterResponseDto>(400, errorJson.toResponseBody(null))
        val exception = HttpException(response)
        
        coEvery { api.register(any()) } throws exception

        val result = repository.register("u", "f", "l", "e", "p", "p")

        assertTrue(result is RegisterResult.Error)
        val fieldErrors = (result as RegisterResult.Error).fieldErrors
        assertEquals("This field is required", fieldErrors?.get("username")?.first())
        assertEquals("Enter a valid email", fieldErrors?.get("email")?.first())
    }

    @Test
    fun `register handles 400 Bad Request with malformed json`() = runTest {
        val malformedJson = "{ invalid }"
        val response = Response.error<RegisterResponseDto>(400, malformedJson.toResponseBody(null))
        val exception = HttpException(response)
        
        coEvery { api.register(any()) } throws exception

        val result = repository.register("u", "f", "l", "e", "p", "p")

        assertTrue(result is RegisterResult.Error)
    }

    @Test
    fun `refreshToken returns null on 401 Unauthorized and clears storage`() = runTest {
        val exception = mockk<HttpException> {
            every { code() } returns 401
        }
        coEvery { api.refreshToken(any()) } throws exception

        val result = repository.refreshToken("invalid_refresh")

        assertTrue(result == null)
        io.mockk.verify { tokenStorage.clear() }
    }
}
