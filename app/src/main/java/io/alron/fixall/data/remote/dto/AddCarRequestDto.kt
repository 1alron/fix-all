package io.alron.fixall.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AddCarRequestDto(
    @SerializedName("model_id") val modelId: String,
    @SerializedName("year") val year: Int,
    @SerializedName("license_plate") val licensePlate: String,
    @SerializedName("vin") val vin: String?
)