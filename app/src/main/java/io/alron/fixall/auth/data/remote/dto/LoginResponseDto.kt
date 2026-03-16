package io.alron.fixall.auth.data.remote.dto

data class LoginResponseDto(
    val access: String,
    val refresh: String
)