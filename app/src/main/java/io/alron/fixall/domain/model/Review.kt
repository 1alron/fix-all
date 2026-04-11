package io.alron.fixall.domain.model

data class Review(
    val id: String,
    val userName: String,
    val userAvatar: String?,
    val rating: Int,
    val comment: String,
    val adminReply: String?,
    val adminReplyAt: String?,
    val createdAt: String
)