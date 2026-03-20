package io.alron.fixall.domain.model

sealed class LoginResult {
    data class Success(val tokens: AuthTokens) : LoginResult()

    object InvalidCredentials : LoginResult()
    object NetworkError : LoginResult()
    object ServerError : LoginResult()
    object UnknownError : LoginResult()
}