package io.alron.fixall.auth.data.remote.dto

data class RegisterResponseDto(
    val success: Boolean,
    val id: Int?,
    val username: String?,
    val errors: Map<String, List<String>>?
)