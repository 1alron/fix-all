package io.alron.fixall.domain.model

data class AdminAttendanceStats(
    val period: String,
    val centers: List<CenterAttendance>,
    val daily: List<AdminDailyAttendance>? = null
)

data class CenterAttendance(
    val id: String,
    val address: String,
    val count: Int
)

data class AdminDailyAttendance(
    val date: String,
    val label: String,
    val count: Int
)
