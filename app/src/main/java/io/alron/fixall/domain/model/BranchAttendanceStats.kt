package io.alron.fixall.domain.model

data class BranchAttendanceStats(
    val centerId: String,
    val period: String,
    val labels: List<String>,
    val data: List<Int>
)
