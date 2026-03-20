package io.alron.fixall.data.remote.dto

data class RegisterRequestDto(
    val username: String,
    val first_name: String,
    val last_name: String,
    val email: String,
    val password1: String,
    val password2: String
)