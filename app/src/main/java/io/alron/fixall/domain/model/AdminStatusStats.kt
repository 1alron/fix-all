package io.alron.fixall.domain.model

data class AdminStatusStats(
    val period: String,
    val statuses: Map<String, StatusInfo>,
    val total: Int
)

data class StatusInfo(
    val label: String,
    val count: Int
)
