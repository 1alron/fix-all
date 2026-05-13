package io.alron.fixall.data.repository

import io.alron.fixall.data.remote.api.AdminApi
import io.alron.fixall.data.remote.dto.AdminReviewListItemDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class AdminReviewMappingTest {

    private val api: AdminApi = mockk()
    private lateinit var repository: AdminRepositoryImpl

    @Before
    fun setup() {
        repository = AdminRepositoryImpl(api)
    }

    @Test
    fun `getReviews maps admin reply and dates correctly`() = runTest {
        val dto = AdminReviewListItemDto(
            id = "rev-1",
            userName = "Customer",
            userPhone = "+7999",
            centerAddress = "Spb Office",
            rating = 5,
            comment = "Excellent!",
            adminReply = "Thank you!",
            adminReplyAt = "2024-05-21T10:00:00",
            createdAt = "2024-05-20T12:00:00"
        )
        coEvery { api.getReviews(any()) } returns listOf(dto)

        val result = repository.getReviews(emptyMap())
        val review = result.getOrNull()?.first()

        assertEquals("Excellent!", review?.comment)
        assertEquals("Thank you!", review?.adminReply)
        assertNotNull(review?.adminReplyAt)
    }
}
