package io.alron.fixall.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CarModelDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("brand") val brandId: String,
    @SerializedName("brand_name") val brandName: String
)