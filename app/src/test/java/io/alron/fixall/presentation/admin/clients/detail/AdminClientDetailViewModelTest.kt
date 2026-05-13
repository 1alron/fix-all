package io.alron.fixall.presentation.admin.clients.detail

import androidx.lifecycle.SavedStateHandle
import io.alron.fixall.domain.model.AdminClientDetail
import io.alron.fixall.domain.repository.AdminRepository
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
class AdminClientDetailViewModelTest {

    private val repository: AdminRepository = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()
    private val clientId = 123
    private lateinit var viewModel: AdminClientDetailViewModel

    private val sampleDetail = AdminClientDetail(
        id = clientId,
        username = "test_user",
        fullName = "John Doe",
        email = "test@test.com",
        phone = "1234567890",
        address = "Street 1",
        dateJoined = "2024-01-01",
        isStaff = false,
        carsCount = 1,
        appointmentsCount = 5,
        totalPaid = 1000.0,
        cars = emptyList(),
        recentAppointments = emptyList(),
        topServices = emptyList(),
        topCenters = emptyList(),
        weekdayCounts = emptyList(),
        hourCounts = emptyList()
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { repository.getClientDetail(clientId) } returns Result.success(sampleDetail)
        
        val savedStateHandle = SavedStateHandle(mapOf("clientId" to clientId))
        viewModel = AdminClientDetailViewModel(repository, savedStateHandle)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads client details and populates edit fields`() {
        assertEquals(sampleDetail, viewModel.state.value.client)
        assertEquals(sampleDetail.username, viewModel.state.value.editUsername)
        assertEquals("John", viewModel.state.value.editFirstName)
        assertEquals("Doe", viewModel.state.value.editLastName)
    }

    @Test
    fun `toggleEditMode changes isEditMode state`() {
        assertFalse(viewModel.state.value.isEditMode)
        viewModel.toggleEditMode()
        assertTrue(viewModel.state.value.isEditMode)
        viewModel.toggleEditMode()
        assertFalse(viewModel.state.value.isEditMode)
    }

    @Test
    fun `updateClient success exits edit mode and reloads`() = runTest {
        coEvery { repository.updateClient(clientId, any()) } returns Result.success(Unit)
        viewModel.toggleEditMode()
        
        viewModel.updateClient()

        assertFalse(viewModel.state.value.isEditMode)
        coVerify(atLeast = 1) { repository.getClientDetail(clientId) }
    }

    @Test
    fun `deleteClient calls repository and notifies success`() = runTest {
        coEvery { repository.deleteClient(clientId) } returns Result.success(Unit)
        var successCalled = false

        viewModel.deleteClient { successCalled = true }

        assertTrue(successCalled)
        coVerify { repository.deleteClient(clientId) }
    }

    @Test
    fun `onUsernameChange updates edit state`() {
        val newUsername = "changed_name"
        viewModel.onUsernameChange(newUsername)
        assertEquals(newUsername, viewModel.state.value.editUsername)
    }
}
