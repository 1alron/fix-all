package io.alron.fixall.domain.repository

import io.alron.fixall.domain.model.Branch
import io.alron.fixall.domain.model.Review
import kotlinx.coroutines.flow.Flow

interface BranchesRepository {
    val currentBranch: Flow<Branch?>
    
    suspend fun getBranches(): Result<List<Branch>>
    suspend fun getBranch(id: String): Result<Branch>
    suspend fun getReviews(id: String, page: Int = 1): Result<List<Review>>
    suspend fun addReview(id: String, rating: Int, comment: String): Result<Review>
}
