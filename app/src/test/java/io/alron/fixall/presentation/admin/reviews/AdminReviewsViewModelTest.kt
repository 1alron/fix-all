package io.alron.fixall.presentation.admin.reviews

import io.alron.fixall.data.remote.dto.AdminReviewListItemDto
import io.alron.fixall.domain.model.Branch
import io.alron.fixall.domain.repository.AdminRepository
import io.alron.fixall.domain.repository.BranchesRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AdminReviewsViewModelTest {

    private val adminRepository: AdminRepository = mockk()
    private val branchesRepository: BranchesRepository = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: AdminReviewsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { branchesRepository.getBranches() } returns Result.success(emptyList())
        coEvery { adminRepository.getReviews(any()) } returns Result.success(emptyList())
        viewModel = AdminReviewsViewModel(adminRepository, branchesRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads branches and reviews`() {
        val branches = listOf(mockk<Branch>(relaxed = true))
        val reviews = listOf(mockk<AdminReviewListItemDto>(relaxed = true))
        coEvery { branchesRepository.getBranches() } returns Result.success(branches)
        coEvery { adminRepository.getReviews(any()) } returns Result.success(reviews)

        val vm = AdminReviewsViewModel(adminRepository, branchesRepository)

        assertEquals(branches, vm.state.value.branches)
        assertEquals(reviews.size, vm.state.value.reviews.size)
    }

    @Test
    fun `onSearchChange updates state`() {
        viewModel.onSearchChange("John")
        assertEquals("John", viewModel.state.value.search)
    }

    @Test
    fun `onRatingChange updates state`() {
        viewModel.onRatingChange(5)
        assertEquals(5, viewModel.state.value.rating)
    }

    @Test
    fun `onUnansweredOnlyChange updates state`() {
        viewModel.onUnansweredOnlyChange(true)
        assertTrue(viewModel.state.value.unansweredOnly)
    }

    @Test
    fun `onCenterChange updates state`() {
        viewModel.onCenterChange("center_1")
        assertEquals("center_1", viewModel.state.value.centerId)
    }

    @Test
    fun `clearFilters resets state and reloads reviews`() = runTest {
        viewModel.onSearchChange("query")
        viewModel.onRatingChange(3)
        viewModel.onUnansweredOnlyChange(true)

        viewModel.clearFilters()

        assertEquals("", viewModel.state.value.search)
        assertNull(viewModel.state.value.rating)
        assertFalse(viewModel.state.value.unansweredOnly)
        coVerify { adminRepository.getReviews(any()) }
    }

    @Test
    fun `replyToReview success reloads reviews`() = runTest {
        coEvery { adminRepository.replyToReview(any(), any()) } returns Result.success(mockk())
        
        viewModel.replyToReview("r1", "Thank you")
        
        coVerify { adminRepository.getReviews(any()) }
    }

    @Test
    fun `deleteReview success reloads reviews`() = runTest {
        coEvery { adminRepository.deleteReview(any()) } returns Result.success(mockk())
        
        viewModel.deleteReview("r1")
        
        coVerify { adminRepository.getReviews(any()) }
    }

    @Test
    fun `refreshReviews updates isRefreshing state`() = runTest {
        coEvery { adminRepository.getReviews(any()) } returns Result.success(emptyList())
        
        viewModel.refreshReviews()
        
        assertFalse(viewModel.state.value.isRefreshing)
    }

    @Test
    fun `fetchReviews updates error state on failure`() = runTest {
        coEvery { adminRepository.getReviews(any()) } returns Result.failure(Exception("Fetch failed"))
        
        viewModel.loadReviews()
        
        assertEquals("Fetch failed", viewModel.state.value.error)
        assertFalse(viewModel.state.value.isLoading)
    }
}
