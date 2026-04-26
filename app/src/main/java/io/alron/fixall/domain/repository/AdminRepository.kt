package io.alron.fixall.domain.repository

import io.alron.fixall.domain.model.AdminAttendanceStats
import io.alron.fixall.domain.model.AdminDashboardStats
import io.alron.fixall.domain.model.AdminStatusStats

interface AdminRepository {
    suspend fun getDashboardStats(): Result<AdminDashboardStats>
    suspend fun getStatusStats(period: String): Result<AdminStatusStats>
    suspend fun getAttendanceStats(period: String): Result<AdminAttendanceStats>
}
