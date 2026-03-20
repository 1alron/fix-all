package io.alron.fixall.data.remote.api

import io.alron.fixall.data.remote.dto.BranchDto
import retrofit2.http.GET

interface BranchesApi {
    @GET("/api/service-centers/")
    suspend fun getBranches(): List<BranchDto>
}