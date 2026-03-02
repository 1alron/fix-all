package io.alron.fixall.auth.domain.model

data class AuthTokens(
    val access: String,
    val refresh: String
)