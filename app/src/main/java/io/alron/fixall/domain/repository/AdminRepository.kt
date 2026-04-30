package io.alron.fixall.domain.repository

import io.alron.fixall.data.remote.dto.*
import io.alron.fixall.domain.model.*
import java.io.File

interface AdminRepository {
    suspend fun getDashboardStats(): Result<AdminDashboardStats>
    suspend fun getStatusStats(period: String, centerId: String? = null): Result<AdminStatusStats>
    suspend fun getAttendanceStats(period: String, centerId: String? = null): Result<AdminAttendanceStats>
    suspend fun getBranchAttendanceStats(centerId: String, period: String): Result<BranchAttendanceStats>
    suspend fun getServicePopularity(period: String, centerId: String? = null): Result<AdminServicePopularity>
    suspend fun getAppointments(filters: Map<String, String>): Result<List<AdminAppointmentListItem>>
    suspend fun getAppointmentDetail(id: String): Result<AdminAppointmentDetail>
    suspend fun changeStatus(id: String, status: String): Result<Unit>
    suspend fun addNote(id: String, note: String): Result<AddNoteResponse>
    suspend fun getReviews(filters: Map<String, String>): Result<List<AdminReviewListItemDto>>
    suspend fun replyToReview(id: String, reply: String): Result<AdminReplyResponseDto>
    suspend fun deleteReview(id: String): Result<DeleteReviewResponseDto>
    suspend fun getServices(filters: Map<String, String>): Result<List<AdminService>>
    suspend fun getUniqueServiceNames(): Result<List<String>>
    suspend fun getServiceDetail(id: String): Result<AdminService>
    suspend fun createService(request: CreateUpdateServiceRequestDto): Result<AdminService>
    suspend fun updateService(id: String, request: CreateUpdateServiceRequestDto): Result<AdminService>
    suspend fun deleteService(id: String): Result<Unit>
    suspend fun toggleServiceActive(id: String): Result<ToggleActiveResponseDto>
    suspend fun getBranches(): Result<List<AdminBranch>>
    suspend fun getBranchDetail(id: String): Result<AdminBranch>
    suspend fun createBranch(address: String, phone: String, openingHours: String): Result<AdminBranch>
    suspend fun updateBranch(id: String, address: String, phone: String, openingHours: String): Result<AdminBranch>
    suspend fun deleteBranch(id: String): Result<Unit>
    suspend fun updateBranchPhoto(id: String, photoFile: File): Result<String>
    suspend fun getBranchWorkingHours(id: String): Result<List<AdminWorkingHour>>
    suspend fun setBranchWorkingHours(id: String, workingHour: AdminWorkingHour): Result<AdminWorkingHour>
}
