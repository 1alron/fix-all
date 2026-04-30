package io.alron.fixall.data.remote.api

import io.alron.fixall.data.remote.dto.*
import okhttp3.MultipartBody
import retrofit2.http.*

interface AdminApi {
    @GET("/api/admin-panel/dashboard/full/")
    suspend fun getDashboardStats(): AdminDashboardDto

    @GET("/api/admin-panel/stats/statuses/")
    suspend fun getStatusStats(
        @Query("period") period: String,
        @Query("center_id") centerId: String? = null
    ): AdminStatusStatsDto

    @GET("/api/admin-panel/stats/attendance/")
    suspend fun getAttendanceStats(
        @Query("period") period: String,
        @Query("center_id") centerId: String? = null
    ): AdminAttendanceStatsDto

    @GET("/api/admin-panel/stats/center_visits/")
    suspend fun getBranchAttendanceStats(
        @Query("center_id") centerId: String,
        @Query("period") period: String
    ): BranchAttendanceStatsDto

    @GET("/api/admin-panel/stats/service_popularity/")
    suspend fun getServicePopularity(
        @Query("period") period: String,
        @Query("center_id") centerId: String? = null
    ): AdminServicePopularityDto

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

    @GET("/api/admin-panel/services/")
    suspend fun getServices(@QueryMap filters: Map<String, String>): List<AdminServiceItemDto>

    @GET("/api/admin-panel/services/unique/")
    suspend fun getUniqueServices(): List<String>

    @GET("/api/admin-panel/services/{id}/")
    suspend fun getServiceDetail(@Path("id") id: String): AdminServiceItemDto

    @POST("/api/admin-panel/services/")
    suspend fun createService(@Body request: CreateUpdateServiceRequestDto): AdminServiceItemDto

    @PUT("/api/admin-panel/services/{id}/")
    suspend fun updateService(
        @Path("id") id: String,
        @Body request: CreateUpdateServiceRequestDto
    ): AdminServiceItemDto

    @DELETE("/api/admin-panel/services/{id}/")
    suspend fun deleteService(@Path("id") id: String)

    @POST("/api/admin-panel/services/{id}/toggle_active/")
    suspend fun toggleServiceActive(@Path("id") id: String): ToggleActiveResponseDto

    @GET("/api/admin-panel/centers/")
    suspend fun getBranches(): List<AdminBranchListItemDto>

    @GET("/api/admin-panel/centers/{id}/")
    suspend fun getBranchDetail(@Path("id") id: String): AdminBranchListItemDto

    @POST("/api/admin-panel/centers/")
    suspend fun createBranch(@Body request: CreateUpdateBranchRequestDto): AdminBranchListItemDto

    @PUT("/api/admin-panel/centers/{id}/")
    suspend fun updateBranch(
        @Path("id") id: String,
        @Body request: CreateUpdateBranchRequestDto
    ): AdminBranchListItemDto

    @DELETE("/api/admin-panel/centers/{id}/")
    suspend fun deleteBranch(@Path("id") id: String)

    @Multipart
    @POST("/api/admin-panel/centers/{id}/update_photo/")
    suspend fun updateBranchPhoto(
        @Path("id") id: String,
        @Part photo: MultipartBody.Part
    ): UpdatePhotoResponseDto

    @GET("/api/admin-panel/centers/{id}/working_hours/")
    suspend fun getBranchWorkingHours(@Path("id") id: String): List<AdminWorkingHourDto>

    @POST("/api/admin-panel/centers/{id}/set_working_hours/")
    suspend fun setBranchWorkingHours(
        @Path("id") id: String,
        @Body request: SetWorkingHoursRequestDto
    ): SetWorkingHoursResponseDto
}
