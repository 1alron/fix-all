package io.alron.fixall.presentation.admin.branches.add_edit

import androidx.lifecycle.SavedStateHandle
import io.alron.fixall.domain.model.AdminBranch
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
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class AdminAddEditBranchViewModelTest {

    private val repository: AdminRepository = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: AdminAddEditBranchViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { repository.getUniqueServiceNames() } returns Result.success(listOf("Diagnostic", "Wash"))
        coEvery { repository.getBranchDetail(any()) } returns Result.success(mockk(relaxed = true))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init in create mode loads unique service names and default working hours`() = runTest {
        val names = listOf("Oil Change", "Brake Fix")
        coEvery { repository.getUniqueServiceNames() } returns Result.success(names)

        viewModel = AdminAddEditBranchViewModel(repository, SavedStateHandle())

        assertEquals(names.size, viewModel.state.value.selectableServices.size)
        assertEquals("Oil Change", viewModel.state.value.selectableServices[0].name)
        assertEquals(7, viewModel.state.value.workingHours.size)
    }

    @Test
    fun `init in edit mode loads branch details`() = runTest {
        val branchId = "b123"
        val branch = AdminBranch(
            id = branchId,
            address = "Main St 1",
            phone = "12345",
            openingHours = "9-18",
            photo = "url",
            servicesCount = 5,
            workingHours = emptyList()
        )
        coEvery { repository.getBranchDetail(branchId) } returns Result.success(branch)

        viewModel = AdminAddEditBranchViewModel(repository, SavedStateHandle(mapOf("branchId" to branchId)))

        assertEquals("Main St 1", viewModel.state.value.address)
        assertEquals("12345", viewModel.state.value.phone)
        assertEquals("url", viewModel.state.value.photoUrl)
    }

    @Test
    fun `onAddressChange updates state`() {
        viewModel = AdminAddEditBranchViewModel(repository, SavedStateHandle())
        viewModel.onAddressChange("New Address")
        assertEquals("New Address", viewModel.state.value.address)
    }

    @Test
    fun `onServiceToggle updates selection state`() {
        viewModel = AdminAddEditBranchViewModel(repository, SavedStateHandle())
        assertFalse(viewModel.state.value.selectableServices[0].isSelected)
        
        viewModel.onServiceToggle(0, true)
        
        assertTrue(viewModel.state.value.selectableServices[0].isSelected)
    }

    @Test
    fun `save in create mode calls createBranch and other setup methods`() = runTest {
        val branchId = "new_b_id"
        viewModel = AdminAddEditBranchViewModel(repository, SavedStateHandle())
        viewModel.onAddressChange("Address")
        viewModel.onPhoneChange("Phone")
        viewModel.onOpeningHoursChange("Hours")
        
        coEvery { repository.createBranch(any(), any(), any()) } returns Result.success(mockk { coEvery { id } returns branchId })
        coEvery { repository.setBranchWorkingHours(any(), any()) } returns Result.success(mockk())
        
        viewModel.save()

        coVerify { repository.createBranch("Address", "Phone", "Hours") }
        assertTrue(viewModel.state.value.isSuccess)
        assertFalse(viewModel.state.value.isSaving)
    }

    @Test
    fun `save with photo calls updateBranchPhoto`() = runTest {
        val branchId = "b123"
        val photoFile = mockk<File>()
        viewModel = AdminAddEditBranchViewModel(repository, SavedStateHandle())
        viewModel.onPhotoSelected(photoFile)
        
        coEvery { repository.createBranch(any(), any(), any()) } returns Result.success(mockk { coEvery { id } returns branchId })
        coEvery { repository.updateBranchPhoto(branchId, photoFile) } returns Result.success("new_url")
        coEvery { repository.setBranchWorkingHours(any(), any()) } returns Result.success(mockk())

        viewModel.save()

        coVerify { repository.updateBranchPhoto(branchId, photoFile) }
    }

    @Test
    fun `save updates error state on failure`() = runTest {
        viewModel = AdminAddEditBranchViewModel(repository, SavedStateHandle())
        coEvery { repository.createBranch(any(), any(), any()) } returns Result.failure(Exception("Creation failed"))

        viewModel.save()

        assertEquals("Creation failed", viewModel.state.value.error)
        assertFalse(viewModel.state.value.isSuccess)
    }
}
