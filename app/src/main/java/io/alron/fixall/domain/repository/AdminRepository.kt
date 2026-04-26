package io.alron.fixall.domain.repository

import io.alron.fixall.domain.model.AdminDashboardStats
import io.alron.fixall.domain.model.AdminStatusStats

interface AdminRepository {
    suspend fun getDashboardStats(): Result<AdminDashboardStats>
    suspend fun getStatusStats(period: String): Result<AdminStatusStats>
}
