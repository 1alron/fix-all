package io.alron.fixall.data.remote.api

import io.alron.fixall.data.remote.dto.AdminDashboardDto
import io.alron.fixall.data.remote.dto.AdminStatusStatsDto
import retrofit2.http.GET
import retrofit2.http.Query

interface AdminApi {
    @GET("/api/admin-panel/dashboard/full/")
    suspend fun getDashboardStats(): AdminDashboardDto

    @GET("/api/admin-panel/stats/statuses/")
    suspend fun getStatusStats(@Query("period") period: String): AdminStatusStatsDto
}
