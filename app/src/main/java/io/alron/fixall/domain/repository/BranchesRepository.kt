package io.alron.fixall.domain.repository

import io.alron.fixall.domain.model.Branch

interface BranchesRepository {
    suspend fun getBranches(): List<Branch>
}