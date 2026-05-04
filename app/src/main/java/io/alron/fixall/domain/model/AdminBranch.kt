package io.alron.fixall.domain.model

data class AdminBranch(
    val id: String,
    val address: String,
    val phone: String,
    val openingHours: String,
    val photo: String?,
    val servicesCount: Int,
    val workingHours: List<AdminWorkingHour> = emptyList()
)

data class AdminWorkingHour(
    val id: Int?,
    val dayOfWeek: Int,
    val dayDisplay: String?,
    val startTime: String,
    val endTime: String,
    val lunchStart: String?,
    val lunchEnd: String?,
    val isWorking: Boolean
)
