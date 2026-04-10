package io.alron.fixall.domain.repository

import io.alron.fixall.domain.model.Branch

interface BranchesRepository {
    suspend fun getBranches(): Result<List<Branch>>
    suspend fun getBranch(id: String): Result<Branch>
}