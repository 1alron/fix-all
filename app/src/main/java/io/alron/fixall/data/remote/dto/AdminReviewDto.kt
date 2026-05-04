package io.alron.fixall.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AdminReviewListDto(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<AdminReviewListItemDto>
)

data class AdminReviewListItemDto(
    val id: String,
    @SerializedName("user_name") val userName: String,
    @SerializedName("user_phone") val userPhone: String?,
    @SerializedName("center_address") val centerAddress: String,
    val rating: Int,
    val comment: String?,
    @SerializedName("admin_reply") val adminReply: String?,
    @SerializedName("admin_reply_at") val adminReplyAt: String?,
    @SerializedName("created_at") val createdAt: String
)

data class AdminReplyRequestDto(
    val reply: String
)

data class AdminReplyResponseDto(
    val success: Boolean,
    @SerializedName("admin_reply") val adminReply: String?,
    @SerializedName("admin_reply_at") val adminReplyAt: String?
)

data class DeleteReviewResponseDto(
    val success: Boolean,
    val message: String?
)
