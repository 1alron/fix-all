package io.alron.fixall.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AdminServiceListDto(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<AdminServiceItemDto>
)

data class AdminServiceItemDto(
    val id: String,
    val name: String,
    val description: String?,
    val duration: Int,
    val price: String,
    @SerializedName("service_center") val serviceCenterId: String,
    @SerializedName("center_address") val centerAddress: String?,
    @SerializedName("is_active") val isActive: Boolean
)

data class CreateUpdateServiceRequestDto(
    val name: String,
    val description: String?,
    val duration: Int,
    val price: String,
    @SerializedName("service_center") val serviceCenterId: String,
    @SerializedName("is_active") val isActive: Boolean
)

data class ToggleActiveResponseDto(
    val success: Boolean,
    @SerializedName("is_active") val isActive: Boolean,
    val message: String?
)
