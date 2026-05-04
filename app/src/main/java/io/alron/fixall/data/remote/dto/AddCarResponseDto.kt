package io.alron.fixall.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AddCarResponseDto(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: CarDto?,
    @SerializedName("errors") val errors: Map<String, Any>?
)