package io.alron.fixall.data.remote.dto

data class AdminAttendanceStatsDto(
    val period: String,
    val centers: List<CenterAttendanceDto>,
    val daily: List<AdminDailyAttendanceDto>? = null
)

data class CenterAttendanceDto(
    val id: String,
    val address: String,
    val count: Int
)

data class AdminDailyAttendanceDto(
    val date: String,
    val label: String,
    val count: Int
)
