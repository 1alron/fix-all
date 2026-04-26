package io.alron.fixall.domain.model

data class AdminAttendanceStats(
    val period: String,
    val centers: List<CenterAttendance>
)

data class CenterAttendance(
    val id: String,
    val address: String,
    val count: Int
)
