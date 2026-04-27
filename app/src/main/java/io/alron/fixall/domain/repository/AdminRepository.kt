package io.alron.fixall.domain.repository

import io.alron.fixall.data.remote.dto.*
import io.alron.fixall.domain.model.*

interface AdminRepository {
    suspend fun getDashboardStats(): Result<AdminDashboardStats>
    suspend fun getStatusStats(period: String): Result<AdminStatusStats>
    suspend fun getAttendanceStats(period: String): Result<AdminAttendanceStats>
    suspend fun getServicePopularity(period: String): Result<AdminServicePopularity>
    suspend fun getAppointments(filters: Map<String, String>): Result<List<AdminAppointmentListItem>>
    suspend fun getAppointmentDetail(id: String): Result<AdminAppointmentDetail>
    suspend fun changeStatus(id: String, status: String): Result<Unit>
    suspend fun addNote(id: String, note: String): Result<AddNoteResponse>
    suspend fun getReviews(filters: Map<String, String>): Result<List<AdminReviewListItemDto>>
    suspend fun replyToReview(id: String, reply: String): Result<AdminReplyResponseDto>
    suspend fun deleteReview(id: String): Result<DeleteReviewResponseDto>
    suspend fun getServices(filters: Map<String, String>): Result<List<AdminService>>
    suspend fun getServiceDetail(id: String): Result<AdminService>
    suspend fun createService(request: CreateUpdateServiceRequestDto): Result<AdminService>
    suspend fun updateService(id: String, request: CreateUpdateServiceRequestDto): Result<AdminService>
    suspend fun deleteService(id: String): Result<Unit>
    suspend fun toggleServiceActive(id: String): Result<ToggleActiveResponseDto>
}
