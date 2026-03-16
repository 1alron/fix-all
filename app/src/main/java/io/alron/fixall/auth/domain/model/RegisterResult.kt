package io.alron.fixall.auth.domain.model

sealed class RegisterResult {
    object Success : RegisterResult()
    data class Error(
        val message: String? = null,
        val fieldErrors: Map<String, List<String>>? = null
    ) : RegisterResult()

    object NetworkError : RegisterResult()
    object ServerError : RegisterResult()
    object UnknownError : RegisterResult()
}