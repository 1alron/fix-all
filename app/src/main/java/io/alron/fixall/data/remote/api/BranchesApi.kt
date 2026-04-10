package io.alron.fixall.data.remote.api

import io.alron.fixall.data.remote.dto.BranchDto
import retrofit2.http.GET
import retrofit2.http.Path

interface BranchesApi {
    @GET("/api/service-centers/")
    suspend fun getBranches(): List<BranchDto>

    @GET("/api/service-centers/{id}/")
    suspend fun getBranch(@Path("id") id: String): BranchDto
}