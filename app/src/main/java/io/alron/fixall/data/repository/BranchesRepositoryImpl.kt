package io.alron.fixall.data.repository

import io.alron.fixall.data.remote.api.BranchesApi
import io.alron.fixall.data.remote.mappers.toDomain
import io.alron.fixall.domain.model.Branch
import io.alron.fixall.domain.repository.BranchesRepository
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

class BranchesRepositoryImpl(
    private val branchesApi: BranchesApi
) : BranchesRepository {
    override suspend fun getBranches(): List<Branch> {
        val branchesDto = branchesApi.getBranches()
        val branchesDomain = branchesDto.map { it.toDomain() }
        return branchesDomain
    }
}