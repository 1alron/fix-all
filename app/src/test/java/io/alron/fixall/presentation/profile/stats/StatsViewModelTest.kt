package io.alron.fixall.presentation.profile.stats

import io.alron.fixall.domain.model.UserStats
import io.alron.fixall.domain.repository.ProfileRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class StatsViewModelTest {

    private val repository: ProfileRepository = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: StatsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { repository.getStats() } returns Result.success(mockk(relaxed = true))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init calls loadStats and updates state on success`() = runTest {
        val stats = mockk<UserStats>(relaxed = true)
        coEvery { repository.getStats() } returns Result.success(stats)

        viewModel = StatsViewModel(repository)

        assertEquals(stats, viewModel.state.value.stats)
        assertFalse(viewModel.state.value.isLoading)
        assertNull(viewModel.state.value.errorMessage)
    }

    @Test
    fun `loadStats updates error message on failure`() = runTest {
        val error = "Failed to load stats"
        coEvery { repository.getStats() } returns Result.failure(Exception(error))

        viewModel = StatsViewModel(repository)
        viewModel.loadStats()

        assertEquals(error, viewModel.state.value.errorMessage)
        assertFalse(viewModel.state.value.isLoading)
    }
}
