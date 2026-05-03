package io.alron.fixall.data.remote.dto

import com.google.gson.annotations.SerializedName

data class BranchAttendanceStatsDto(
    @SerializedName("center_id") val centerId: String,
    val period: String,
    val labels: List<String>,
    val data: List<Int>
)
