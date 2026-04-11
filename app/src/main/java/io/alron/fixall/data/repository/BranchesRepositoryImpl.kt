package io.alron.fixall.data.repository

import io.alron.fixall.data.remote.api.BranchesApi
import io.alron.fixall.data.remote.dto.AddReviewRequestDto
import io.alron.fixall.data.remote.mappers.toDomain
import io.alron.fixall.domain.model.Branch
import io.alron.fixall.domain.model.Review
import io.alron.fixall.domain.repository.BranchesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject

class BranchesRepositoryImpl @Inject constructor(
    private val branchesApi: BranchesApi
) : BranchesRepository {

    private val _currentBranch = MutableSharedFlow<Branch?>(replay = 1)
    override val currentBranch: Flow<Branch?> = _currentBranch

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
            val domain = branchDto.toDomain()
            _currentBranch.emit(domain)
            Result.success(domain)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getReviews(id: String, page: Int): Result<List<Review>> {
        return try {
            val response = branchesApi.getReviews(id, page)
            Result.success(response.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addReview(id: String, rating: Int, comment: String): Result<Review> {
        return try {
            val response = branchesApi.addReview(id, AddReviewRequestDto(rating, comment))
            if (response.success && response.data != null) {
                getBranch(id)
                Result.success(response.data.toDomain())
            } else {
                Result.failure(Exception(response.error ?: "Failed to add review"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
