package io.alron.fixall.domain.model

data class AuthTokens(
    val access: String,
    val refresh: String
)