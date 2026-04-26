package io.alron.fixall.data.remote.api

import io.alron.fixall.data.remote.dto.AdminDashboardDto
import retrofit2.http.GET

interface AdminApi {
    @GET("/api/admin-panel/dashboard/full/")
    suspend fun getDashboardStats(): AdminDashboardDto
}
