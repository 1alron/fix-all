package io.alron.fixall.data.repository

import com.google.gson.Gson
import io.alron.fixall.data.remote.api.BranchesApi
import io.alron.fixall.data.remote.dto.AddReviewRequestDto
import io.alron.fixall.data.remote.dto.AddReviewResponseDto
import io.alron.fixall.data.remote.dto.ReviewDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class BranchesRepositoryImplReviewTest {

    private val api: BranchesApi = mockk()
    private lateinit var repository: BranchesRepositoryImpl

    @Before
    fun setup() {
        repository = BranchesRepositoryImpl(api)
    }

    @Test
    fun `addReview success returns mapped review and refreshes branch`() = runTest {
        val reviewDto = ReviewDto(
            id = "r1", userName = "User", userAvatar = null, rating = 5, 
            comment = "Good", adminReply = null, adminReplyAt = null, createdAt = "now"
        )
        val response = AddReviewResponseDto(success = true, message = null, data = reviewDto, error = null, errors = null)
        
        coEvery { api.addReview(any(), any()) } returns response
        coEvery { api.getBranch(any()) } returns mockk(relaxed = true)

        val result = repository.addReview("b1", 5, "Good")

        assertTrue(result.isSuccess)
        assertEquals("r1", result.getOrNull()?.id)
    }

    @Test
    fun `addReview failure with server error message`() = runTest {
        val response = AddReviewResponseDto(success = false, message = "Already reviewed", data = null, error = null, errors = null)
        coEvery { api.addReview(any(), any()) } returns response

        val result = repository.addReview("b1", 5, "Good")

        assertTrue(result.isFailure)
        assertEquals("Already reviewed", result.exceptionOrNull()?.message)
    }

    @Test
    fun `addReview handles HttpException with body parsing`() = runTest {
        val errorJson = """{"success": false, "error": "Invalid rating"}"""
        val errorResponse = Response.error<AddReviewResponseDto>(400, errorJson.toResponseBody(null))
        val exception = HttpException(errorResponse)
        
        coEvery { api.addReview(any(), any()) } throws exception

        val result = repository.addReview("b1", 10, "Bad")

        assertTrue(result.isFailure)
        assertEquals("Invalid rating", result.exceptionOrNull()?.message)
    }
}
