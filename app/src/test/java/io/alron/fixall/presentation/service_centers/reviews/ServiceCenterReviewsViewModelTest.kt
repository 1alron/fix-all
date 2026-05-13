package io.alron.fixall.presentation.service_centers.reviews

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import io.alron.fixall.domain.model.Appointment
import io.alron.fixall.domain.model.Review
import io.alron.fixall.domain.model.User
import io.alron.fixall.domain.repository.AppointmentsRepository
import io.alron.fixall.domain.repository.BranchesRepository
import io.alron.fixall.domain.repository.ProfileRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
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
class ServiceCenterReviewsViewModelTest {

    private val branchesRepository: BranchesRepository = mockk()
    private val appointmentsRepository: AppointmentsRepository = mockk()
    private val profileRepository: ProfileRepository = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()
    private val branchId = "branch_123"
    private lateinit var viewModel: ServiceCenterReviewsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        coEvery { profileRepository.getMe() } returns Result.success(mockk(relaxed = true))
        coEvery { appointmentsRepository.getAppointments() } returns Result.success(emptyList())
        coEvery { branchesRepository.getReviews(branchId, any()) } returns Result.success(emptyList())
        
        val savedStateHandle = SavedStateHandle(mapOf("id" to branchId))
        viewModel = ServiceCenterReviewsViewModel(
            branchesRepository, 
            appointmentsRepository, 
            profileRepository, 
            savedStateHandle
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init checks eligibility and loads reviews`() = runTest {
        val user = mockk<User>(relaxed = true) { every { username } returns "user1" }
        val appointments = listOf(mockk<Appointment>(relaxed = true) {
            every { serviceCenter.id } returns branchId
            every { status } returns "COMPLETED"
        })
        val reviews = listOf(mockk<Review>(relaxed = true))

        coEvery { profileRepository.getMe() } returns Result.success(user)
        coEvery { appointmentsRepository.getAppointments() } returns Result.success(appointments)
        coEvery { branchesRepository.getReviews(branchId, 1) } returns Result.success(reviews)

        val savedStateHandle = SavedStateHandle(mapOf("id" to branchId))
        val vm = ServiceCenterReviewsViewModel(branchesRepository, appointmentsRepository, profileRepository, savedStateHandle)

        assertTrue(vm.state.value.hasCompletedAppointment)
        assertEquals("user1", vm.state.value.currentUserId)
        assertEquals(reviews, vm.state.value.reviews)
        assertFalse(vm.state.value.isLoading)
    }

    @Test
    fun `loadReviews with refresh resets page and clears reviews`() = runTest {
        val initialReviews = listOf(mockk<Review>(relaxed = true))
        val newReviews = listOf(mockk<Review>(relaxed = true))
        
        coEvery { branchesRepository.getReviews(branchId, 1) } returns Result.success(initialReviews)
        viewModel.loadReviews()
        assertEquals(initialReviews, viewModel.state.value.reviews)

        coEvery { branchesRepository.getReviews(branchId, 1) } returns Result.success(newReviews)
        viewModel.loadReviews(refresh = true)

        assertEquals(newReviews, viewModel.state.value.reviews)
        assertEquals(1, viewModel.state.value.currentPage)
    }

    @Test
    fun `loadNextPage increments page and fetches more reviews`() = runTest {
        val firstPage = listOf(mockk<Review>(relaxed = true))
        val secondPage = listOf(mockk<Review>(relaxed = true))
        
        coEvery { branchesRepository.getReviews(branchId, 1) } returns Result.success(firstPage)
        coEvery { branchesRepository.getReviews(branchId, 2) } returns Result.success(secondPage)

        viewModel.loadReviews()
        viewModel.loadNextPage()

        assertEquals(2, viewModel.state.value.currentPage)
        assertEquals(firstPage + secondPage, viewModel.state.value.reviews)
    }

    @Test
    fun `loadReviews handles failure and sets error message`() = runTest {
        coEvery { branchesRepository.getReviews(branchId, any()) } returns Result.failure(Exception("Failed"))

        viewModel.loadReviews()

        assertEquals("Failed", viewModel.state.value.errorMessage)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `addReview does nothing if already reviewed`() = runTest {
        val existingReview = mockk<Review>(relaxed = true) { every { userName } returns "user1" }
        coEvery { branchesRepository.getReviews(branchId, 1) } returns Result.success(listOf(existingReview))
        
        val user = mockk<User>(relaxed = true) { every { username } returns "user1" }
        coEvery { profileRepository.getMe() } returns Result.success(user)

        val savedStateHandle = SavedStateHandle(mapOf("id" to branchId))
        val vm = ServiceCenterReviewsViewModel(branchesRepository, appointmentsRepository, profileRepository, savedStateHandle)

        vm.addReview(5, "Should not be called")

        coVerify(exactly = 0) { branchesRepository.addReview(any(), any(), any()) }
    }
}
