package io.alron.fixall.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CarBrandDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String
)