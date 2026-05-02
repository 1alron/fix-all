package io.alron.fixall.domain.model

data class AdminReviewListItem(
    val id: String,
    val userName: String,
    val userPhone: String?,
    val centerAddress: String,
    val rating: Int,
    val comment: String?,
    val adminReply: String?,
    val adminReplyAt: String?,
    val createdAt: String
)
