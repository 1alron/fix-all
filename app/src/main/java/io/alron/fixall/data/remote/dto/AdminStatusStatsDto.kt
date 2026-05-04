package io.alron.fixall.data.remote.dto

data class AdminStatusStatsDto(
    val period: String,
    val statuses: Map<String, StatusInfoDto>,
    val total: Int
)

data class StatusInfoDto(
    val label: String,
    val count: Int
)
