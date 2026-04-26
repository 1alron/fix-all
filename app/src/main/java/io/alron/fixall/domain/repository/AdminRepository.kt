package io.alron.fixall.domain.repository

import io.alron.fixall.domain.model.AdminDashboardStats

interface AdminRepository {
    suspend fun getDashboardStats(): Result<AdminDashboardStats>
}
