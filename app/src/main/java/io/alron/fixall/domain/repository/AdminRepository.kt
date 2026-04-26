package io.alron.fixall.domain.repository

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
}
