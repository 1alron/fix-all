package io.alron.fixall.data.repository

import io.alron.fixall.data.remote.api.BranchesApi
import io.alron.fixall.data.remote.mappers.toDomain
import io.alron.fixall.domain.model.Branch
import io.alron.fixall.domain.repository.BranchesRepository

class BranchesRepositoryImpl(
    private val branchesApi: BranchesApi
) : BranchesRepository {
    override suspend fun getBranches(): Result<List<Branch>> {
        return try {
            val branchesDto = branchesApi.getBranches()
            Result.success(branchesDto.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getBranch(id: String): Result<Branch> {
        return try {
            val branchDto = branchesApi.getBranch(id)
            Result.success(branchDto.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}