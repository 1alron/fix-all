package io.alron.fixall.data.remote.api

import io.alron.fixall.data.remote.dto.*
import retrofit2.http.*

interface AdminApi {
    @GET("/api/admin-panel/dashboard/full/")
    suspend fun getDashboardStats(): AdminDashboardDto

    @GET("/api/admin-panel/stats/statuses/")
    suspend fun getStatusStats(@Query("period") period: String): AdminStatusStatsDto

    @GET("/api/admin-panel/stats/attendance/")
    suspend fun getAttendanceStats(@Query("period") period: String): AdminAttendanceStatsDto

    @GET("/api/admin-panel/stats/service_popularity/")
    suspend fun getServicePopularity(@Query("period") period: String): AdminServicePopularityDto

    @GET("/api/admin-panel/appointments/")
    suspend fun getAppointments(
        @QueryMap filters: Map<String, String>
    ): List<AdminAppointmentListItemDto>

    @GET("/api/admin-panel/appointments/{id}/")
    suspend fun getAppointmentDetail(@Path("id") id: String): AdminAppointmentDetailDto

    @POST("/api/admin-panel/appointments/{id}/change_status/")
    suspend fun changeStatus(
        @Path("id") id: String,
        @Body request: ChangeStatusRequestDto
    )

    @POST("/api/admin-panel/appointments/{id}/add_note/")
    suspend fun addNote(
        @Path("id") id: String,
        @Body request: AddNoteRequestDto
    ): AddNoteResponseDto

    @GET("/api/admin-panel/reviews/")
    suspend fun getReviews(
        @QueryMap filters: Map<String, String>
    ): List<AdminReviewListItemDto>

    @POST("/api/admin-panel/reviews/{id}/reply/")
    suspend fun replyToReview(
        @Path("id") id: String,
        @Body request: AdminReplyRequestDto
    ): AdminReplyResponseDto

    @DELETE("/api/admin-panel/reviews/{id}/")
    suspend fun deleteReview(@Path("id") id: String): DeleteReviewResponseDto
}
