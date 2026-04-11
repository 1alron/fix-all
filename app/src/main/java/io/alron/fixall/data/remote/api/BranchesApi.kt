package io.alron.fixall.data.remote.api

import io.alron.fixall.data.remote.dto.*
import retrofit2.http.*

interface BranchesApi {
    @GET("/api/service-centers/")
    suspend fun getBranches(): List<BranchDto>

    @GET("/api/service-centers/{id}/")
    suspend fun getBranch(@Path("id") id: String): BranchDto

    @GET("/api/service-centers/{id}/reviews/")
    suspend fun getReviews(
        @Path("id") id: String,
        @Query("page") page: Int = 1
    ): List<ReviewDto>

    @POST("/api/service-centers/{id}/add_review/")
    suspend fun addReview(
        @Path("id") id: String,
        @Body request: AddReviewRequestDto
    ): AddReviewResponseDto
}
