package io.alron.fixall.presentation.admin.branches.add_edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.alron.fixall.data.remote.dto.CreateUpdateServiceRequestDto
import io.alron.fixall.domain.model.AdminWorkingHour
import io.alron.fixall.domain.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AdminAddEditBranchViewModel @Inject constructor(
    private val repository: AdminRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val branchId: String? = savedStateHandle["branchId"]

    private val _state = MutableStateFlow(AdminAddEditBranchState(id = branchId))
    val state = _state.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            if (branchId != null) {
                repository.getBranchDetail(branchId).onSuccess { branch ->
                    _state.update { currentState ->
                        currentState.copy(
                            address = branch.address,
                            phone = branch.phone,
                            openingHours = branch.openingHours,
                            photoUrl = branch.photo,
                            workingHours = branch.workingHours
                                .map { wh ->
                                    val day = wh.dayOfWeek
                                    wh.copy(
                                        dayOfWeek = day,
                                        dayDisplay = getDayName(day),
                                        startTime = formatTimeForUi(wh.startTime),
                                        endTime = formatTimeForUi(wh.endTime),
                                        lunchStart = wh.lunchStart?.let { formatTimeForUi(it) },
                                        lunchEnd = wh.lunchEnd?.let { formatTimeForUi(it) }
                                    )
                                }
                                .sortedBy { wh -> wh.dayOfWeek }
                        )
                    }
                }
            } else {
                val names = repository.getUniqueServiceNames().getOrDefault(emptyList())
                val selectable = names.map { SelectableService(name = it) }
                
                val defaults = (1..7).map { day ->
                    AdminWorkingHour(
                        id = null,
                        dayOfWeek = day,
                        dayDisplay = getDayName(day),
                        startTime = if (day == 7) "00:00" else "09:00",
                        endTime = if (day == 7) "00:01" else "18:00",
                        lunchStart = if (day == 7) null else "13:00",
                        lunchEnd = if (day == 7) null else "14:00",
                        isWorking = day != 7
                    )
                }
                _state.update { it.copy(workingHours = defaults, selectableServices = selectable) }
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun getDayName(day: Int): String = when (day) {
        1 -> "Понедельник"
        2 -> "Вторник"
        3 -> "Среда"
        4 -> "Четверг"
        5 -> "Пятница"
        6 -> "Суббота"
        7 -> "Воскресенье"
        else -> "День $day"
    }

    private fun formatTimeForUi(time: String?): String {
        if (time == null || time.isBlank() || time == "null") return "00:00"
        val cleaned = time.trim()
        val parts = cleaned.split(":")
        val h = parts.getOrNull(0)?.padStart(2, '0') ?: "00"
        val m = parts.getOrNull(1)?.padStart(2, '0') ?: "00"
        return "${h.take(2)}:${m.take(2)}"
    }

    private fun normalizeTime(time: String?): String? {
        if (time == null || time.isBlank() || time == "—" || time == "null") return null
        val cleaned = time.trim()
        val parts = cleaned.split(":")
        val h = parts.getOrNull(0)?.padStart(2, '0') ?: "00"
        val m = parts.getOrNull(1)?.padStart(2, '0') ?: "00"
        return "${h.take(2)}:${m.take(2)}"
    }

    fun onAddressChange(value: String) = _state.update { it.copy(address = value) }
    fun onPhoneChange(value: String) = _state.update { it.copy(phone = value) }
    fun onOpeningHoursChange(value: String) = _state.update { it.copy(openingHours = value) }
    fun onPhotoSelected(file: File) = _state.update { it.copy(selectedPhotoFile = file) }

    fun onWorkingHourChange(index: Int, updated: AdminWorkingHour) {
        val list = _state.value.workingHours.toMutableList()
        list[index] = updated
        _state.update { it.copy(workingHours = list) }
    }

    fun onServiceToggle(index: Int, isSelected: Boolean) {
        val list = _state.value.selectableServices.toMutableList()
        list[index] = list[index].copy(isSelected = isSelected)
        _state.update { it.copy(selectableServices = list) }
    }

    fun onServicePriceChange(index: Int, price: String) {
        val list = _state.value.selectableServices.toMutableList()
        list[index] = list[index].copy(price = price)
        _state.update { it.copy(selectableServices = list) }
    }

    fun onServiceDurationChange(index: Int, duration: Int) {
        val list = _state.value.selectableServices.toMutableList()
        list[index] = list[index].copy(duration = duration)
        _state.update { it.copy(selectableServices = list) }
    }

    fun save() {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }
            try {
                val currentState = _state.value
                
                val branchResult = if (branchId == null) {
                    repository.createBranch(currentState.address, currentState.phone, currentState.openingHours)
                } else {
                    repository.updateBranch(branchId, currentState.address, currentState.phone, currentState.openingHours)
                }
                
                val branch = branchResult.getOrThrow()
                val id = branch.id
                
                currentState.selectedPhotoFile?.let {
                    repository.updateBranchPhoto(id, it).getOrThrow()
                }
                
                currentState.workingHours.forEach { wh ->
                    val apiDay = wh.dayOfWeek
                    
                    val normalizedWh = if (!wh.isWorking) {
                        wh.copy(
                            dayOfWeek = apiDay,
                            startTime = "00:00",
                            endTime = "00:01",
                            lunchStart = null,
                            lunchEnd = null
                        )
                    } else {
                        val sTime = normalizeTime(wh.startTime) ?: "09:00"
                        val eTime = normalizeTime(wh.endTime) ?: (if (apiDay == 7) "00:01" else "18:00")
                        val lStart = normalizeTime(wh.lunchStart)
                        val lEnd = normalizeTime(wh.lunchEnd)
                        
                        val finalLStart = if (lStart != null && lEnd != null) lStart else null
                        val finalLEnd = if (lStart != null && lEnd != null) lEnd else null

                        wh.copy(
                            dayOfWeek = apiDay,
                            startTime = sTime,
                            endTime = eTime,
                            lunchStart = finalLStart,
                            lunchEnd = finalLEnd
                        )
                    }
                    
                    repository.setBranchWorkingHours(id, normalizedWh).getOrThrow()
                }
                
                if (branchId == null) {
                    currentState.selectableServices.filter { it.isSelected }.forEach { service ->
                        val request = CreateUpdateServiceRequestDto(
                            name = service.name,
                            description = "",
                            duration = service.duration,
                            price = service.price,
                            serviceCenterId = id,
                            isActive = true
                        )
                        repository.createService(request).getOrThrow()
                    }
                }
                
                _state.update { it.copy(isSaving = false, isSuccess = true) }
            } catch (e: Throwable) {
                _state.update { it.copy(isSaving = false, error = e.message ?: "Ошибка при сохранении") }
            }
        }
    }
}
