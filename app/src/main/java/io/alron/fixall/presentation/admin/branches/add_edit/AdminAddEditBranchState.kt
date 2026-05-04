package io.alron.fixall.presentation.admin.branches.add_edit

import io.alron.fixall.domain.model.AdminWorkingHour
import java.io.File

data class AdminAddEditBranchState(
    val id: String? = null,
    val address: String = "",
    val phone: String = "",
    val openingHours: String = "",
    val photoUrl: String? = null,
    val selectedPhotoFile: File? = null,
    
    val workingHours: List<AdminWorkingHour> = emptyList(),
    val selectableServices: List<SelectableService> = emptyList(),
    
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

data class SelectableService(
    val id: String = "",
    val name: String,
    val price: String = "1000",
    val duration: Int = 60,
    val isSelected: Boolean = false
)
