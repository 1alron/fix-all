package io.alron.fixall.data.remote.dto

data class AdminAttendanceStatsDto(
    val period: String,
    val centers: List<CenterAttendanceDto>
)

data class CenterAttendanceDto(
    val id: String,
    val address: String,
    val count: Int
)
